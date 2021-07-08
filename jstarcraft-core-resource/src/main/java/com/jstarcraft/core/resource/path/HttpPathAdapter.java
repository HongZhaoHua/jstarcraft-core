package com.jstarcraft.core.resource.path;

import java.io.InputStream;
import java.util.function.Function;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpPathAdapter implements PathAdapter {

    private RestTemplate restTemplate;

    private HttpMethod httpMethod;

    private Function<String, String> urlFunction;

    private Function<String, HttpEntity> entityFunction;

    public HttpPathAdapter(RestTemplate restTemplate, HttpMethod httpMethod, String url) {
        this(restTemplate, httpMethod, url, new HttpEntity<>(null, new HttpHeaders()));
    }

    public HttpPathAdapter(RestTemplate restTemplate, HttpMethod httpMethod, String url, HttpEntity entity) {
        this(restTemplate, httpMethod, (path) -> {
            return url + path;
        }, (path) -> {
            return entity;
        });
    }

    public HttpPathAdapter(RestTemplate restTemplate, HttpMethod httpMethod, Function<String, String> urlFunction, Function<String, HttpEntity> entityFunction) {
        this.restTemplate = restTemplate;
        this.httpMethod = httpMethod;
        this.urlFunction = urlFunction;
        this.entityFunction = entityFunction;
    }

    @Override
    public InputStream getStream(String path) throws Exception {
        String url = urlFunction.apply(path);
        HttpEntity request = entityFunction.apply(path);
        ResponseEntity<Resource> response = restTemplate.exchange(url, httpMethod, request, Resource.class);
        Resource resource = response.getBody();
        InputStream stream = resource.getInputStream();
        return stream;
    }

}
