package soundtribe.soundtribemusic.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import soundtribe.soundtribemusic.models.enums.TypeAlbum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "album")
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeAlbum typeAlbum;

    @ManyToOne
    @JoinColumn(name = "photo_id")
    private FilePhotoEntity photo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SongEntity> songs;

    @Column(name = "owner")
    private Long owner;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "slug")
    private String slug;


}
