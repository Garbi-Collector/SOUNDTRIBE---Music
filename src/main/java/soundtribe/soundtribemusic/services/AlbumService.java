package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import soundtribe.soundtribemusic.dtos.AlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.entities.AlbumEntity;
import soundtribe.soundtribemusic.models.enums.TypeAlbum;

import java.util.List;

@Service
public interface AlbumService {


    ResponseAlbumDto uploadAlbumWithSongsAndCover(
            //seguridad
            String token,
            //album
            AlbumDto albumDto
    );

    ResponseAlbumDto mapperAlbum(Long id);
}
