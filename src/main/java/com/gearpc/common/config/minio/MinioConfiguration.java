package com.gearpc.common.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Value("${minio.domain.endpoint}")
    private String minioDomainEndpoint;

    @Value("${minio.access.name}")
    private String minioAccessName;

    @Value("${minio.access.secret}")
    private String minioSecretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioDomainEndpoint)
                .credentials(minioAccessName, minioSecretKey)
                .build();
    }

}
