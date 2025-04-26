package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.entities.FilePhotoEntity;

@Repository
public interface FilePhotoRepository extends JpaRepository<FilePhotoEntity, Long> {
}