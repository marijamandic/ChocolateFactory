package services;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if (isPreflightRequest(request)) {
            // Ovdje zaustavljamo preflight zahtjev, odgovaramo sa 200 OK
            request.abortWith(Response.ok().build());
            return;
        }
    }

    private static boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null && "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        System.out.println("CORS filter applied for request: " + request.getMethod() + " from " + request.getHeaderString("Origin"));

        response.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000"); // Dozvoli samo frontend
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.getHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");

        if (isPreflightRequest(request)) {
            response.setStatus(200);
        }
    }

}
