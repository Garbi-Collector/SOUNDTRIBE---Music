package soundtribe.soundtribemusic.services.impl;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class SlugGenerator {

    private static final String URL_SAFE_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SLUG_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public String generateSlug() {
        StringBuilder slug = new StringBuilder(SLUG_LENGTH);
        for (int i = 0; i < SLUG_LENGTH; i++) {
            int index = random.nextInt(URL_SAFE_CHARS.length());
            slug.append(URL_SAFE_CHARS.charAt(index));
        }
        return slug.toString();
    }
}