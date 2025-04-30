package soundtribe.soundtribemusic.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import soundtribe.soundtribemusic.models.enums.VoteType;

@Data
@AllArgsConstructor
public class VoteResponse {
    private Long songId;
    private VoteType voteType;
    private String message;
}
