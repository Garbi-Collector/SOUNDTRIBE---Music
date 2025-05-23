package soundtribe.soundtribemusic.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import soundtribe.soundtribemusic.dtos.dashboard.*;

import java.util.List;

@Service
public interface DashboardService {
    boolean haveASongs(String jwt);

    @Cacheable("dashboardsCache")
    Long getPlayCount(String jwt);

    @Cacheable("dashboardsCache")
    List<DashboardSong> getTopOfMySongs(String jwt);

    @Cacheable("generoMasEscuchadoCache")
    List<DashboardGeneroTop> generoMasEscuchado(String jwt);

    @Cacheable("generoTopGlobalCache")
    DashboardGeneroTopGlobal getGeneroTopGlobal();

    @Cacheable("subgeneroTopGlobalCache")
    DashboardSubGeneroTopGlobal getSubGeneroTopGlobal();

    @Cacheable("estiloTopGlobalCache")
    DashboardEstiloTopGlobal getEstiloTopGlobal();
}
