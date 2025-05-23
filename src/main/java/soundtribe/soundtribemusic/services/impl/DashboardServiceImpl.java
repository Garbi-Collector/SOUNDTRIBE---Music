package soundtribe.soundtribemusic.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soundtribe.soundtribemusic.dtos.dashboard.*;
import soundtribe.soundtribemusic.entities.EstiloEntity;
import soundtribe.soundtribemusic.entities.GeneroEntity;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.entities.SubgeneroEntity;
import soundtribe.soundtribemusic.external_APIS.ExternalJWTService;
import soundtribe.soundtribemusic.repositories.SongRepository;
import soundtribe.soundtribemusic.services.DashboardService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ExternalJWTService externalJWTService;
    @Autowired
    private SongRepository songRepository;


    @Override
    public boolean haveASongs(String jwt){

        System.out.println("el token que llega al service es: "+ jwt);

        Map<String, Object> userInfo = externalJWTService.validateToken(jwt);
        Boolean isUser = (Boolean) userInfo.get("valid");

        if (!Boolean.TRUE.equals(isUser)) {
            throw new RuntimeException("No eres un usuario válido");
        }
        Integer userIdInteger = (Integer) userInfo.get("userId");
        Long userId = userIdInteger.longValue();
        List<SongEntity> songs = songRepository.findByOwner(userId);

        return !songs.isEmpty();
    }


    /**
     * cantidad de play count de un artista
     */
    @Cacheable("getPlayCountCache")
    @Transactional(readOnly = true)
    @Override
    public Long getPlayCount(String jwt){
        Map<String, Object> userInfo = externalJWTService.validateToken(jwt);
        Boolean isUser = (Boolean) userInfo.get("valid");

        if (!Boolean.TRUE.equals(isUser)) {
            throw new RuntimeException("No eres un usuario válido");
        }
        Integer userIdInteger = (Integer) userInfo.get("userId");
        Long userId = userIdInteger.longValue();

        Long playCount = 0L;

        List<SongEntity> songs = songRepository.findByOwner(userId);

        if (songs.isEmpty()) {
            return null;
        }
        for (SongEntity song: songs){
            playCount = playCount + song.getPlayCount();
        }
        return playCount;
    }

    /**
     * top de canciones mas escuchadas por play count, top 10.
     */
    @Cacheable("getTopOfMySongsCache")
    @Transactional(readOnly = true)
    @Override
    public List<DashboardSong> getTopOfMySongs(String jwt){
        Map<String, Object> userInfo = externalJWTService.validateToken(jwt);
        Boolean isUser = (Boolean) userInfo.get("valid");

        if (!Boolean.TRUE.equals(isUser)) {
            throw new RuntimeException("No eres un usuario válido");
        }
        Integer userIdInteger = (Integer) userInfo.get("userId");
        Long userId = userIdInteger.longValue();

        List<DashboardSong> dashboardSongs = new ArrayList<>();
        List<SongEntity> songs = songRepository.findTop10ByOwnerOrderByPlayCountDesc(userId);

        if (songs.isEmpty()) {
            return null;
        }
        for (SongEntity song: songs){
            dashboardSongs.add(
                    DashboardSong.builder()
                            .nameSong(song.getName())
                            .playCount(song.getPlayCount())
                            .slug(song.getSlug())
                            .build()
            );
        }
        return dashboardSongs;
    }

    /**
     * genero mas escuchado del artista (un grafico de torta mostrando, de sus canciones, cual es el genero mas reproducido)
     */
    @Cacheable("generoMasEscuchadoCache")
    @Transactional(readOnly = true)
    @Override
    public List<DashboardGeneroTop> generoMasEscuchado  (String jwt){
        Map<String, Object> userInfo = externalJWTService.validateToken(jwt);
        Boolean isUser = (Boolean) userInfo.get("valid");

        if (!Boolean.TRUE.equals(isUser)) {
            throw new RuntimeException("No eres un usuario válido");
        }
        Integer userIdInteger = (Integer) userInfo.get("userId");
        Long userId = userIdInteger.longValue();

        List<SongEntity> songs = songRepository.findByOwner(userId);
        if (songs.isEmpty()) {
            return null;
        }

        Map<String, Long> generoPlayCounts = new HashMap<>();

        long totalPlayCount = 0L;

        for (SongEntity song : songs) {
            Long songPlayCount = song.getPlayCount() != null ? song.getPlayCount() : 0L;

            if (song.getGeneros() != null) {
                for (GeneroEntity genero : song.getGeneros()) {
                    generoPlayCounts.merge(genero.getName(), songPlayCount, Long::sum);
                }
            }

            totalPlayCount += songPlayCount;
        }
        // Transformar a lista DTO
        long finalTotalPlayCount = totalPlayCount;
        List<DashboardGeneroTop> topGeneros = generoPlayCounts.entrySet().stream()
                .map(entry -> DashboardGeneroTop.builder()
                        .nameGenero(entry.getKey())
                        .playCount(entry.getValue())
                        .percent(finalTotalPlayCount == 0 ? 0.0 : (entry.getValue() * 100.0) / finalTotalPlayCount)
                        .build())
                .sorted((a, b) -> Long.compare(b.getPlayCount(), a.getPlayCount())) // opcional: ordenar
                .collect(Collectors.toList());

        return topGeneros;
    }


    @Cacheable("generoTopGlobalCache")
    @Transactional(readOnly = true)
    @Override
    public DashboardGeneroTopGlobal getGeneroTopGlobal() {
        List<SongEntity> allSongs = songRepository.findAll();
        if (allSongs.isEmpty()) return null;

        Map<String, Long> generoPlayCounts = new HashMap<>();
        Map<String, String> generoFestejos = new HashMap<>();

        for (SongEntity song : allSongs) {
            Long count = song.getPlayCount() != null ? song.getPlayCount() : 0L;
            if (song.getGeneros() != null) {
                for (GeneroEntity genero : song.getGeneros()) {
                    generoPlayCounts.merge(genero.getName(), count, Long::sum);
                    generoFestejos.putIfAbsent(genero.getName(), genero.getFestejo());
                }
            }
        }

        return generoPlayCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> DashboardGeneroTopGlobal.builder()
                        .nameGenero(entry.getKey())
                        .playCount(entry.getValue())
                        .festejo(generoFestejos.get(entry.getKey()))
                        .build())
                .orElse(null);
    }

    @Cacheable("subgeneroTopGlobalCache")
    @Transactional(readOnly = true)
    @Override
    public DashboardSubGeneroTopGlobal getSubGeneroTopGlobal() {
        List<SongEntity> allSongs = songRepository.findAll();
        if (allSongs.isEmpty()) return null;

        Map<String, Long> subgeneroPlayCounts = new HashMap<>();
        Map<String, String> subgeneroFestejos = new HashMap<>();

        for (SongEntity song : allSongs) {
            Long count = song.getPlayCount() != null ? song.getPlayCount() : 0L;
            if (song.getSubgeneros() != null) {
                for (SubgeneroEntity subgenero : song.getSubgeneros()) {
                    subgeneroPlayCounts.merge(subgenero.getName(), count, Long::sum);
                    subgeneroFestejos.putIfAbsent(subgenero.getName(), subgenero.getFestejo());
                }
            }
        }

        return subgeneroPlayCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> DashboardSubGeneroTopGlobal.builder()
                        .nameSubGenero(entry.getKey())
                        .playCount(entry.getValue())
                        .festejo(subgeneroFestejos.get(entry.getKey()))
                        .build())
                .orElse(null);
    }

    @Cacheable("estiloTopGlobalCache")
    @Transactional(readOnly = true)
    @Override
    public DashboardEstiloTopGlobal getEstiloTopGlobal() {
        List<SongEntity> allSongs = songRepository.findAll();
        if (allSongs.isEmpty()) return null;

        Map<String, Long> estiloPlayCounts = new HashMap<>();
        Map<String, String> estiloFestejos = new HashMap<>();

        for (SongEntity song : allSongs) {
            Long count = song.getPlayCount() != null ? song.getPlayCount() : 0L;
            if (song.getEstilos() != null) {
                for (EstiloEntity estilo : song.getEstilos()) {
                    estiloPlayCounts.merge(estilo.getName(), count, Long::sum);
                    estiloFestejos.putIfAbsent(estilo.getName(), estilo.getFestejo());
                }
            }
        }

        return estiloPlayCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> DashboardEstiloTopGlobal.builder()
                        .nameEstilo(entry.getKey())
                        .playCount(entry.getValue())
                        .festejo(estiloFestejos.get(entry.getKey()))
                        .build())
                .orElse(null);
    }



}
