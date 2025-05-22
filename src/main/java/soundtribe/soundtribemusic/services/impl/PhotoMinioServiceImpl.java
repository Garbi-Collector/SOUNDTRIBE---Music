package soundtribe.soundtribemusic.services.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import soundtribe.soundtribemusic.services.PhotoMinioService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Service
public class PhotoMinioServiceImpl implements PhotoMinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name.portada}")
    private String portadaBucket;

    private final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 3MB como ejemplo promedio

    @Override
    public String uploadCoverPhoto(String fileName, MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".png")) {
            throw new IllegalArgumentException("Solo se permiten imágenes PNG");
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new IllegalArgumentException("No se pudo leer la imagen");
            }

            if (image.getWidth() != image.getHeight()) {
                throw new IllegalArgumentException("La imagen debe ser cuadrada");
            }

            if (file.getSize() > MAX_SIZE_BYTES) {
                throw new IllegalArgumentException("La imagen excede el tamaño máximo permitido (5MB)");
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(portadaBucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return portadaBucket + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen a MinIO: " + e.getMessage(), e);
        }
    }
    @Override
    public void deleteCoverPhoto(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(portadaBucket)
                            .object(fileName)
                            .build()
            );
            System.out.println("✅ Imagen eliminada de MinIO: " + fileName);
        } catch (Exception e) {
            throw new RuntimeException("❌ Error al eliminar la imagen de MinIO: " + e.getMessage(), e);
        }
    }
}
