package soundtribe.soundtribemusic.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "album_vote")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumVoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userLiker; // ID del usuario que dio like

    @ManyToOne
    @JoinColumn(name = "album_id")
    private AlbumEntity album;
}
