package com.example.music.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${music.upload.audio-path:./uploads/audio/}")
    private String audioPath;

    @Value("${music.upload.audio-url-prefix:/audio/}")
    private String audioUrlPrefix;

    /**
     * 将上传的音频文件目录映射为可访问的静态资源路径，
     * 例如访问 /audio/xxx.mp3 即可获取上传后保存的音频文件。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + new File(audioPath).getAbsolutePath() + File.separator;
        registry.addResourceHandler(audioUrlPrefix + "**")
                .addResourceLocations(location);
    }
}