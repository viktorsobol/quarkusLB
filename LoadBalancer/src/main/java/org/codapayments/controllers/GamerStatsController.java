package org.codapayments.controllers;

import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.codapayments.exceptions.DownstreamException;
import org.codapayments.loadbalancer.HttpLoadBalancer;
import org.codapayments.loadbalancer.dto.http.HttpMethod;
import org.codapayments.loadbalancer.dto.http.HttpOriginalRequest;
import org.codapayments.loadbalancer.dto.http.HttpOriginalResponse;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;


@Path("/gamer")
public class GamerStatsController {

    @Context
    HttpServerRequest request;

    @Inject
    HttpLoadBalancer httpLoadBalancer;

    @ServerExceptionMapper
    public RestResponse<String> mapException(DownstreamException e) {
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @POST
    @Path("/game_points")
    @Produces("*/*")
    @Consumes("*/*")
    public RestResponse<byte[]> hello(byte[] requestBody) {
        HttpOriginalResponse response = httpLoadBalancer.route(
                new HttpOriginalRequest(requestBody, request.uri(), HttpMethod.POST)
        );
        return RestResponse.status(
                Response.Status.fromStatusCode(response.getStatusCode()),
                response.getBody());
    }
}
