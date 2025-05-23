package soundtribe.soundtribemusic.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponsePortadaDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongPortadaDto;
import soundtribe.soundtribemusic.entities.AlbumEntity;
import soundtribe.soundtribemusic.entities.FilePhotoEntity;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.repositories.AlbumRepository;
import soundtribe.soundtribemusic.services.AlbumService;
import soundtribe.soundtribemusic.services.ExploreService;
import soundtribe.soundtribemusic.services.SongService;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
public class ExploreServiceImpl implements ExploreService {

    private static final int MAX_ALBUMS_RETURNED = 15;
    private static final int MAX_SONGS_RETURNED = 15;

    private final AlbumService albumService;
    private final AlbumRepository albumRepository;
    private final SongService songService;

    @Autowired
    public ExploreServiceImpl(
            AlbumService albumService,
            AlbumRepository albumRepository,
            SongService songService
    ) {
        this.albumService = albumService;
        this.albumRepository = albumRepository;
        this.songService = songService;
    }

    /**
     * Méto-do para devolver álbumes en la pantalla de exploración.
     *
     * Lógica de filtrado:
     * 1. Sin name ni género: devuelve todos los álbumes
     * 2. Solo name: filtra por coincidencia de nombre
     * 3. Solo género: filtra álbumes donde la mayoría de canciones sean del género especificado
     * 4. Ambos parámetros: aplica ambos filtros
     *
     * Todos los resultados se ordenan por playcount descendente y se limitan a 15 álbumes.
     *
     * @param name Nombre del álbum a buscar (opcional)
     * @param genero ID del género a filtrar (opcional)
     * @return Lista de ResponseAlbumDto ordenada por popularidad, máximo 15 elementos
     */
    @Override
    public List<ResponseAlbumDto> explorarAlbumes(String name, Long genero) {
        List<AlbumEntity> allAlbums = albumService.getAll();

        return filterAlbums(allAlbums, name, genero)
                .map(album -> albumService.mapperAlbum(album.getId()))
                .sorted(byPlayCountDescending())
                .limit(MAX_ALBUMS_RETURNED)
                .toList();
    }

    /**
     * Méto-do para devolver canciones en la pantalla de exploración.
     * Lógica de filtrado:
     * 1. Sin name ni género: devuelve todas las canciones
     * 2. Solo name: filtra por coincidencia de nombre
     * 3. Solo género: filtra canciones donde contenga género especificado
     * 4. Ambos parámetros: aplica ambos filtros
     *
     * Todos los resultados se ordenan por playcount descendente y se limitan a 15 canciones
     *
     * @param name Nombre de la canción a buscar (opcional)
     * @param genero ID del género a filtrar (opcional)
     * @return Lista de ResponseSongPortadaDto ordenada por popularidad, máximo 15 elementos
     */
    @Override
    public List<ResponseSongPortadaDto> explorarCanciones(String name, Long genero) {
        List<SongEntity> allSongs = songService.getAll();

        return filterSongs(allSongs, name, genero)
                .map(this::mapSongToResponseSongPortadaDto)
                .sorted(bySongPlayCountDescending())
                .limit(MAX_SONGS_RETURNED)
                .toList();
    }

    /**
     * Aplica los filtros correspondientes según los parámetros recibidos para álbumes
     */
    private Stream<AlbumEntity> filterAlbums(List<AlbumEntity> albums, String name, Long genero) {
        Stream<AlbumEntity> stream = albums.stream();

        if (hasValidGenre(genero)) {
            stream = stream.filter(createGenreFilter(genero));
        }

        if (hasValidName(name)) {
            stream = stream.filter(createNameFilter(name));
        }

        return stream;
    }

    /**
     * Aplica los filtros correspondientes según los parámetros recibidos para canciones
     */
    private Stream<SongEntity> filterSongs(List<SongEntity> songs, String name, Long genero) {
        Stream<SongEntity> stream = songs.stream();

        if (hasValidGenre(genero)) {
            stream = stream.filter(createSongGenreFilter(genero));
        }

        if (hasValidName(name)) {
            stream = stream.filter(createSongNameFilter(name));
        }

        return stream;
    }

