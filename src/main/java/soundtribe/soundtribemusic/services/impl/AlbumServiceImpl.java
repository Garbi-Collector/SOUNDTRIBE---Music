
package soundtribe.soundtribemusic.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soundtribe.soundtribemusic.dtos.request.RequestAlbumDto;
import soundtribe.soundtribemusic.dtos.request.RequestSongDto;
import soundtribe.soundtribemusic.dtos.response.ResponseAlbumDto;
import soundtribe.soundtribemusic.dtos.response.ResponseSongDto;
import soundtribe.soundtribemusic.entities.AlbumEntity;
import soundtribe.soundtribemusic.entities.FilePhotoEntity;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.external_APIS.ExternalJWTService;
import soundtribe.soundtribemusic.repositories.AlbumRepository;
import soundtribe.soundtribemusic.services.AlbumService;
import soundtribe.soundtribemusic.services.PhotoService;
import soundtribe.soundtribemusic.services.SongService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    private ExternalJWTService externalJWTService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongService songService;

    @Autowired
    private SlugGenerator slugGenerator;

    /**
     *  metodo especializado y central del microservicio, maneja muchas acciones
     *  como para subir canciones (en bucle) y portadas
     * @param token sirve para saber si el que usa el metodo esta validamente identificado y saber quien es
     * @param albumDto dto que sirve para encapsular las canciones
     * @return retorna un album entity
     */
    @Transactional
    @Override
    public ResponseAlbumDto uploadAlbumWithSongsAndCover(
            //seguridad
            String token,
            //album
            RequestAlbumDto albumDto
    ) {
        // 1) verificamos el token y sacamos el id del artista creador.
        Map<String, Object> userInfo = externalJWTService.validateToken(token);

        Boolean isArtista = (Boolean) userInfo.get("isArtista");
        if (!Boolean.TRUE.equals(isArtista)) {
            throw new RuntimeException("No tenés permisos para crear un álbum");
        }
        Long albumOwner = Long.parseLong(userInfo.get("userId").toString());

        // 2) guardar la portada en minio y bd
        FilePhotoEntity portadaEntity = photoService.uploadCover(albumDto.getPortada());

        // 3) guardar el album en la bd, sin canciones.
        AlbumEntity album = AlbumEntity.builder()
                .name(albumDto.getName())
                .description(albumDto.getDescription())
                .typeAlbum(albumDto.getTypeAlbum())
                .photo(portadaEntity)
                .songs(new ArrayList<>())
                .owner(albumOwner)
                .slug(generateUniqueSlug())
                .likeCount(0L)
                .build();

        album = albumRepository.save(album);

        //inicializo una lista de canciones con las canciones que llegaron por parametro
        List<RequestSongDto> songsDtos = albumDto.getSongs();

        for (RequestSongDto songsDto : songsDtos) {

            SongEntity song = songService.uploadSongWithInfo(
                    albumOwner,
                    songsDto
            );

            song.setAlbum(album);
            album.getSongs().add(song);
        }

        AlbumEntity albumsaved =albumRepository.save(album);

        return mapperAlbum(albumsaved.getId());
    }

    private String generateUniqueSlug() {
        String slug;
        do {
            slug = slugGenerator.generateSlug();
        } while (albumRepository.existsBySlug(slug));
        return slug;
    }

    @Cacheable(value = "albumMapperCache", key = "#id")
    @Transactional
    @Override
    public ResponseAlbumDto mapperAlbum(Long id){
        AlbumEntity albumE = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album no encontrado con el id: "+id));
        ResponseAlbumDto albumDto;

        List<ResponseSongDto> songs = new ArrayList<>();
        for (SongEntity song: albumE.getSongs()){
            ResponseSongDto songDTO = songService.getSongDto(song.getId());
            songs.add(songDTO);
        }


        return albumDto = ResponseAlbumDto.builder()
                .id(albumE.getId())
                .name(albumE.getName())
                .description(albumE.getDescription())
                .typeAlbum(albumE.getTypeAlbum())
                .portada(photoService.getPortadaDto(albumE.getPhoto().getId()))
                .songs(songs)
                .owner(albumE.getOwner())
                .slug(albumE.getSlug())
                .likeCount(albumE.getLikeCount())
                .build();

    }


    @Transactional(readOnly = true)
    @Override
    public List<ResponseAlbumDto> getAlbumsByOwnerId(Long ownerId) {
        List<AlbumEntity> albums = albumRepository.findByOwner(ownerId);

        if (albums.isEmpty()) {
            throw new RuntimeException("No se encontraron álbumes para el artista con id: " + ownerId);
        }

        List<ResponseAlbumDto> responseAlbums = new ArrayList<>();

        for (AlbumEntity album : albums) {
            responseAlbums.add(mapperAlbum(album.getId()));
        }

        return responseAlbums;
    }


    @Transactional
    @Override
    public ResponseAlbumDto getAlbumBySlug(String slug){
        System.out.println("el slug recibido es: " + slug);

        AlbumEntity albumE = albumRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Album no encontrado con el slug: " + slug));

        System.out.println("Álbum encontrado: " + albumE.getDescription());

        ResponseAlbumDto dto = mapperAlbum(albumE.getId());

        // Mostrar cada campo del DTO manualmente
        System.out.println("=== DTO generado ===");
        System.out.println("ID: " + dto.getId());
        System.out.println("Nombre: " + dto.getName());
        System.out.println("Descripción: " + dto.getDescription());
        System.out.println("Tipo de Álbum: " + dto.getTypeAlbum());
        System.out.println("Portada ID: " + (dto.getPortada() != null ? dto.getPortada().getId() : "null"));
        System.out.println("Portada URL: " + (dto.getPortada() != null ? dto.getPortada().getFileUrl() : "null"));
        System.out.println("Canciones:");
        if (dto.getSongs() != null && !dto.getSongs().isEmpty()) {
            for (ResponseSongDto song : dto.getSongs()) {
                System.out.println(" - ID: " + song.getId() + ", Nombre: " + song.getName());
            }
        } else {
            System.out.println(" - No hay canciones");
        }
        System.out.println("Owner: " + dto.getOwner());
        System.out.println("Slug: " + dto.getSlug());
        System.out.println("Likes: " + dto.getLikeCount());

        return dto;
    }


}
