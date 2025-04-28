package soundtribe.soundtribemusic.external_APIS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class ExternalJWTService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalJWTService.class);

    @Value("${user.back.url}")
    private String userBackUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> validateToken(String jwt) {
        String url = userBackUrl + "api/jwt/validate";

        logger.info("Validando token contra URL: {}", url);
        logger.info("Token enviado: {}", jwt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Esto es opcional, pero no hace daño aquí
        headers.set("Authorization", "Bearer " + jwt); // <-- Aca está el cambio importante

        HttpEntity<Void> request = new HttpEntity<>(headers); // No mandamos body

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        logger.info("Respuesta HTTP status: {}", response.getStatusCode());

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Respuesta exitosa: {}", response.getBody());
            return response.getBody();
        } else {
            logger.error("Error al validar el token. Status: {}", response.getStatusCode());
            throw new RuntimeException("Token inválido o error al validar");
        }
    }
}
