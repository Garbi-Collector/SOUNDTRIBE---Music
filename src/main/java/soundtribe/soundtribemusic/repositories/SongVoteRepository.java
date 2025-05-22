package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import soundtribe.soundtribemusic.entities.SongVoteEntity;
import soundtribe.soundtribemusic.models.enums.VoteType;

import java.util.List;
import java.util.Optional;

public interface SongVoteRepository extends JpaRepository<SongVoteEntity, Long> {

    long countBySongIdAndVoteType(Long songId, VoteType voteType);

    Optional<SongVoteEntity> findByUserIdAndSongId(Long userId, Long songId);

    boolean existsByUserIdAndSong_IdAndVoteType(Long userId, Long songId, VoteType voteType);

    List<SongVoteEntity> findByUserId(Long userId);

}