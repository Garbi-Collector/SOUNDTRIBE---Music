package soundtribe.soundtribemusic.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSubgeneroDto {
    private Long id;
    private String name;
    private String description;
}
