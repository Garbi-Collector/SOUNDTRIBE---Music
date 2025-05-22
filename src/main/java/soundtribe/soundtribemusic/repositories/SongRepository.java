package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.entities.SongEntity;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, Long> {

    boolean existsBySlugAndAlbumId(String slug, Long albumId);

    List<SongEntity> findByAlbumId(Long id);

    @Query(value = """
    SELECT s.*, 
           COUNT(CASE WHEN v.vote_type = 'LIKE' THEN 1 END) AS like_count,
           (s.play_count + COUNT(CASE WHEN v.vote_type = 'LIKE' THEN 10 END)) AS score
    FROM song s
    LEFT JOIN song_vote v ON s.id = v.song_id
        AND v.vote_type = 'LIKE'
        AND YEARWEEK(v.created_at, 1) = YEARWEEK(CURDATE(), 1)
    GROUP BY s.id
    ORDER BY score DESC
    LIMIT 10
    """, nativeQuery = true)
    List<SongEntity> findTop10SongsOnFire();


    @Query(value = """
    SELECT s.*, 
           COUNT(CASE WHEN v.vote_type = 'LIKE' THEN 1 END) AS like_count
    FROM song s
    LEFT JOIN song_vote v ON s.id = v.song_id
        AND v.vote_type = 'LIKE'
    GROUP BY s.id
    ORDER BY like_count DESC
    LIMIT 10
    """, nativeQuery = true)
    List<SongEntity> findTop10MostLikedSongs();


    List<SongEntity> findByArtistaIdsContaining(Long artistaId);

    List<SongEntity> findByOwner(Long owner);


}