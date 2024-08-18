package org.codapayments.loadbalancer.downstream.http;

import org.codapayments.exceptions.DownstreamException;
import org.codapayments.loadbalancer.downstream.Downstream;
import org.codapayments.loadbalancer.dto.http.HttpOriginalRequest;
import org.codapayments.loadbalancer.dto.http.HttpOriginalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.codapayments.loadbalancer.dto.http.HttpMethod.GET;

public class HttpDownstream implements Downstream<HttpOriginalRequest, HttpOriginalResponse> {

    private final Logger logger = LoggerFactory.getLogger(HttpDownstream.class);

    private final String host;

    private final String port;

    private final HttpClient client;

    public HttpDownstream(HttpDownstreamHost host) {
        this.host = host.hostUrl();
        this.port = host.port();
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public HttpOriginalResponse process(HttpOriginalRequest request) {
        return sendRequest(request);
    }

    @Override
    public boolean isAlive() {
        logger.info("Checking health of downstream: {}", this.host + ":" + this.port);
        try {
            HttpOriginalResponse response = sendRequest(new HttpOriginalRequest(new byte[]{}, "/health", GET));
            return response.getStatusCode() == 200;
        } catch (DownstreamException e) {
            return false;
        }
    }

    @Override
    public String identification() {
        return this.host + ":" + this.port;
    }

    private HttpOriginalResponse sendRequest(HttpOriginalRequest request) {
        String url = "http://" + this.host + ":" + this.port + request.getUri();

        HttpRequest httpRequest;
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.of(10, SECONDS))
                .header("Content-Type", "application/json");

        httpRequest = switch (request.getHttpMethod()) {
            case POST -> builder
                    .POST(HttpRequest.BodyPublishers.ofByteArray(request.getBody()))
                    .build();
            case GET -> builder
                    .GET()
                    .build();
        };

        try {
            HttpResponse<byte[]> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            return new HttpOriginalResponse(response.body(), response.statusCode());
        } catch (IOException | InterruptedException e) {
            logger.error("Exception occurred during downstream request");
            logger.debug("Exception occurred during ", e);
            throw new DownstreamException("Exception occurred during downstream request", e);
        }
    }

}
