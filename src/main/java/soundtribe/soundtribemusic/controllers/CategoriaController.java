package soundtribe.soundtribemusic.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soundtribe.soundtribemusic.dtos.response.ResponseEstiloDto;
import soundtribe.soundtribemusic.dtos.response.ResponseGeneroDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSubgeneroDto;
import soundtribe.soundtribemusic.services.CategoriasService;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriasService categoriasService;

    // Obtener todos los géneros
    @GetMapping("/generos")
    public ResponseEntity<List<ResponseGeneroDto>> getAllGeneros() {
        List<ResponseGeneroDto> generos = categoriasService.getAllGeneros();
        return ResponseEntity.ok(generos);
    }

    // Obtener todos los subgéneros por id del género
    @GetMapping("/generos/{generoId}/subgeneros")
    public ResponseEntity<List<ResponseSubgeneroDto>> getSubgenerosByGeneroId(@PathVariable Long generoId) {
        List<ResponseSubgeneroDto> subgeneros = categoriasService.getSubgenerosByGeneroId(generoId);
        return ResponseEntity.ok(subgeneros);
    }

    // Obtener todos los estilos
    @GetMapping("/estilos")
    public ResponseEntity<List<ResponseEstiloDto>> getAllEstilos() {
        List<ResponseEstiloDto> estilos = categoriasService.getAllEstilos();
        return ResponseEntity.ok(estilos);
    }
}
