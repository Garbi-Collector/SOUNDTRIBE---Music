package soundtribe.soundtribemusic.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soundtribe.soundtribemusic.dtos.notis.NotificationPost;
import soundtribe.soundtribemusic.dtos.notis.NotificationType;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.entities.SongVoteEntity;
import soundtribe.soundtribemusic.external_APIS.ExternalJWTService;
import soundtribe.soundtribemusic.external_APIS.NotificationService;
import soundtribe.soundtribemusic.external_APIS.UserFollowersService;
import soundtribe.soundtribemusic.models.enums.VoteType;
import soundtribe.soundtribemusic.repositories.SongRepository;
import soundtribe.soundtribemusic.repositories.SongVoteRepository;
import soundtribe.soundtribemusic.services.VoteService;

import java.util.List;
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
    @Autowired
    private UserFollowersService userFollowersService;
    @Autowired
    private NotificationService notificationService;

    @Async
    @Override
    public void votar(
            String token,
            Long idSong,
            VoteType newVoteType
    ) {
        // 1) Extraer el ID del usuario desde el token JWT
        Map<String, Object> userInfo = externalJWTService.validateToken(token);
        Long voter = Long.parseLong(userInfo.get("userId").toString());

        // 2) Obtener la canción
        SongEntity song = songRepository.findById(idSong)
                .orElseThrow(() -> new RuntimeException("La canción con Id: " + idSong + " no se encontró"));

        // 3) Buscar si ya existe un voto
        Optional<SongVoteEntity> existingVoteOpt = repository.findByUserIdAndSongId(voter, song.getId());

        if (existingVoteOpt.isEmpty()) {
            // Caso 1: No existe voto previo → crear uno nuevo
            SongVoteEntity newVote = SongVoteEntity.builder()
                    .userId(voter)
                    .song(song)
                    .voteType(newVoteType)
                    .build();
            repository.save(newVote);

            if (newVoteType.equals(VoteType.LIKE)){
                // crear la notificacion a mandar
                notificationService.enviarNotificacion(
                        token,
                        NotificationPost.builder()
                                .receivers(List.of(song.getOwner()))
                                .type(NotificationType.LIKE_SONG)
                                .slugSong(song.getSlug())
                                .nameSong(song.getName())
                                .build()
                );
            }
        } else {
            // Ya existe un voto
            SongVoteEntity existingVote = existingVoteOpt.get();

            if (existingVote.getVoteType() == newVoteType) {
                // Caso 2: Repite el mismo voto → eliminar
                repository.delete(existingVote);

            } else {
                // Caso 3: Cambia el voto → eliminar el viejo, crear el nuevo
                repository.delete(existingVote);

                SongVoteEntity newVote = SongVoteEntity.builder()
                        .userId(voter)
                        .song(song)
                        .voteType(newVoteType)
                        .build();
                repository.save(newVote);
                if (newVoteType.equals(VoteType.LIKE)){
                    // crear la notificacion a mandar
                    notificationService.enviarNotificacion(
                            token,
                            NotificationPost.builder()
                                    .receivers(List.of(song.getOwner()))
                                    .type(NotificationType.LIKE_SONG)
                                    .slugSong(song.getSlug())
                                    .nameSong(song.getName())
                                    .build()
                    );
                }
            }
        }


    }

    /**
     * Método que busca si un usuario ya le dio un voto específico a una canción.
     * En caso afirmativo, devuelve true; false en caso contrario.
     *
     * @param jwt para obtener el id del usuario
     * @param idSong para obtener la canción
     * @param vote para filtrar el tipo de voto
     * @return boolean
     */
    @Transactional
    @Override
    public boolean isVoted(String jwt, Long idSong, VoteType vote) {
        // 1) Extraer el ID del usuario desde el token JWT
        Map<String, Object> userInfo = externalJWTService.validateToken(jwt);
        Long voter = Long.parseLong(userInfo.get("userId").toString());

        // 2) Verificar si existe la canción (opcional si no usás song en el filtro)
        SongEntity song = songRepository.findById(idSong)
                .orElseThrow(() -> new RuntimeException("La canción con Id: " + idSong + " no se encontró"));

        // 3) Verificar si el usuario ya votó esa canción con ese tipo de voto
        return repository.existsByUserIdAndSong_IdAndVoteType(voter, idSong, vote);
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
