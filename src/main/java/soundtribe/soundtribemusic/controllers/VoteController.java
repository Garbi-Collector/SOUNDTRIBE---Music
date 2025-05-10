package soundtribe.soundtribemusic.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soundtribe.soundtribemusic.dtos.VoteMessage;
import soundtribe.soundtribemusic.dtos.VoteResponse;
import soundtribe.soundtribemusic.models.enums.VoteType;
import soundtribe.soundtribemusic.services.VoteService;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping()
    public ResponseEntity<VoteResponse> votar(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody VoteMessage voteMessage
    ) {
        String token = bearerToken.replace("Bearer ", "");

        voteService.votar(
                token,
                voteMessage.getSongId(),
                voteMessage.getVoteType()
        );

        VoteResponse response = new VoteResponse(
                voteMessage.getSongId(),
                voteMessage.getVoteType(),
                "Voto registrado correctamente"
        );

        return ResponseEntity.ok(response);
    }

    // Endpoint para eliminar un voto
    @DeleteMapping
    public ResponseEntity<String> eliminarVoto(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam Long songId
    ) {
        String token = bearerToken.replace("Bearer ", "");

        voteService.eliminarVoto(token, songId);

        return ResponseEntity.ok("Voto eliminado correctamente");
    }

    @GetMapping("/{idSong}/isvoted")
    public ResponseEntity<?> isVoted(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long idSong,
            @RequestParam("vote") VoteType voteType
    ) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no proporcionado o inválido");
        }

        String token = bearerToken.replace("Bearer ", "");

        try {
            boolean isVoted = voteService.isVoted(token, idSong, voteType);
            return ResponseEntity.ok(isVoted); // true o false
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor.");
        }
    }



}
