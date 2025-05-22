package soundtribe.soundtribemusic.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soundtribe.soundtribemusic.services.impl.EliminateAccountFromMicroserviceImpl;

@RestController
@RequestMapping("/eliminate-music") // Puerto 8085
public class EliminateAccountController {

    @Autowired
    private EliminateAccountFromMicroserviceImpl eliminateAccountFromMicroservice;

    @DeleteMapping
    public ResponseEntity<?> eliminarDonacionesDelUsuario(@RequestHeader("Authorization") String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no proporcionado o inválido");
        }

        String jwt = token.replace("Bearer ", "");

        try {
            eliminateAccountFromMicroservice.eliminateByAccount(jwt);
            return ResponseEntity.ok("Donaciones actualizadas correctamente (donor eliminado)");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al eliminar donor: " + e.getMessage());
        }
    }
}
