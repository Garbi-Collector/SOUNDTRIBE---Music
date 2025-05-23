package soundtribe.soundtribemusic.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardSong {
    private String nameSong;
    private Long playCount;
    private String slug;
}
