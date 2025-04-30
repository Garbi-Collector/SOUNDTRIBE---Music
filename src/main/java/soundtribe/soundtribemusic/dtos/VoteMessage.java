package soundtribe.soundtribemusic.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soundtribe.soundtribemusic.models.enums.VoteType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteMessage {
    private Long songId;
    private VoteType voteType;
}
