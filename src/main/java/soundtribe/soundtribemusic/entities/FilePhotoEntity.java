package soundtribe.soundtribemusic.entities;

import jakarta.persistence.*;
import lombok.*;
import soundtribe.soundtribemusic.models.enums.FileType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "file_photo")
public class FilePhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileUrl;
}
