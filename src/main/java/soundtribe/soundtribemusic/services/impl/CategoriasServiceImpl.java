package soundtribe.soundtribemusic.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.dtos.response.ResponseEstiloDto;
import soundtribe.soundtribemusic.dtos.response.ResponseGeneroDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSubgeneroDto;
import soundtribe.soundtribemusic.entities.GeneroEntity;
import soundtribe.soundtribemusic.entities.SubgeneroEntity;
import soundtribe.soundtribemusic.entities.EstiloEntity;
import soundtribe.soundtribemusic.repositories.EstiloRepository;
import soundtribe.soundtribemusic.repositories.GeneroRepository;
import soundtribe.soundtribemusic.repositories.SubgeneroRepository;
import soundtribe.soundtribemusic.services.CategoriasService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriasServiceImpl implements CategoriasService {

    @Autowired
    private GeneroRepository generoRepository;
    @Autowired
    private SubgeneroRepository subgeneroRepository;
    @Autowired
    private EstiloRepository estiloRepository;

    @Override
    public GeneroEntity getGeneroById(Long id) {
        return generoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Género no encontrado con id: " + id));
    }

    @Override
    public SubgeneroEntity getSubgeneroById(Long id) {
        return subgeneroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subgénero no encontrado con id: " + id));
    }

    @Override
    public EstiloEntity getEstiloById(Long id) {
        return estiloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estilo no encontrado con id: " + id));
    }


    @Override
    public List<GeneroEntity> getGenerosByIds(List<Long> ids) {
        return ids.stream()
                .map(this::getGeneroById)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubgeneroEntity> getSubgenerosByIds(List<Long> ids) {
        return ids.stream()
                .map(this::getSubgeneroById)
                .collect(Collectors.toList());
    }

    @Override
    public List<EstiloEntity> getEstilosByIds(List<Long> ids) {
        return ids.stream()
                .map(this::getEstiloById)
                .collect(Collectors.toList());
    }





    @Override
    public List<ResponseGeneroDto> getAllGeneros() {
        return generoRepository.findAll().stream()
                .map(this::mapToResponseGeneroDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseSubgeneroDto> getSubgenerosByGeneroId(Long generoId) {
        return subgeneroRepository.findByGeneroId(generoId).stream()
                .map(this::mapToResponseSubgeneroDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseEstiloDto> getAllEstilos() {
        return estiloRepository.findAll().stream()
                .map(this::mapToResponseEstiloDto)
                .collect(Collectors.toList());
    }


    @Override
    public ResponseGeneroDto mapToResponseGeneroDto(GeneroEntity genero) {
        return ResponseGeneroDto.builder()
                .id(genero.getId())
                .name(genero.getName())
                .description(genero.getDescription())
                .build();
    }
    @Override
    public ResponseSubgeneroDto mapToResponseSubgeneroDto(SubgeneroEntity subgenero) {
        return ResponseSubgeneroDto.builder()
                .id(subgenero.getId())
                .name(subgenero.getName())
                .description(subgenero.getDescription())
                .build();
    }
    @Override
    public ResponseEstiloDto mapToResponseEstiloDto(EstiloEntity estilo) {
        return ResponseEstiloDto.builder()
                .id(estilo.getId())
                .name(estilo.getName())
                    .description(estilo.getDescription())
                .build();
    }


    @Override
    public void crearGenerosSubgenerosYEstilos() {
        // Crear géneros
        String[] generos = {
                "Rock", "Pop", "Reggae", "Ska", "Jazz", "Hip Hop", "Blues", "Clásica", "Electrónica", "Folk"
        };

        // Crear subgéneros por cada género
        String[][] subgeneros = {
                {"Rock Alternativo", "Math Rock", "Post Rock"},
                {"JPop", "KPop", "C-Pop"},
                {"Reggae Dub", "Dancehall", "Roots Reggae"},
                {"Ska Punk", "2 Tone", "Ska Revival"},
                {"Smooth Jazz", "Jazz Fusion", "Bebop"},
                {"Trap", "Rap", "Boom Bap"},
                {"Blues Rock", "Chicago Blues", "Delta Blues"},
                {"Música Clásica", "Ópera", "Música de Cámara"},
                {"House", "Techno", "Dubstep"},
                {"Folk Acústico", "Folk Rock", "Indie Folk"}
        };

        // Crear estilos
        String[] estilos = {
                "Cover", "Acapella", "Sinfónico", "DIY", "Acústico", "Electrónica", "Instrumental", "Live", "Experimental", "Orquestal"
        };

        // Crear y guardar los géneros si no existen
        for (String generoName : generos) {
            // Verificar si el género ya existe
            if (!generoRepository.existsByName(generoName)) {
                GeneroEntity genero = GeneroEntity.builder()
                        .name(generoName)
                        .description("Descripción de " + generoName)
                        .build();
                generoRepository.save(genero);

                // Crear y guardar los subgéneros para cada género si no existen
                for (String subgeneroName : subgeneros[Arrays.asList(generos).indexOf(generoName)]) {
                    if (!subgeneroRepository.existsByNameAndGeneroId(subgeneroName, genero.getId())) {
                        SubgeneroEntity subgenero = SubgeneroEntity.builder()
                                .name(subgeneroName)
                                .description("Descripción de " + subgeneroName)
                                .genero(genero)
                                .build();
                        subgeneroRepository.save(subgenero);
                    }
                }
            }
        }

        // Crear y guardar los estilos si no existen
        for (String estiloName : estilos) {
            if (!estiloRepository.existsByName(estiloName)) {
                EstiloEntity estilo = EstiloEntity.builder()
                        .name(estiloName)
                        .description("Descripción de " + estiloName)
                        .build();
                estiloRepository.save(estilo);
            }
        }
    }
}
