package soundtribe.soundtribemusic.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.dtos.SongsDto;
import soundtribe.soundtribemusic.dtos.response.ResponseEstiloDto;
import soundtribe.soundtribemusic.dtos.response.ResponseGeneroDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSubgeneroDto;
import soundtribe.soundtribemusic.entities.EstiloEntity;
import soundtribe.soundtribemusic.entities.GeneroEntity;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.entities.SubgeneroEntity;
import soundtribe.soundtribemusic.repositories.SongRepository;
import soundtribe.soundtribemusic.services.SongMinioService;
import soundtribe.soundtribemusic.services.SongService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    SongMinioService songMinioService;
    @Autowired
    SongRepository repo;

    @Override
    public SongEntity uploadSongWithInfo(
            Long owner,
            SongsDto songsDto
    ) {
        String fileUrl = songMinioService.uploadSong(UUID.randomUUID() + ".wav", songsDto.getFile());


        SongEntity song = SongEntity.builder()
                .name(songsDto.getName())
                .description(songsDto.getDescription())
                .duration(songMinioService.getWavDurationInSeconds(songsDto.getFile()))
                .fileUrl(fileUrl)
                .generos(songsDto.getGeneros())
                .subgeneros(songsDto.getSubgeneros())
                .estilos(songsDto.getEstilos())
                .artistaIds(songsDto.getArtistaId())
                .owner(owner)
                .build();

        return repo.save(song);
    }

    @Override
    public ResponseSongDto getSongDto(Long id){
        SongEntity songE = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("cancion no encontrada con el id: "+id));
        ResponseSongDto songDto;

        // iniciamos la tanda de listados
        List<ResponseGeneroDto> generos = new ArrayList<>();
        List<ResponseSubgeneroDto> subgeneros = new ArrayList<>();
        List<ResponseEstiloDto> estilos = new ArrayList<>();

        //realizamos la busqueda y seteo

        //genero
        for(GeneroEntity genero: songE.getGeneros()){
            ResponseGeneroDto generoDto = ResponseGeneroDto.builder()
                    .id(genero.getId())
                    .name(genero.getName())
                    .description(genero.getDescription())
                    .build();
            generos.add(generoDto);
        }
        //subgenero
        for(SubgeneroEntity subgenero: songE.getSubgeneros()){
            ResponseSubgeneroDto subgeneroDto = ResponseSubgeneroDto.builder()
                    .id(subgenero.getId())
                    .name(subgenero.getName())
                    .description(subgenero.getDescription())
                    .build();
            subgeneros.add(subgeneroDto);
        }
        //estilo
        for(EstiloEntity estilo: songE.getEstilos()){
            ResponseEstiloDto estiloDto = ResponseEstiloDto.builder()
                    .id(estilo.getId())
                    .name(estilo.getName())
                    .description(estilo.getDescription())
                    .build();
            estilos.add(estiloDto);
        }


        return songDto = new ResponseSongDto().builder()
                .id(songE.getId())
                .name(songE.getName())
                .description(songE.getDescription())
                .owner(songE.getOwner())
                .fileUrl(songE.getFileUrl())
                .genero(generos)
                .subgenero(subgeneros)
                .estilo(estilos)
                .artistasFt(songE.getArtistaIds())
                .build();
    }
}