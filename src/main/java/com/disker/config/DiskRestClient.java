package com.disker.config;

import com.disker.models.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Log4j2
public class DiskRestClient {
    static public final String DISK_DOMAIN = "https://cloud-api.yandex.net/v1/disk/";
    static public final String DISK_RESOURCES = DISK_DOMAIN + "resources"; // ?path={}
    private final String urlString = UriComponentsBuilder.fromHttpUrl(DISK_RESOURCES)
            .queryParam("path", "{param}")
            .queryParam("limit", "{limit}")
            .queryParam("offset", "{offset}")
            .queryParam("sort", "{sort}")
            .encode(StandardCharsets.UTF_8)
            .build()
            .toUriString();
    private RestTemplate restTemplate;
    private String accessToken;
    private int limit;

    public DiskRestClient(String accessToken) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        restTemplate = new RestTemplateBuilder().uriTemplateHandler(factory).build();
        if (accessToken != null) {
            restTemplate.getInterceptors().add(getBearerTokenInterceptor(accessToken));
        } else {
            restTemplate.getInterceptors().add(getNoTokenInterceptor());
        }
        this.accessToken = accessToken;
    }

    private ClientHttpRequestInterceptor getBearerTokenInterceptor(String accessToken) {
        return (request, bytes, execution) -> {
            request.getHeaders().add("Authorization", "OAuth " + accessToken);
            return execution.execute(request, bytes);
        };
    }

    private ClientHttpRequestInterceptor getNoTokenInterceptor() {
        return (request, bytes, execution) -> {
            throw new IllegalStateException("Can't access the API without an access token");
        };
    }

    public String getAccessToken() {
        return accessToken;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public Item getResources(String path) {
        return restTemplate.getForEntity(urlString, Item.class, path).getBody();
    }

    public File downloadFile(String url, File localFile) {
        log.info("downloadFile : {}", url);
        return restTemplate.execute(url, HttpMethod.GET, null, clientHttpResponse -> {
            try (FileOutputStream out = new FileOutputStream(localFile); InputStream in = clientHttpResponse.getBody()) {
                StreamUtils.copy(in, out);
                return localFile;
            }
        });
    }

}
