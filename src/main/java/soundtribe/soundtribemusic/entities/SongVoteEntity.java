package soundtribe.soundtribemusic.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import soundtribe.soundtribemusic.models.enums.VoteType;

import java.time.LocalDateTime;

@Entity
@Table(name = "song_vote")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongVoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "song_id")
    private SongEntity song;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
