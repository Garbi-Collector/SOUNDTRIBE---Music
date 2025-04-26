package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.entities.SubgeneroEntity;

@Repository
public interface SubgeneroRepository extends JpaRepository<SubgeneroEntity, Long> {
}