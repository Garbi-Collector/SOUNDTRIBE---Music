package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.dtos.request.RequestAlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;

import java.util.List;

@Service
public interface AlbumService {


    ResponseAlbumDto uploadAlbumWithSongsAndCover(
            //seguridad
            String token,
            //album
            RequestAlbumDto albumDto
    );

    ResponseAlbumDto mapperAlbum(Long id);

    List<ResponseAlbumDto> getAlbumsByOwnerId(Long ownerId);

    ResponseAlbumDto getAlbumBySlug(String slug);
}
