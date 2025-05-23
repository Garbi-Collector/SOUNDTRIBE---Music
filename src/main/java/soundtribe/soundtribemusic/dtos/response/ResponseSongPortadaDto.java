package soundtribe.soundtribemusic.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSongPortadaDto {
    private Long id;
    private String name;
    private String description;
    private Integer duration;
    private Long owner;
    private String fileUrl;
    private List<ResponseGeneroDto> genero;
    private List<ResponseSubgeneroDto> subgenero;
    private List<ResponseEstiloDto> estilo;
    private List<Long> artistasFt;
    private Long likes;
    private Long dislike;
    private String slug;
    private Long playCount;
    private ResponsePortadaDto portada;
}
