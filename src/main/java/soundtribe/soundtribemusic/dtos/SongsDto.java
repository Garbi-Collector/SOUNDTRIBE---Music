package soundtribe.soundtribemusic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import soundtribe.soundtribemusic.entities.EstiloEntity;
import soundtribe.soundtribemusic.entities.GeneroEntity;
import soundtribe.soundtribemusic.entities.SubgeneroEntity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SongsDto {
    //metadatos
    private List<Long> artistaId;
    private String name;
    private String description;

    //catagorias
    List<GeneroEntity> generos;
    List<SubgeneroEntity> subgeneros;
    List<EstiloEntity> estilos;


    //archivo
    private MultipartFile file;
}
