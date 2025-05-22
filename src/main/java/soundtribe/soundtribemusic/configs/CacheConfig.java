package soundtribe.soundtribemusic.configs;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        // Cache de 1 hora
        Cache albumMapperCache = new CaffeineCache("albumMapperCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.HOURS)
                        .maximumSize(2000)
                        .build());
        // Cache de 1 hora
        Cache SongsMapperCache = new CaffeineCache("songsMapperCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.HOURS)
                        .maximumSize(2000)
                        .build());

        // Cache de 1 día
        Cache homeDataCacheReciente = new CaffeineCache("homeDataCacheReciente",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS)
                        .maximumSize(2000)
                        .build());

        // Cache de 1 día
        Cache homeDataCacheValorado = new CaffeineCache("homeDataCacheValorado",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS)
                        .maximumSize(2000)
                        .build());

        // Cache de 1 día
        Cache homeDataCacheEscuchados = new CaffeineCache("homeDataCacheEscuchados",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS)
                        .maximumSize(2000)
                        .build());

        Cache portadaCache = new CaffeineCache("portadaCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS)
                        .maximumSize(1000)
                        .build());

        Cache playSongCache = new CaffeineCache("playSongCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS)
                        .maximumSize(1000)
                        .build());

        Cache albumGetCache = new CaffeineCache("albumGetCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.DAYS)
                        .maximumSize(1000)
                        .build());


        // Agregá todos los caches al manager
        cacheManager.setCaches(Arrays.asList(
                albumMapperCache,
                SongsMapperCache,
                homeDataCacheReciente,
                homeDataCacheValorado,
                homeDataCacheEscuchados,
                portadaCache,
                playSongCache,
                albumGetCache
        ));

        return cacheManager;
    }
}
