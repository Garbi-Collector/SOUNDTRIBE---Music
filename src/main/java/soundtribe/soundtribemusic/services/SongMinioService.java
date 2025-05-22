package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface SongMinioService {
    String uploadSong(String fileName, MultipartFile file);

    int getWavDurationInSeconds(MultipartFile file);

    boolean isValidDurationForSaving(MultipartFile file);

    void deleteSongFromMinio(String fileUrl);
}
