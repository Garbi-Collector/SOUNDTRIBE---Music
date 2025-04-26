package soundtribe.soundtribemusic.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import soundtribe.soundtribemusic.dtos.response.ResponsePortadaDto;
import soundtribe.soundtribemusic.entities.FilePhotoEntity;

@Service
public interface PhotoService {
    FilePhotoEntity uploadCover(MultipartFile file);

    ResponsePortadaDto getPortadaDto(Long id);
}
