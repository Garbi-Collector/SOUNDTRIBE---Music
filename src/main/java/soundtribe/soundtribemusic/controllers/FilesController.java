package soundtribe.soundtribemusic.controllers;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soundtribe.soundtribemusic.dtos.response.ResponsePortadaDto;
import soundtribe.soundtribemusic.entities.FilePhotoEntity;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.repositories.FilePhotoRepository;
import soundtribe.soundtribemusic.repositories.SongRepository;
import soundtribe.soundtribemusic.services.PhotoMinioService;
import soundtribe.soundtribemusic.services.PhotoService;

import java.io.InputStream;

@RestController
@RequestMapping("/files")
public class FilesController {

    @Autowired
    private PhotoService photoService;
    @Autowired
    private PhotoMinioService photoMinioService;
    @Autowired
    private MinioClient minioClient;

    @Autowired
    private SongRepository songRepository;

    @Value("${minio.bucket-name.song}")
    private String songBucket;


    @Value("${minio.bucket-name.portada}")
    private String portadaBucket;

    @GetMapping("/portada/{id}")
    public ResponseEntity<byte[]> getPortadaById(@PathVariable Long id) {
        try {
            // 1. Buscar la info del archivo en la base de datos
            ResponsePortadaDto portadaDto = photoService.getPortadaDto(id);

            // 2. Descargar el archivo desde MinIO
            InputStream objectStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(portadaBucket)
                            .object(portadaDto.getFileName())
                            .build()
            );

            // 3. Convertir el InputStream a un array de bytes
            byte[] bytes = objectStream.readAllBytes();

            // 4. Retornar la imagen como respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // Sabemos que es PNG
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            // Manejar errores
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<byte[]> playSongById(@PathVariable Long id) {
        try {
            // 1. Buscar la canción en la base de datos
            SongEntity songEntity = songRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Canción no encontrada con el ID: " + id));

            // 2. Extraer el nombre real del archivo (recordá que fileUrl es tipo "bucket/filename.wav")
            String[] parts = songEntity.getFileUrl().split("/");
            String fileName = parts[1];

            // 3. Descargar el archivo desde MinIO
            InputStream objectStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(songBucket)
                            .object(fileName)
                            .build()
            );

            // 4. Convertir el InputStream a un array de bytes
            byte[] bytes = objectStream.readAllBytes();

            // 5. Configurar headers para audio
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/wav"));

            // 6. Retornar el archivo de audio
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
