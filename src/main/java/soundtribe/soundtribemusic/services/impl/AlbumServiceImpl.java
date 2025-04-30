package soundtribe.soundtribemusic.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
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




}