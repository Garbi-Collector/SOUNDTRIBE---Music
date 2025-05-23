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
public class RequestGeneroDto {
    private Long id;
    private String name;
    private String description;
    private String festejo;
}