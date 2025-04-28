package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.entities.GeneroEntity;

@Repository
public interface GeneroRepository extends JpaRepository<GeneroEntity, Long> {
    boolean existsByName(String name);
}