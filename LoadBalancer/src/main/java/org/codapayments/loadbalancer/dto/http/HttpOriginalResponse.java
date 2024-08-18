package org.codapayments.loadbalancer.dto.http;

import org.codapayments.loadbalancer.dto.OriginalResponse;

public class HttpOriginalResponse implements OriginalResponse {
    private final byte[] body;
    private final int statusCode;

    public HttpOriginalResponse(byte[] body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public byte[] getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
