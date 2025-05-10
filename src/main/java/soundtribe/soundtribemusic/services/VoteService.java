package soundtribe.soundtribemusic.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.models.enums.VoteType;

@Service
public interface VoteService {
    @Async
    void votar(
            String token,
            Long idSong,
            VoteType voteType);

    boolean isVoted(String jwt, Long idSong, VoteType vote);

    void eliminarVoto(String token, Long idSong);

    Long getLike(Long idSong);

    Long getDislike(Long idSong);
}
