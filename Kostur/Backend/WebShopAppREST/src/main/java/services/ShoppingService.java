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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Shopping;
import beans.User;
import dao.ShoppingDAO;

@Path("/shoppings")
public class ShoppingService{
	@Context
	ServletContext ctx;
	@Context
    HttpServletRequest request;
	
	private ShoppingDAO shoppingDAO;
	
	public ShoppingService(@Context ServletContext ctx) {
		String contextPath = ctx.getRealPath("");
		shoppingDAO = new ShoppingDAO(contextPath);
	}
	
	@GET
    @Path("/init")
    @Produces(MediaType.TEXT_PLAIN)
    public String initMessage() {
        return "ShoppingDAO initialized";
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Shopping> getAllShoppings() {
		return shoppingDAO.findAll();
	}
	
	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Shopping getById(@PathParam("id") String id) {
		Shopping shopping = shoppingDAO.findById(id);
        if (shopping != null) {
            return shopping;
        } 
        return null;
    }
	
	@GET
    @Path("getByUser/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Shopping> getByUserId(@PathParam("id") String id) {
		return shoppingDAO.findByUserId(id);
    }
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addShopping(Shopping shopping) {
	    Shopping addedShopping = shoppingDAO.addShopping(shopping);
	    System.out.println("Added shopping: " + addedShopping.getId());
	    return Response.ok(addedShopping).build();
	}
	
	@PUT
	@Path("/remove/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeShopping(@PathParam("id") String id) {
		Shopping shopping = shoppingDAO.findById(id);
	    boolean removedShopping = shoppingDAO.removeShopping(shopping);
	    System.out.println("Removed shopping: " + shopping.getId());
	    if (removedShopping) {
            return Response.ok("Shopping removed successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Shopping not found").build();
        }
	}
	
}