package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import soundtribe.soundtribemusic.entities.SongVoteEntity;

public interface SongVoteRepository extends JpaRepository<SongVoteEntity, Long> {
}