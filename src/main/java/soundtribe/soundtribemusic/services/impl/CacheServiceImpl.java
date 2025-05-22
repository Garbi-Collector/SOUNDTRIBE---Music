package soundtribe.soundtribemusic.services.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.services.CacheService;
@Service
public class CacheServiceImpl implements CacheService {

    @CacheEvict(value = "homeDataCacheReciente", allEntries = true)
    @Override
    public void limpiarCacheHomeDataCacheReciente() {
    }
    @CacheEvict(value = "homeDataCacheValorado", allEntries = true)
    @Override
    public void limpiarCacheHomeDataCacheValorado() {
    }
    @CacheEvict(value = "homeDataCacheEscuchados", allEntries = true)
    @Override
    public void limpiarCacheHomeDataCacheEscuchados() {
    }

}
