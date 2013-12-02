package edu.sjsu.cmpe.procurement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RootResource {

    public RootResource() {
        // do nothing
    }

    @GET
    public Response getRoot() {
        return Response.ok("Ok").build();
    }
}