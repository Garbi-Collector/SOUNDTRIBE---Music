package soundtribe.soundtribemusic.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadSongDto {
    private String name;
    private String description;
    private List<Long> genero;
    private List<Long> subgenero;
    private List<Long> estilo;
    private List<Long> artistasFt;
}