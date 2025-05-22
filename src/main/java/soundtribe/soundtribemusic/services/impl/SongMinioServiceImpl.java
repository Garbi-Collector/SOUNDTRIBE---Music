package soundtribe.soundtribemusic.services.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import soundtribe.soundtribemusic.services.SongMinioService;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
public class SongMinioServiceImpl implements SongMinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name.song}")
    private String songBucket;

    @Transactional
    @Override
    public String uploadSong(String fileName, MultipartFile file) {
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".wav")) {
            throw new IllegalArgumentException("Solo se permiten archivos .wav");
        }

        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(songBucket)
                            .object(fileName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return songBucket + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error subiendo archivo a MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public int getWavDurationInSeconds(MultipartFile file) {
        try {
            // Convertimos el InputStream en un byte[]
            byte[] fileBytes = file.getBytes();

            // Creamos un ByteArrayInputStream que soporta mark/reset
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
                 AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream)) {

                AudioFormat format = audioInputStream.getFormat();
                long frames = audioInputStream.getFrameLength();
                float frameRate = format.getFrameRate();

                return Math.round(frames / frameRate);
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException("No se pudo calcular la duración del archivo WAV: " + e.getMessage(), e);
        }
    }


    @Override
    public boolean isValidDurationForSaving(MultipartFile file) {
        try {
            int duration = getWavDurationInSeconds(file);
            return duration > 3 && duration < 600;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteSongFromMinio(String fileUrl) {
        try {
            // Validamos y extraemos el nombre del archivo
            if (fileUrl == null || !fileUrl.contains("/")) {
                throw new IllegalArgumentException("La URL del archivo no es válida");
            }

            String[] parts = fileUrl.split("/");
            if (parts.length != 2 || !parts[0].equals(songBucket)) {
                throw new IllegalArgumentException("Formato de fileUrl inválido o bucket incorrecto");
            }

            String fileName = parts[1];

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(songBucket)
                            .object(fileName)
                            .build()
            );

            System.out.println("✅ Canción eliminada correctamente de MinIO: " + fileName);

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando la canción de MinIO: " + e.getMessage(), e);
        }
    }

}