    /**
     * Crea un filtro para álbumes por género.
     * Un álbum coincide si más de la mitad de sus canciones pertenecen al género especificado.
     */
    private Predicate<AlbumEntity> createGenreFilter(Long genero) {
        return album -> {
            List<SongEntity> songs = album.getSongs();
            if (songs.isEmpty()) {
                return false;
            }

            long matchingSongs = songs.stream()
                    .filter(song -> containsGenre(song, genero))
                    .count();

            return matchingSongs >= (songs.size() / 2);
        };
    }

    /**
     * Crea un filtro para canciones por género.
     * Una canción coincide si contiene el género especificado.
     */
    private Predicate<SongEntity> createSongGenreFilter(Long genero) {
        return song -> containsGenre(song, genero);
    }

    /**
     * Crea un filtro para álbumes por nombre (case-insensitive)
     */
    private Predicate<AlbumEntity> createNameFilter(String name) {
        String lowerCaseName = name.toLowerCase();
        return album -> album.getName().toLowerCase().contains(lowerCaseName);
    }

    /**
     * Crea un filtro para canciones por nombre (case-insensitive)
     */
    private Predicate<SongEntity> createSongNameFilter(String name) {
        String lowerCaseName = name.toLowerCase();
        return song -> song.getName().toLowerCase().contains(lowerCaseName);
    }

    /**
     * Verifica si una canción contiene el género especificado
     */
    private boolean containsGenre(SongEntity song, Long genero) {
        return song.getGeneros().stream()
                .anyMatch(g -> g.getId().equals(genero));
    }

    /**
     * Mapea una SongEntity a ResponseSongPortadaDto
     */
    private ResponseSongPortadaDto mapSongToResponseSongPortadaDto(SongEntity song) {

        ResponseSongDto songDto = songService.getSongDto(song.getId());

        AlbumEntity album = albumRepository.findAlbumBySongId(song.getId())
                .orElseThrow(() -> new RuntimeException("No se encontró un álbum para la canción con ID: " + song.getId()));

        FilePhotoEntity photo = album.getPhoto();

        ResponsePortadaDto portada = ResponsePortadaDto.builder()
                .id(photo.getId())
                .fileName(photo.getFileName())
                .fileUrl(photo.getFileUrl())
                .build();

        return ResponseSongPortadaDto.builder()
                .id(songDto.getId())
                .name(songDto.getName())
                .description(songDto.getDescription())
                .duration(songDto.getDuration())
                .owner(songDto.getOwner())
                .fileUrl(songDto.getFileUrl())
                .genero(songDto.getGenero())
                .subgenero(songDto.getSubgenero())
                .estilo(songDto.getEstilo())
                .artistasFt(songDto.getArtistasFt())
                .likes(songDto.getLikes())
                .dislike(songDto.getDislike())
                .slug(songDto.getSlug())
                .playCount(songDto.getPlayCount())
                .portada(portada)
                .build();
    }

    /**
     * Comparador para ordenar álbumes por playcount descendente
     */
    private Comparator<ResponseAlbumDto> byPlayCountDescending() {
        return Comparator.comparing(ResponseAlbumDto::getAllPlaysCount).reversed();
    }

    /**
     * Comparador para ordenar canciones por playcount descendente
     */
    private Comparator<ResponseSongPortadaDto> bySongPlayCountDescending() {
        return Comparator.comparing(ResponseSongPortadaDto::getPlayCount).reversed();
    }

    /**
     * Valida si el parámetro de género es válido
     */
    private boolean hasValidGenre(Long genero) {
        return genero != null;
    }

    /**
     * Valida si el parámetro de nombre es válido
     */
    private boolean hasValidName(String name) {
        return StringUtils.hasText(name);
    }
}