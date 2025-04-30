package soundtribe.soundtribemusic.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "song")
public class SongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Integer duration; // en segundos

    @Column(name = "owner")
    private Long owner;

    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private AlbumEntity album;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "song_genero",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private List<GeneroEntity> generos;

    @ManyToMany
    @JoinTable(
            name = "song_subgenero",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "subgenero_id")
    )
    private List<SubgeneroEntity> subgeneros;

    @ManyToMany
    @JoinTable(
            name = "song_estilo",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "estilo_id")
    )
    private List<EstiloEntity> estilos;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> artistaIds; // Referencias externas a otro microservicio

    @Column(name = "play_count", nullable = false)
    private Long playCount;


    @Column(name = "slug")
    private String slug;

}
