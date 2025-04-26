package soundtribe.soundtribemusic.dtos;

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
public class AlbumDto {
    //album
    String name;
    String description;
    TypeAlbum typeAlbum;
    //portada
    MultipartFile portada;
    //songs
    List<SongsDto> songs;
}
