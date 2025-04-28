package soundtribe.soundtribemusic.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestSongDto {
    private String name;
    private String description;
    private List<Long> genero;
    private List<Long> subgenero;
    private List<Long> estilo;
    private List<Long> artistasFt;

    //archivo
    private MultipartFile file;
}
