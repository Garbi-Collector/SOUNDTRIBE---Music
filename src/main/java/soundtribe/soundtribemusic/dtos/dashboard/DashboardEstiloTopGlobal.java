package soundtribe.soundtribemusic.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardEstiloTopGlobal {
    private String nameEstilo;
    private Long playCount;
    private String festejo;
}
