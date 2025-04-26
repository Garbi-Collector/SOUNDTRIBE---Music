package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.dtos.SongsDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongDto;
import soundtribe.soundtribemusic.entities.SongEntity;

@Service
public interface SongService {

    SongEntity uploadSongWithInfo(
            Long owner,
            SongsDto uploadSongWithInfoDto
    );

    ResponseSongDto getSongDto(Long id);
}
