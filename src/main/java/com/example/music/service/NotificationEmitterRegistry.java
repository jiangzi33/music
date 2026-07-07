package com.example.music.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 维护每个用户的 SSE 连接,用于向浏览器实时推送通知,无需前端刷新。
 * 同一个用户可能同时打开多个页面,因此每个用户 id 对应一组 emitter。
 */
@Component
@Slf4j
public class

NotificationEmitterRegistry {

    /** 连接超时时间,30 分钟;浏览器 EventSource 断开后会自动重连。 */
    private static final long TIMEOUT = 30 * 60 * 1000L;

    private final Map<Integer, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /** 用户订阅通知流,返回一个 SseEmitter 给 Spring MVC 输出。 */
    public SseEmitter subscribe(int userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> emitter.complete());
        emitter.onError(e -> remove(userId, emitter));

        try {
            // 立刻发一个握手事件,确认连接建立
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            remove(userId, emitter);
        }
        return emitter;
    }

    /** 向指定用户的所有在线连接推送一条通知。 */
    public void send(int userId, Object payload) {
        List<SseEmitter> list = emitters.get(userId);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(payload));
            } catch (Exception e) {
                log.warn("推送通知失败,移除连接 userId={}", userId, e);
                remove(userId, emitter);
            }
        }
    }

    private void remove(int userId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(userId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }
}