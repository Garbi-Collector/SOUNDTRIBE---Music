package soundtribe.soundtribemusic.services;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public interface CacheService {
    @CacheEvict(value = "homeDataCacheReciente", allEntries = true)
    void limpiarCacheHomeDataCacheReciente();

    @CacheEvict(value = "homeDataCacheValorado", allEntries = true)
    void limpiarCacheHomeDataCacheValorado();

    @CacheEvict(value = "homeDataCacheEscuchados", allEntries = true)
    void limpiarCacheHomeDataCacheEscuchados();
}
