package org.codapayments.loadbalancer.dto.http;

import org.codapayments.loadbalancer.dto.OriginalRequest;

public class HttpOriginalRequest implements OriginalRequest {
    private final byte[] body;
    private final String uri;
    private final HttpMethod httpMethod;

    public HttpOriginalRequest(byte[] body, String uri, HttpMethod httpMethod) {
        this.body = body;
        this.uri = uri;
        this.httpMethod = httpMethod;
    }

    public byte[] getBody() {
        return body;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

}
