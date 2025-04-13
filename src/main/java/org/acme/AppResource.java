package org.acme;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/api/v1/app")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
@Typed({AppResource.class, PointGenerator.class})
@ApplicationScoped
public class AppResource implements PointGenerator {

    @Inject
    ApplicationController applicationController;

    private volatile SseBroadcaster broadcaster;
    private volatile Sse sse;

    @GET
    public Response getStatus() {
        return Response.ok(applicationController.isStarted() ? "started" : "stopped").build();
    }

    @GET
    @Path("/position")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPosition() {
        Position position = applicationController.getPosition();
        return Response.ok(position).build();
    }

    @PUT
    @Path("/start")
    public Response start() {
        applicationController.start();
        return Response.ok("started").build();
    }

    @PUT
    @Path("/stop")
    public Response stop() {
        applicationController.stop();
        return Response.ok("stopped").build();
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void stream(@Context Sse sse, @Context SseEventSink sink) {
        if (broadcaster == null) {
            this.sse = sse;
            broadcaster = sse.newBroadcaster();
            broadcaster.onError((s, t) -> System.err.println("SSE error: " + t.getMessage()));
        }
        if (!sink.isClosed()) {
            broadcaster.register(sink);
        }
    }

    @Override
    public void newPoint(PolarPoint point) {
        if (broadcaster != null && sse != null) {
            broadcaster.broadcast(
                sse.newEventBuilder()
                    .name("point")
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .data(PolarPoint.class, point)
                    .build()
            );
        }
    }
}
