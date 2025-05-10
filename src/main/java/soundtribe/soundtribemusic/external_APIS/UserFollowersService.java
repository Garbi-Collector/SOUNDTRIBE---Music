package soundtribe.soundtribemusic.external_APIS;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import soundtribe.soundtribemusic.dtos.user.UserGet;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserFollowersService {

    private final RestTemplate restTemplate;

    @Value("${user.back.url}")
    private String userServiceBaseUrl;

    public List<UserGet> getFollowers(String jwtToken) {
        String url = userServiceBaseUrl + "/user/followers";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UserGet[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    UserGet[].class
            );

            UserGet[] body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("No se recibieron seguidores desde el microservicio de usuario.");
            }

            return Arrays.asList(body);
        } catch (Exception e) {
            // Podés crear una excepción custom si querés
            throw new RuntimeException("Error al obtener los seguidores: " + e.getMessage(), e);
        }
    }
}
