package soundtribe.soundtribemusic.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface EliminateAccountFromMicroservice {
    @Transactional
    @Async
    void eliminateByAccount(String token);
}
