package soundtribe.soundtribemusic.configs;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name.song}")
    private String songBucket;

    @Value("${minio.bucket-name.portada}")
    private String portadaBucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public void init() {
        try {
            MinioClient minioClient = minioClient();

            // Crear song bucket si no existe
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(songBucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(songBucket).build());
                System.out.println("Bucket '" + songBucket + "' creado exitosamente.");
            } else {
                System.out.println("Bucket '" + songBucket + "' ya existe.");
            }

            // Crear portada bucket si no existe
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(portadaBucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(portadaBucket).build());
                System.out.println("Bucket '" + portadaBucket + "' creado exitosamente.");
            } else {
                System.out.println("Bucket '" + portadaBucket + "' ya existe.");
            }

        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            System.err.println("Error al verificar/crear los buckets: " + e.getMessage());
        }
    }


}
