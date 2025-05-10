package soundtribe.soundtribemusic.dtos.notis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationPost {
    List<Long> receivers; //aca
    NotificationType type; //aca
    String slugSong; //para las canciones
    String nameSong; //para las canciones
    String slugAlbum; //para los albumes
    String nameAlbum; //para los albumes
}