package soundtribe.soundtribemusic.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subgenero")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubgeneroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "genero_id")
    private GeneroEntity genero;
}