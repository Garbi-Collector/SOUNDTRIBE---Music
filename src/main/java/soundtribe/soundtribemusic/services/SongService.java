package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.dtos.SongsDto;
import soundtribe.soundtribemusic.dtos.request.RequestSongDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongDto;
import soundtribe.soundtribemusic.entities.SongEntity;

@Service
public interface SongService {


    SongEntity uploadSongWithInfo(
            Long owner,
            RequestSongDto songsDto
    );

    ResponseSongDto getSongDto(Long id);
}
