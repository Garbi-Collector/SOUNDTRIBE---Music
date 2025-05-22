package soundtribe.soundtribemusic.services.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongDto;
import soundtribe.soundtribemusic.entities.AlbumEntity;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.repositories.AlbumRepository;
import soundtribe.soundtribemusic.repositories.AlbumVoteRepository;
import soundtribe.soundtribemusic.repositories.SongRepository;
import soundtribe.soundtribemusic.repositories.SongVoteRepository;
import soundtribe.soundtribemusic.services.AlbumService;
import soundtribe.soundtribemusic.services.HomeDataService;
import soundtribe.soundtribemusic.services.SongService;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Servicio responsable de cargar la información para la página de inicio (Home).
 *
 * <p>Esta vista presentará cinco secciones principales de álbumes/canciones:</p>
 *
 * <ul>
 *   <li><strong>Álbumes recientes</strong>: muestra los 10 álbumes más nuevos, ordenados por su fecha de creación.</li>
 *   <li><strong>Álbumes más valorados</strong>: muestra los 5 álbumes con mayor cantidad de "likes" o votos positivos. historico</li>
 *   <li><strong>Álbumes más escuchados</strong>: muestra los 10 álbumes más reproducidos, calculado sumando el número total de reproducciones de todas sus canciones.
 *   historico</li>
 *   <li><strong>Canciones onfire</strong>: muestra las 10 canciones más likeadas y escuchadas de la semana actual</li>
 *   <li><strong>Canciones mas bien votadas</strong>: muestra las 10 canciones más likeadas.
 *   historico</li>
 * </ul>
 */

@Service
public class HomeDataServiceImpl implements HomeDataService {


    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private AlbumVoteRepository albumVoteRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongVoteRepository songVoteRepository;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private SongService songService;


    /**
     * Devuelve un listado de los 10 álbumes más recientes.
     * @return List<ResponseAlbumDto>
     */
    @Cacheable("homeDataCacheReciente")
    @Transactional(readOnly = true)
    @Override
    public List<ResponseAlbumDto> getAlbumesMasRecientes() {
        return albumRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(album -> albumService.mapperAlbum(album.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve un listado de los 10 álbumes más valorados (con más likes).
     * historicamente
     * @return List<ResponseAlbumDto>
     */
    @Cacheable("homeDataCacheValorado")
    @Transactional(readOnly = true)
    @Override
    public List<ResponseAlbumDto> getAlbumesMasValorados() {
        return albumRepository.findTop10ByOrderByLikeCountDesc().stream()
                .map(album -> albumService.mapperAlbum(album.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve un listado de los 10 álbumes más escuchados (con más playcounts en sus canciones).
     * historicamente
     * @see soundtribe.soundtribemusic.entities.SongEntity
     * @return List<ResponseAlbumDto>
     */
    @Cacheable("homeDataCacheEscuchados")
    @Transactional(readOnly = true)
    @Override
    public List<ResponseAlbumDto> getAlbumesMasEscuchados() {
        return albumRepository.findTop10MostPlayedAlbums().stream()
                .map(album -> albumService.mapperAlbum(album.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve un listado de los 10 canciones ON FIRE (con mas play count y mg).
     * semanal
     * @see soundtribe.soundtribemusic.entities.SongEntity
     * @return List<ResponseSongDto>
     */
    @Transactional(readOnly = true)
    @Override
    public List<ResponseSongDto> getCancionesOnFire() {
        return songRepository.findTop10SongsOnFire().stream()
                .map(song -> songService.getSongDto(song.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseSongDto> getCancionesMasLikeadas() {
        return songRepository.findTop10MostLikedSongs().stream()
                .map(song -> songService.getSongDto(song.getId()))
                .collect(Collectors.toList());
    }

    /**
     * este metodo sirve para aquellas listas que solo son canciones,
     * en principal para mostrar a que album pertenece con un subtitulo
     * "en el album x" ademas de usar la imagen del album para presentar a la cancion
     * @param idSong
     * @return
     */
    @Transactional(readOnly = true)
    @Override
    public ResponseAlbumDto getAlbumBySong(Long idSong) {
        SongEntity song = songRepository.findById(idSong)
                .orElseThrow(() -> new EntityNotFoundException("Canción no encontrada con ID: " + idSong));
        AlbumEntity album = song.getAlbum();

        if (album == null) {
            throw new EntityNotFoundException("La canción no está asociada a ningún álbum.");
        }

        return albumService.mapperAlbum(album.getId());
    }

}
