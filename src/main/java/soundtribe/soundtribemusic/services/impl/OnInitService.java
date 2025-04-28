package soundtribe.soundtribemusic.services.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.configs.MinioConfig;
import soundtribe.soundtribemusic.services.CategoriasService;

@Service
public class OnInitService {

    @Autowired
    MinioConfig minioConfig;
    @Autowired
    CategoriasService categoriasService;

    @PostConstruct
    public void OnInit(){
        minioConfig.init();
        categoriasService.crearGenerosSubgenerosYEstilos();
    }
}
