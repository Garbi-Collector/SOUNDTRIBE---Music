package soundtribe.soundtribemusic.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import soundtribe.soundtribemusic.models.enums.TypeAlbum;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAlbumDto {
    private Long id;
    private String name;
    private String description;
    private TypeAlbum typeAlbum;
    private ResponsePortadaDto portada;
    private List<ResponseSongDto> songs;
    private Long owner;
    private String slug;
    private Long likeCount;
}
