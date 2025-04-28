package soundtribe.soundtribemusic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soundtribe.soundtribemusic.dtos.response.ResponseSubgeneroDto;
import soundtribe.soundtribemusic.entities.SubgeneroEntity;

import java.util.List;

@Repository
public interface SubgeneroRepository extends JpaRepository<SubgeneroEntity, Long> {
    List<SubgeneroEntity> findByGeneroId(Long generoId);
    boolean existsByNameAndGeneroId(String name, Long generoId);

}