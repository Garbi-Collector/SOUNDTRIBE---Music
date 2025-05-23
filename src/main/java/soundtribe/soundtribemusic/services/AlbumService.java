package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soundtribe.soundtribemusic.dtos.request.RequestAlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.entities.AlbumEntity;

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

    @Transactional(readOnly = true)
    List<AlbumEntity> getAll();

    List<ResponseAlbumDto> getAlbumsByOwnerId(Long ownerId);

    @Transactional
    void likeUnlikeAlbum(String jwt, Long idAlbum);

    boolean isLikedAlbum(String jwt, Long idAlbum);

    ResponseAlbumDto getAlbumBySlug(String slug);
}
