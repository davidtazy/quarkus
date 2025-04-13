package org.acme;


import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarResource {

    @Inject
    ICarCache carCache;

    // get car by id
    @GET
    @Path("/car/{id}")
    public Response getCar(@PathParam("id") String id) {
        //Config config = ConfigProvider.getConfig();
        //List<String> profiles = new ArrayList<>();
        //for (String key : config.getPropertyNames()) {
        //    if( key.startsWith("quarkus.")) {
        //        profiles.add(key + " : " + config.getValue(key, String.class));
        //    }
        //}
        //sorted list
        //profiles.sort(String::compareTo);
        //profiles.forEach(System.out::println);
        //System.out.println("***********");

        Log.info("Getting car with id: " + id);
        Car car = carCache.getCar(id);
        if (car == null) {
            Log.info("Car not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Log.info("Car found: " + car);
        return Response.ok(car).build();
    }

    // add car
    @POST
    @Path("/car")
    public Response addCar(Car car) {
        Log.info("Adding car: " + car);
        carCache.addCar(car);
        return Response.status(Response.Status.CREATED).entity(car).build();
    }

    // remove car
    @DELETE
    @Path("/car/{id}")
    public Response removeCar(@PathParam("id") String id) {
        carCache.removeCar(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}