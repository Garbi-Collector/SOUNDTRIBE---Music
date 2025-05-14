package soundtribe.soundtribemusic.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongDto;
import soundtribe.soundtribemusic.services.HomeDataService;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeDataController {

    @Autowired
    private HomeDataService homeDataService;

    @GetMapping("/albumes-recientes")
    public ResponseEntity<List<ResponseAlbumDto>> getAlbumesMasRecientes() {
        return ResponseEntity.ok(homeDataService.getAlbumesMasRecientes());
    }

    @GetMapping("/albumes-mas-votados")
    public ResponseEntity<List<ResponseAlbumDto>> getAlbumesMasValorados() {
        return ResponseEntity.ok(homeDataService.getAlbumesMasValorados());
    }

    @GetMapping("/albumes-mas-escuchados")
    public ResponseEntity<List<ResponseAlbumDto>> getAlbumesMasEscuchados() {
        return ResponseEntity.ok(homeDataService.getAlbumesMasEscuchados());
    }

    @GetMapping("/canciones-onfire")
    public ResponseEntity<List<ResponseSongDto>> getCancionesOnFire() {
        return ResponseEntity.ok(homeDataService.getCancionesOnFire());
    }

    @GetMapping("/canciones-mas-likeadas")
    public ResponseEntity<List<ResponseSongDto>> getCancionesMasLikeadas() {
        return ResponseEntity.ok(homeDataService.getCancionesMasLikeadas());
    }

    @GetMapping("/album-de-cancion/{idSong}")
    public ResponseEntity<ResponseAlbumDto> getAlbumBySong(@PathVariable Long idSong) {
        return ResponseEntity.ok(homeDataService.getAlbumBySong(idSong));
    }
}
