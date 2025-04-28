package soundtribe.soundtribemusic.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import soundtribe.soundtribemusic.models.enums.TypeAlbum;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestAlbumDto {
    private String name;
    private String description;
    private TypeAlbum typeAlbum;
    private MultipartFile portada;
    private List<RequestSongDto> songs;
}
