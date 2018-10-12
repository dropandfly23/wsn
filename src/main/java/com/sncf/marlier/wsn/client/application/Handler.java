package com.sncf.marlier.wsn.client.application;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.spi.http.HttpExchange;

@Path("/")
public class Handler {

    @GET
    @Path("?wsdl")
    @Produces(MediaType.TEXT_PLAIN)
    public String alive() {
        return "I AM ALIVE";
    }

    @POST
    @Path("/wsn-client")
    public void notification(HttpExchange exchange) {
        System.out.println(exchange);
    }

}
