package soundtribe.soundtribemusic.controllers;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soundtribe.soundtribemusic.dtos.response.ResponsePortadaDto;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.repositories.SongRepository;
import soundtribe.soundtribemusic.services.CacheService;
import soundtribe.soundtribemusic.services.PhotoService;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/files")
public class FilesController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private CacheService cacheService;

    @Value("${minio.bucket-name.song}")
    private String songBucket;

    @Value("${minio.bucket-name.portada}")
    private String portadaBucket;

    private static final int CHUNK_SIZE = 1024 * 1024; // 1MB chunks
    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(?<start>\\d+)-(?<end>\\d*)");

    @GetMapping("/portada/{id}")
    @Cacheable(value = "portadaCache", key = "#id")
    public ResponseEntity<byte[]> getPortadaById(@PathVariable Long id) {
        try {
            ResponsePortadaDto portadaDto = photoService.getPortadaDto(id);

            InputStream objectStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(portadaBucket)
                            .object(portadaDto.getFileName())
                            .build()
            );

            byte[] bytes = objectStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<InputStreamResource> playSongById(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            // 1. Buscar canción
            SongEntity songEntity = songRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Canción no encontrada con el ID: " + id));

            // 2. Incrementar contador de reproducciones
            songEntity.setPlayCount(songEntity.getPlayCount() + 1);
            songRepository.save(songEntity);
            cacheService.limpiarCacheHomeDataCacheEscuchados();

            // 3. Obtener información del archivo
            String[] parts = songEntity.getFileUrl().split("/");
            String fileName = parts[1];

            // 4. Obtener el tamaño del archivo
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(songBucket)
                            .object(fileName)
                            .build()
            );
            long fileSize = stat.size();

            // 5. Procesar Range header
            String rangeHeader = request.getHeader("Range");

            if (rangeHeader == null || rangeHeader.isEmpty()) {
                // Sin Range - devolver archivo completo
                return handleFullFileRequest(fileName, fileSize);
            } else {
                // Con Range - devolver parte específica
                return handleRangeRequest(fileName, fileSize, rangeHeader);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private ResponseEntity<InputStreamResource> handleFullFileRequest(String fileName, long fileSize)
            throws Exception {

        InputStream objectStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(songBucket)
                        .object(fileName)
                        .build()
        );

        InputStreamResource resource = new InputStreamResource(objectStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.setContentLength(fileSize);
        headers.set("Accept-Ranges", "bytes");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + fileName + "\"");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }

    private ResponseEntity<InputStreamResource> handleRangeRequest(
            String fileName, long fileSize, String rangeHeader) throws Exception {

        Matcher matcher = RANGE_PATTERN.matcher(rangeHeader);
        if (!matcher.matches()) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header("Content-Range", "bytes */" + fileSize)
                    .build();
        }

        long start = Long.parseLong(matcher.group("start"));
        String endGroup = matcher.group("end");
        long end = endGroup.isEmpty() ? fileSize - 1 : Long.parseLong(endGroup);

        // Validar rango
        if (start >= fileSize || end >= fileSize || start > end) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header("Content-Range", "bytes */" + fileSize)
                    .build();
        }

        // Ajustar el final si es necesario
        end = Math.min(end, start + CHUNK_SIZE - 1);
        long contentLength = end - start + 1;

        // Obtener el stream con offset
        InputStream objectStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(songBucket)
                        .object(fileName)
                        .offset(start)
                        .length(contentLength)
                        .build()
        );

        InputStreamResource resource = new InputStreamResource(objectStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.setContentLength(contentLength);
        headers.set("Accept-Ranges", "bytes");
        headers.set("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + fileName + "\"");

        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<InputStreamResource> streamSongById(
            @PathVariable Long id,
            HttpServletRequest request) {

        // Este endpoint es específico para streaming, siempre procesa Range
        return playSongById(id, request);
    }
}