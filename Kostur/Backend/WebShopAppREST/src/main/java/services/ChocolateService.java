package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Chocolate;
import beans.Factory;
import dao.ChocolateDAO;
import dao.FactoryDAO;
import dao.UserDAO;

@Path("/chocolates")
public class ChocolateService {
	@Context
	ServletContext ctx;
	@Context
	HttpServletRequest request;

	private ChocolateDAO chocolateDAO;

	public ChocolateService(@Context ServletContext ctx) {
		String contextPath = ctx.getRealPath("");
		chocolateDAO = new ChocolateDAO(contextPath);
	}

	@GET
	@Path("/init")
	@Produces(MediaType.TEXT_PLAIN)
	public String initMessage() {
		return "ChocolateDAO initialized";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Chocolate> getAllChocolates() {
		return chocolateDAO.findAll();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Chocolate getChocolateById(@PathParam("id") String id) {
		Chocolate chocolate = chocolateDAO.findChocolateById(id);
		if (chocolate != null) {
			return chocolate;
		}
		return null;
	}

	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addChocolate(Chocolate chocolate) {
		System.out.println("Received chocolate: " + chocolate.getName());
		Chocolate addedChocolate = chocolateDAO.addChocolate(chocolate);
		System.out.println("Added chocolate: " + addedChocolate.getName());
		return Response.ok(addedChocolate).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateChocolate(@PathParam("id") String id, Chocolate updatedChocolate) {
		if(!id.equals(updatedChocolate.getId())) {
			return Response.status(Response.Status.BAD_REQUEST).entity("ID in path and body must match").build();
		}
		boolean success = chocolateDAO.updateChocolate(updatedChocolate);
		if (success) {
            return Response.ok("Chocolate updated successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Chocolate not found").build();
        }
	}
	
	
	@DELETE 
	@Path("/delete/{id}")
	public Response deleteChocolate(@PathParam("id") String id) { 
		boolean success = chocolateDAO.deleteChocolate(id);
		if(success) {
			return Response.ok().build();  
		}else {
			return Response.status(Response.Status.NOT_FOUND).entity("Chocolate not found").build();
		}
	 }

}
