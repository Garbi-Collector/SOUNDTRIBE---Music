package soundtribe.soundtribemusic.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongPortadaDto;
import soundtribe.soundtribemusic.services.ExploreService;

import java.util.List;

@RestController
@RequestMapping("/api/explore")
@CrossOrigin(origins = "*")
public class ExploreController {

    private final ExploreService exploreService;

    @Autowired
    public ExploreController(ExploreService exploreService) {
        this.exploreService = exploreService;
    }

    /**
     * Endpoint para explorar álbumes con filtros opcionales.
     *
     * @param name Nombre del álbum a buscar (opcional)
     * @param genero ID del género a filtrar (opcional)
     * @return Lista de álbumes filtrados, ordenados por popularidad, máximo 15 elementos
     */
    @GetMapping("/albums")
    public ResponseEntity<List<ResponseAlbumDto>> explorarAlbumes(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "genero", required = false) Long genero
    ) {
        List<ResponseAlbumDto> albums = exploreService.explorarAlbumes(name, genero);
        return ResponseEntity.ok(albums);
    }

    /**
     * Endpoint para explorar canciones con filtros opcionales.
     *
     * @param name Nombre de la canción a buscar (opcional)
     * @param genero ID del género a filtrar (opcional)
     * @return Lista de canciones filtradas, ordenadas por popularidad, máximo 15 elementos
     */
    @GetMapping("/songs")
    public ResponseEntity<List<ResponseSongPortadaDto>> explorarCanciones(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "genero", required = false) Long genero
    ) {
        List<ResponseSongPortadaDto> songs = exploreService.explorarCanciones(name, genero);
        return ResponseEntity.ok(songs);
    }
}