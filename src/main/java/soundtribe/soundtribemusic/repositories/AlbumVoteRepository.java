package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.entities.AlbumVoteEntity;

import java.util.Optional;

@Repository
public interface AlbumVoteRepository extends JpaRepository<AlbumVoteEntity, Long> {

    Optional<AlbumVoteEntity> findByUserLikerAndAlbumId(Long userId, Long albumId);

    boolean existsByUserLikerAndAlbum_Id(Long userLiker, Long albumId);

}