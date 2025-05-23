package soundtribe.soundtribemusic.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soundtribe.soundtribemusic.dtos.dashboard.*;
import soundtribe.soundtribemusic.services.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Verifica si tiene canciones
    @GetMapping("/have-songs")
    public boolean haveSongs(@RequestHeader("Authorization") String jwt) {
        System.out.println("el token que llego es:" + jwt);
        try {
            return dashboardService.haveASongs(clearToken(jwt));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Cantidad total de reproducciones del artista
    @GetMapping("/play-count")
    public Long getPlayCount(@RequestHeader("Authorization") String jwt) {
        try {
            return dashboardService.getPlayCount(clearToken(jwt));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Top 10 canciones más reproducidas
    @GetMapping("/top-songs")
    public List<DashboardSong> getTopOfMySongs(@RequestHeader("Authorization") String jwt) {
        try {
            return dashboardService.getTopOfMySongs(clearToken(jwt));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Género más escuchado (gráfico de torta)
    @GetMapping("/genero-mas-escuchado")
    public List<DashboardGeneroTop> generoMasEscuchado(@RequestHeader("Authorization") String jwt) {
        try {
            return dashboardService.generoMasEscuchado(clearToken(jwt));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Género top global
    @GetMapping("/genero-top-global")
    public DashboardGeneroTopGlobal getGeneroTopGlobal() {
        return dashboardService.getGeneroTopGlobal();
    }

    // Subgénero top global
    @GetMapping("/subgenero-top-global")
    public DashboardSubGeneroTopGlobal getSubGeneroTopGlobal() {
        return dashboardService.getSubGeneroTopGlobal();
    }

    // Estilo top global
    @GetMapping("/estilo-top-global")
    public DashboardEstiloTopGlobal getEstiloTopGlobal() {
        return dashboardService.getEstiloTopGlobal();
    }


    private String clearToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            try {
                throw new Exception("Token no proporcionado o inválido");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return token.replace("Bearer ", "");
    }

}
