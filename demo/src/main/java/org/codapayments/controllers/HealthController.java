package org.codapayments.controllers;

import io.vertx.core.http.HttpServerRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.codapayments.dto.GamerPoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/health")
public class HealthController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response hello() {
        return Response.ok().build();
    }
}
