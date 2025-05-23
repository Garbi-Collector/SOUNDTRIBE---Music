package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongPortadaDto;

import java.util.List;

@Service
public interface ExploreService {

    List<ResponseAlbumDto> explorarAlbumes(String name, Long genero);

    List<ResponseSongPortadaDto> explorarCanciones(String name, Long genero);
}
