package soundtribe.soundtribemusic.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.entities.SongVoteEntity;
import soundtribe.soundtribemusic.external_APIS.ExternalJWTService;
import soundtribe.soundtribemusic.models.enums.VoteType;
import soundtribe.soundtribemusic.repositories.SongRepository;
import soundtribe.soundtribemusic.repositories.SongVoteRepository;
import soundtribe.soundtribemusic.services.VoteService;

import java.util.Map;
import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    SongVoteRepository repository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    private ExternalJWTService externalJWTService;

    @Override
    public void votar(
            String token,
            Long idSong,
            VoteType voteType){
        // 1) Verificamos el token y sacamos el id del usuario.
        Map<String, Object> userInfo = externalJWTService.validateToken(token);
        Long voter = Long.parseLong(userInfo.get("userId").toString());

        // 2) Buscamos la canción por su id
        SongEntity song = songRepository.findById(idSong)
                .orElseThrow(() -> new RuntimeException("La canción con Id: " + idSong + " no se encontró"));

        // 3) Buscamos si ya existe un voto previo del usuario para esa canción
        Optional<SongVoteEntity> existingVote = repository.findByUserIdAndSongId(voter, song.getId());

        // 4) Si ya existe un voto previo, lo eliminamos
        existingVote.ifPresent(vote -> repository.delete(vote));

        // 5) Creamos un nuevo voto
        SongVoteEntity newVote = new SongVoteEntity().builder()
                .userId(voter)
                .song(song)
                .voteType(voteType)
                .build();

        // 6) Guardamos el nuevo voto
        repository.save(newVote);
    }


    @Override
    public void eliminarVoto(String token, Long idSong) {
        // 1) Verificamos el token y obtenemos el id del usuario
        Map<String, Object> userInfo = externalJWTService.validateToken(token);
        Long voter = Long.parseLong(userInfo.get("userId").toString());

        // 2) Buscamos la canción por el id
        SongEntity song = songRepository.findById(idSong)
                .orElseThrow(() -> new RuntimeException("La canción con Id: " + idSong + " no se encontró"));

        // 3) Buscamos el voto del usuario específico para esa canción
        SongVoteEntity vote = repository.findByUserIdAndSongId(voter, song.getId())
                .orElseThrow(() -> new RuntimeException("El usuario con id: " + voter + " no ha votado por esta canción"));

        // 4) Eliminamos el voto encontrado
        repository.delete(vote);
    }



    @Override
    public Long getLike(Long idSong){
        return repository.countBySongIdAndVoteType(idSong, VoteType.LIKE);
    }

    @Override
    public Long getDislike(Long idSong){
        return repository.countBySongIdAndVoteType(idSong, VoteType.DISLIKE);
    }
}
