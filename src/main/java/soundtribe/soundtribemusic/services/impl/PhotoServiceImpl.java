package soundtribe.soundtribemusic.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import soundtribe.soundtribemusic.dtos.response.ResponsePortadaDto;
import soundtribe.soundtribemusic.entities.FilePhotoEntity;
import soundtribe.soundtribemusic.repositories.FilePhotoRepository;
import soundtribe.soundtribemusic.services.PhotoMinioService;
import soundtribe.soundtribemusic.services.PhotoService;

import java.util.UUID;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private PhotoMinioService photoMinioService;

    @Autowired
    private FilePhotoRepository filePhotoRepository;

    @Override
    public FilePhotoEntity uploadCover(MultipartFile file) {
        String uniqueName = UUID.randomUUID() + ".png";
        String fileUrl = photoMinioService.uploadCoverPhoto(uniqueName, file);

        FilePhotoEntity photo = FilePhotoEntity.builder()
                .fileName(uniqueName)
                .fileUrl(fileUrl)
                .build();

        return filePhotoRepository.save(photo);
    }

    @Override
    public ResponsePortadaDto getPortadaDto(Long id){
        FilePhotoEntity portadaE = filePhotoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("portada no encontrada con el id: "+id));
        return ResponsePortadaDto.builder()
                .id(portadaE.getId())
                .fileName(portadaE.getFileName())
                .fileUrl(portadaE.getFileUrl())
                .build();
    }



}
