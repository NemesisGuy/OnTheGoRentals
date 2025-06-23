package za.ac.cput.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;



@Configuration
@Profile("storage-minio")
public class MinioConfig {

    /**
     * Creates and configures the MinioClient bean.
     * Properties are injected directly into this method as parameters to ensure they are
     * available before the bean is constructed, which is more robust than using @Value on fields.
     *
     * @param url       The URL of the MinIO server.
     * @param accessKey The access key for MinIO.
     * @param secretKey The secret key for MinIO.
     * @return A configured MinioClient instance.
     */
    @Bean
    public MinioClient minioClient(
            @Value("${minio.url}") String url,
            @Value("${minio.access.key}") String accessKey,
            @Value("${minio.secret.key}") String secretKey
    ) {
        // Now, we are using the method parameters, which are guaranteed to be populated.
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}