package com.trungdoan.assessment.busservice.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trungdoan.assessment.busservice.exception.JsonParsingException;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.Map;

@Log4j2
public class RestClientSupport {

    /**
     * Default connection timeout in 60 seconds
     */
    private static final int CONNECT_TIMEOUT_SECONDS = 60000;
    /**
     * Default maximum connections is 100
     */
    private static final int MAX_CONNECTIONS = 100;

    private RestTemplate restTemplate;

    @PostConstruct
    public void initRestTemplate() {
        // Add connection pooling
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(MAX_CONNECTIONS);
        connManager.setDefaultMaxPerRoute(MAX_CONNECTIONS);

        HttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // Add timeout custom for request
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT_SECONDS);
        requestFactory.setReadTimeout(CONNECT_TIMEOUT_SECONDS);
        requestFactory.setConnectionRequestTimeout(CONNECT_TIMEOUT_SECONDS);

        restTemplate = new RestTemplate(requestFactory);
    }

    /*
        Call API without request param
     */
    protected final <T> T get(String baseUri, @Nullable HttpHeaders headers, TypeReference<T> refType) {
        return get(baseUri, headers, refType, null);
    }

    protected final <T> T get(String baseUri, TypeReference<T> refType) {
        return get(baseUri, null, refType, null);
    }


    protected final <T> T get(String baseUri, @Nullable HttpHeaders headers, TypeReference<T> refType, Map<String, Object> params) {
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        return deserializer(exchange(getURI(baseUri, params), HttpMethod.GET, httpEntity, params), refType);
    }

    private String getURI(String baseUri, @Nullable Map<String, Object> parameters) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUri);
        if (parameters != null && !parameters.isEmpty()) {
            /*
                Make it to a map to handle special character in param
             */
            parameters.forEach((k, v) -> uriBuilder.queryParam(k, "{" + k + "}"));
        }
        return uriBuilder.build().toUriString();
    }

    private byte[] exchange(String uri, HttpMethod method, HttpEntity<?> requestEntity, @Nullable Map<String, Object> params) {
        ResponseEntity<byte[]> response = params != null
                ? restTemplate.exchange(uri, method, requestEntity, byte[].class, params)
                : restTemplate.exchange(uri, method, requestEntity, byte[].class);
        // +++
        if (response.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new HttpClientErrorException(response.getStatusCode(), "Message is failed.URI: " + uri + " statusCode: " + response.getStatusCodeValue());
        }
        return response.getBody();
    }

    /*
        To handle case external API response String type
     */
    private static <T> T deserializer(final byte[] rawData, TypeReference<T> refType) {
        Assert.notNull(rawData, "raw data must not be null.");
        try {
            // +++ Support return origin data as plain text
            final Class<?> clazz = (Class<?>) (refType.getType() instanceof ParameterizedType ?
                    ((ParameterizedType) refType.getType()).getRawType() : refType.getType());
            if (clazz.isAssignableFrom(String.class)) {
                return (T) new String(rawData);
            }
            return objectMapper().readValue(rawData, refType);
        } catch (IOException e) {
            log.error("Failed to Deserialize response");
            throw new JsonParsingException(e);
        }
    }

    private static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new MillisOrLocalDateTimeDeserializer());
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}
