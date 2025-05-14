package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongDto;
import soundtribe.soundtribemusic.entities.AlbumEntity;

import java.util.List;

@Service
public interface HomeDataService {

    @Transactional(readOnly = true)
    List<ResponseAlbumDto> getAlbumesMasRecientes();

    @Transactional(readOnly = true)
    List<ResponseAlbumDto> getAlbumesMasValorados();

    @Transactional(readOnly = true)
    List<ResponseAlbumDto> getAlbumesMasEscuchados();

    @Transactional(readOnly = true)
    List<ResponseSongDto> getCancionesOnFire();

    @Transactional(readOnly = true)
    List<ResponseSongDto> getCancionesMasLikeadas();

    @Transactional(readOnly = true)
    ResponseAlbumDto getAlbumBySong(Long idSong);
}
