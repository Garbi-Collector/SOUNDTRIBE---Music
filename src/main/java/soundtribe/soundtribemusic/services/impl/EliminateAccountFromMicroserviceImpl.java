package soundtribe.soundtribemusic.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soundtribe.soundtribemusic.entities.AlbumEntity;
import soundtribe.soundtribemusic.entities.AlbumVoteEntity;
import soundtribe.soundtribemusic.entities.SongEntity;
import soundtribe.soundtribemusic.entities.SongVoteEntity;
import soundtribe.soundtribemusic.external_APIS.ExternalJWTService;
import soundtribe.soundtribemusic.repositories.AlbumRepository;
import soundtribe.soundtribemusic.repositories.AlbumVoteRepository;
import soundtribe.soundtribemusic.repositories.SongRepository;
import soundtribe.soundtribemusic.repositories.SongVoteRepository;
import soundtribe.soundtribemusic.services.AlbumService;
import soundtribe.soundtribemusic.services.EliminateAccountFromMicroservice;
import soundtribe.soundtribemusic.services.PhotoMinioService;
import soundtribe.soundtribemusic.services.SongMinioService;

import java.util.List;
import java.util.Map;

@Service
public class EliminateAccountFromMicroserviceImpl implements EliminateAccountFromMicroservice {

    @Autowired
    private ExternalJWTService jwtService;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private AlbumVoteRepository albumVoteRepository;
    @Autowired
    private SongVoteRepository songVoteRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private SongMinioService songMinioService;
    @Autowired
    private PhotoMinioService photoMinioService;



    @Transactional
    @Async
    @Override
    public void eliminateByAccount(String token){
        Map<String, Object> userInfo = jwtService.validateToken(token);
        Boolean isUser = (Boolean) userInfo.get("valid");
        Boolean isArtista = (Boolean) userInfo.get("isArtista");
        if (!Boolean.TRUE.equals(isUser)) {
            throw new RuntimeException("No eres un usuario");
        }

        Long userId = Long.valueOf(userInfo.get("userId").toString());

        // 1. Eliminar votos de album del usuario
        List<AlbumVoteEntity> votos = albumVoteRepository.findByUserLiker(userId);

        for (AlbumVoteEntity voto : votos) {
            AlbumEntity album = voto.getAlbum();
            album.setLikeCount(album.getLikeCount() - 1); // Disminuir like
            albumRepository.save(album);
        }

        albumVoteRepository.deleteAll(votos);
        System.out.println("✅ Votos del usuario eliminados correctamente.");


        // 2. Eliminar votos de canciones del usuario

        List<SongVoteEntity> songVotes = songVoteRepository.findByUserId(userId);

        songVoteRepository.deleteAll(songVotes);
        System.out.println("✅ Votos de canciones del usuario eliminados correctamente.");

        // 3. si es un artista, eliminar sus FT.
        if (Boolean.TRUE.equals(isArtista)) {
            List<SongEntity> songsWithFt = songRepository.findByArtistaIdsContaining(userId);

            for (SongEntity song : songsWithFt) {
                List<Long> artistaIds = song.getArtistaIds();
                artistaIds.remove(userId); // Quitar el ID del artista de la lista
                song.setArtistaIds(artistaIds);
                songRepository.save(song);
            }

            System.out.println("✅ Featurings del artista eliminados correctamente.");

            // 3.1 siguiendo con el artista, eliminar sus Canciones (DB y Minio)
            List<SongEntity> songs = songRepository.findByOwner(userId);

            if (!songs.isEmpty()){
                for (SongEntity song : songs) {
                    try {
                        songMinioService.deleteSongFromMinio(song.getFileUrl());
                    } catch (Exception e) {
                        System.err.println("❌ Error eliminando canción de MinIO: " + e.getMessage());
                    }
                }
                songRepository.deleteAll(songs);
            }

            // 3.2. siguiendo con el artista, eliminar las fotos de sus albumes. (DB y Minio)
            List<AlbumEntity> albumes = albumRepository.findByOwner(userId);

            if(!albumes.isEmpty()){
                for(AlbumEntity album: albumes){
                    try {
                        photoMinioService.deleteCoverPhoto(album.getPhoto().getFileName());
                    } catch (Exception e) {
                        System.err.println("❌ Error eliminando portada del album "+ album.getName() +" de MinIO: " + e.getMessage());
                    }
                }
                // 3.3. siguiendo con el artista, eliminar sus albumes.
                albumRepository.deleteAll(albumes);
            }
        }
    }

}
