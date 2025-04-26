package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface PhotoMinioService {
    String uploadCoverPhoto(String fileName, MultipartFile file);
}
