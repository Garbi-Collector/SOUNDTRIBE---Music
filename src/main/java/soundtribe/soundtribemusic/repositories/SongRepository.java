package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.entities.SongEntity;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, Long> {

    boolean existsBySlugAndAlbumId(String slug, Long albumId);

}