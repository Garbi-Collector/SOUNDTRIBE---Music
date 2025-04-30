package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.entities.AlbumEntity;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {
    List<AlbumEntity> findByOwner(Long owner);

    boolean existsBySlug(String slug);
}