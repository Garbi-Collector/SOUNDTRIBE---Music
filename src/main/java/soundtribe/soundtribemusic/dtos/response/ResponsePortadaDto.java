package soundtribe.soundtribemusic.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePortadaDto {
    private Long id;
    private String fileName;
    private String fileUrl;
}
