package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.entities.AlbumEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {
    List<AlbumEntity> findByOwner(Long owner);

    boolean existsBySlug(String slug);

    Optional<AlbumEntity> findBySlug(String slug);

    Optional<AlbumEntity> findById(Long id);

    Long id(Long id);

    List<AlbumEntity> findTop10ByOrderByCreatedAtDesc();

    List<AlbumEntity> findTop10ByOrderByLikeCountDesc();


    @Query(value = """
        SELECT a.*
        FROM album a
        JOIN song s ON a.id = s.album_id
        GROUP BY a.id
        ORDER BY SUM(s.play_count) DESC
        LIMIT 10
        """, nativeQuery = true)
    List<AlbumEntity> findTop10MostPlayedAlbums();


}