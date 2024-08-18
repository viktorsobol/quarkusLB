package org.codapayments.controllers;

import io.vertx.core.http.HttpServerRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.codapayments.dto.GamerPoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/gamer")
public class GamerStatsController {

    private final Logger logger = LoggerFactory.getLogger(GamerStatsController.class);


    @Context
    HttpServerRequest request;

    @POST
    @Path("/game_points")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response hello(GamerPoints gamerPoints) {
        logger.info("Received request for gamer points: {}", gamerPoints.gamerId());
        return Response.ok(gamerPoints).build();
    }
}
