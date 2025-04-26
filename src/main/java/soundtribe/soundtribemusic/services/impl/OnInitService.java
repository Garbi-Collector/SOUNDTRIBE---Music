package soundtribe.soundtribemusic.services.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.configs.MinioConfig;

@Service
public class OnInitService {

    @Autowired
    MinioConfig minioConfig;

    @PostConstruct
    public void OnInit(){
        minioConfig.init();
    }
}
