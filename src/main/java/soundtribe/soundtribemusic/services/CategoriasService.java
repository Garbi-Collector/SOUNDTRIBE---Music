package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.dtos.response.ResponseEstiloDto;
import soundtribe.soundtribemusic.dtos.response.ResponseGeneroDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSubgeneroDto;
import soundtribe.soundtribemusic.entities.EstiloEntity;
import soundtribe.soundtribemusic.entities.GeneroEntity;
import soundtribe.soundtribemusic.entities.SubgeneroEntity;

import java.util.List;

@Service
public interface CategoriasService {
    GeneroEntity getGeneroById(Long id);

    SubgeneroEntity getSubgeneroById(Long id);

    EstiloEntity getEstiloById(Long id);


    List<GeneroEntity> getGenerosByIds(List<Long> ids);

    List<SubgeneroEntity> getSubgenerosByIds(List<Long> ids);

    List<EstiloEntity> getEstilosByIds(List<Long> ids);

    List<ResponseGeneroDto> getAllGeneros();

    List<ResponseSubgeneroDto> getSubgenerosByGeneroId(Long generoId);

    List<ResponseEstiloDto> getAllEstilos();

    // Métodos privados para mapeo
    ResponseGeneroDto mapToResponseGeneroDto(GeneroEntity genero);

    ResponseSubgeneroDto mapToResponseSubgeneroDto(SubgeneroEntity subgenero);

    ResponseEstiloDto mapToResponseEstiloDto(EstiloEntity estilo);

    // Méto-do para crear géneros, subgéneros y estilos
    void crearGenerosSubgenerosYEstilos();
}
