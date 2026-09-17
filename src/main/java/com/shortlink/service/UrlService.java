package com.shortlink.service;

import com.shortlink.dto.UrlResponse;
import com.shortlink.entity.Url;
import com.shortlink.exception.UrlNotFoundException;
import com.shortlink.repository.UrlRepository;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String BASE_URL = "http://localhost:8080/";

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlResponse createShortUrl(String originalUrl) {
        validateHttpUrl(originalUrl);

        String shortCode = generateUniqueShortCode();
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortCode(shortCode);
        url.setCreatedAt(LocalDateTime.now());
        url.setClickCount(0L);
        urlRepository.save(url);

        return new UrlResponse(shortCode, BASE_URL + shortCode, null, null);
    }

    public String redirectToOriginalUrl(String shortCode) {
        Url url = findUrl(shortCode);
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
        return url.getOriginalUrl();
    }

    public UrlResponse getUrlStats(String shortCode) {
        Url url = findUrl(shortCode);
        return new UrlResponse(url.getShortCode(), null, url.getOriginalUrl(), url.getClickCount());
    }

    private Url findUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            StringBuilder builder = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                builder.append(ALPHANUMERIC.charAt(ThreadLocalRandom.current().nextInt(ALPHANUMERIC.length())));
            }
            shortCode = builder.toString();
        } while (urlRepository.findByShortCode(shortCode).isPresent());
        return shortCode;
    }

    private void validateHttpUrl(String originalUrl) {
        try {
            URI uri = URI.create(originalUrl);
            if (uri.getScheme() == null || uri.getHost() == null
                    || !(uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("originalUrl must be a valid HTTP or HTTPS URL");
            }
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("originalUrl must be a valid HTTP or HTTPS URL");
        }
    }
}
