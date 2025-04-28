package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.entities.EstiloEntity;

@Repository
public interface EstiloRepository extends JpaRepository<EstiloEntity, Long> {
    boolean existsByName(String name);
}