package soundtribe.soundtribemusic.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import soundtribe.soundtribemusic.dtos.request.RequestAlbumDto;
import soundtribe.soundtribemusic.dtos.request.RequestSongDto;
import soundtribe.soundtribemusic.dtos.request.UploadAlbumDto;
import soundtribe.soundtribemusic.dtos.request.UploadSongDto;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.models.enums.TypeAlbum;
import soundtribe.soundtribemusic.services.AlbumService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAlbumDto> uploadAlbum(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("album") UploadAlbumDto albumDto,
            @RequestPart("portada") MultipartFile portada,
            @RequestPart("files") List<MultipartFile> songFiles
    ) {
        // Verificamos que el encabezado contenga "Bearer " y extraemos el token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);  // Remueve el "Bearer " (7 caracteres)
            System.out.println("Token recibido en uploadAlbum: " + token);

            RequestAlbumDto realDto = buildRequestAlbumDto(albumDto, portada, songFiles);
            ResponseAlbumDto response = albumService.uploadAlbumWithSongsAndCover(token, realDto);
            return ResponseEntity.ok(response);
        } else {
            // En caso de que el encabezado no esté bien formado
            return ResponseEntity.status(400).body(null);  // O puedes devolver un mensaje de error adecuado
        }
    }


    /**
     * Obtener un álbum por su slug
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ResponseAlbumDto> getAlbumBySlug(@PathVariable String slug) {
        try {
            ResponseAlbumDto album = albumService.getAlbumBySlug(slug);
            return ResponseEntity.ok(album);
        } catch (RuntimeException e) {
            // Puedes personalizar el mensaje o crear una excepción más específica
            return ResponseEntity.status(404).body(null);
        }
    }



    /**
     * Obtener todos los álbumes de un artista por su ID
     */
    @GetMapping("/artist/{ownerId}")
    public ResponseEntity<List<ResponseAlbumDto>> getAlbumsByArtist(@PathVariable Long ownerId) {
        try {
            List<ResponseAlbumDto> albums = albumService.getAlbumsByOwnerId(ownerId);
            return ResponseEntity.ok(albums);
        } catch (Exception e) {
            // logger.error("Error al obtener álbumes por artista", e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }



    private RequestAlbumDto buildRequestAlbumDto(UploadAlbumDto uploadAlbumDto, MultipartFile portada, List<MultipartFile> songFiles) {
        List<RequestSongDto> requestSongs = new ArrayList<>();

        for (int i = 0; i < uploadAlbumDto.getSongs().size(); i++) {
            UploadSongDto uploadSong = uploadAlbumDto.getSongs().get(i);
            MultipartFile songFile = songFiles.get(i);

            RequestSongDto requestSongDto = RequestSongDto.builder()
                    .name(uploadSong.getName())
                    .description(uploadSong.getDescription())
                    .genero(uploadSong.getGenero())
                    .subgenero(uploadSong.getSubgenero())
                    .estilo(uploadSong.getEstilo())
                    .artistasFt(uploadSong.getArtistasFt())
                    .file(songFile)
                    .build();

            requestSongs.add(requestSongDto);
        }

        return RequestAlbumDto.builder()
                .name(uploadAlbumDto.getName())
                .description(uploadAlbumDto.getDescription())
                .typeAlbum(TypeAlbum.valueOf(uploadAlbumDto.getTypeAlbum()))
                .portada(portada)
                .songs(requestSongs)
                .build();
    }


    @PostMapping("/{idAlbum}/like")
    public ResponseEntity<String> likeOrUnlikeAlbum(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long idAlbum
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no proporcionado o inválido");
        }

        String token = authorizationHeader.substring(7); // quitar "Bearer "

        try {
            albumService.likeUnlikeAlbum(token, idAlbum);
            return ResponseEntity.ok("Operación de like/unlike completada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ocurrió un error al procesar la acción.");
        }
    }

    @GetMapping("/{idAlbum}/isliked")
    public ResponseEntity<?> isLikedAlbum(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long idAlbum
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no proporcionado o inválido");
        }

        String token = authorizationHeader.substring(7); // quitar "Bearer "

        try {
            boolean isLiked = albumService.isLikedAlbum(token, idAlbum);
            return ResponseEntity.ok(isLiked);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ocurrió un error al procesar la acción.");
        }
    }



}
