package com.example.music.util;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 应用启动时确保桶存在且为公共可读，
     * 使得历史上传的对象也能被浏览器直接访问播放。
     * MinIO 不可用时仅记录日志，不阻断应用启动。
     */
    @PostConstruct
    public void init() {
        try {
            createBucket();
        } catch (Exception e) {
            log.warn("初始化 MinIO 桶失败，稍后上传时会重试: {}", e.getMessage());
        }
    }

    /**
     * 检查桶是否存在，不存在则创建，并（幂等地）设置为公共可读，
     * 使得上传后返回的 URL 可以被浏览器直接访问播放。
     */
    public void createBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        // 无论桶是新建还是已存在，都确保公共读策略生效（幂等，可安全重复执行）
        String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{"
                + "\"Effect\":\"Allow\",\"Principal\":\"*\","
                + "\"Action\":[\"s3:GetObject\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
    }

    /**
     * 文件上传：按分类存放到不同文件夹，并保留原始文件名。
     * 对象命名规则为 {folder}/{uuid}/{原始文件名}，用 UUID 目录隔离，
     * 既避免同名文件互相覆盖，又让最终文件名保持为用户上传时的原始名称。
     *
     * @param file   上传的文件
     * @param folder 目标文件夹（如 audio、image）
     * @return 文件完整访问地址（路径已做 URL 编码，兼容中文/空格文件名）
     */
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        createBucket();
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "unnamed";
        }
        // 仅取文件名部分，防止路径穿越（如浏览器带上的相对路径）
        originalName = originalName.replace("\\", "/");
        originalName = originalName.substring(originalName.lastIndexOf("/") + 1);

        String objectName = folder + "/" + UUID.randomUUID().toString().replace("-", "") + "/" + originalName;
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
        // 直接用endpoint拼接路径，逐段编码以兼容中文/空格等特殊字符
        return endpoint + "/" + bucketName + "/" + encodePath(objectName);
    }

    /**
     * 对对象路径逐段进行 URL 编码，保留 "/" 分隔符，
     * 使返回的地址能被浏览器直接访问（处理中文、空格等字符）。
     */
    private String encodePath(String objectName) {
        String[] segments = objectName.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(URLEncoder.encode(segments[i], StandardCharsets.UTF_8).replace("+", "%20"));
        }
        return sb.toString();
    }

    /**
     * 获取文件临时访问URL（默认有效期1小时）
     */
    public String getFileUrl(String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .method(Method.GET)
                        .expiry(1, TimeUnit.HOURS)
                        .build()
        );
    }

    /**
     * 文件下载流
     */
    public InputStream downloadFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }
}
