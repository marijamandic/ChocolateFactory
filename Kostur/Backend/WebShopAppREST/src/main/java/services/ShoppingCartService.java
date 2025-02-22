package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Chocolate;
import beans.ShoppingCart;
import beans.User;
import dao.ShoppingCartDAO;

@Path("/shoppingCarts")
public class ShoppingCartService{
	@Context
	ServletContext ctx;
	@Context
    HttpServletRequest request;
	
	private ShoppingCartDAO shoppingCartDAO;
	
	public ShoppingCartService(@Context ServletContext ctx) {
		String contextPath = ctx.getRealPath("");
		shoppingCartDAO = new ShoppingCartDAO(contextPath);
	}
	
	@GET
    @Path("/init")
    @Produces(MediaType.TEXT_PLAIN)
    public String initMessage() {
        return "ShoppingCartDAO initialized";
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ShoppingCart> getAllShoppingCarts(){
		return shoppingCartDAO.findAll();
	}
	
	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createCart(ShoppingCart shoppingCart) {
		ShoppingCart addedShoppingCart = shoppingCartDAO.addCart(shoppingCart);
	    System.out.println("Added ShoppingCart: " + addedShoppingCart.getId());
	    return Response.ok(addedShoppingCart).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ShoppingCart findById(@PathParam("id") String id) {
		ShoppingCart shoppingCart = shoppingCartDAO.findById(id);
		if(shoppingCart != null) {
			return shoppingCart;
		}
		return null;
	}
	
	@PUT
	@Path("/add/{id}/{chocoId}")
	public Response addToCart(@PathParam("id") String id, @PathParam("chocoId") String chocolateId){
		ShoppingCart updated = shoppingCartDAO.addToCart(id, chocolateId);
		if(updated != null)
			return Response.ok("Shopping cart updated successfully").build();
		else
			return Response.status(Response.Status.NOT_FOUND).entity("Not found").build();
	}
	
	@PUT
	@Path("/remove/{id}/{chocoId}")
	public Response removeFromCart(@PathParam("id") String id, @PathParam("chocoId") String chocolateId){
		ShoppingCart updated = shoppingCartDAO.removeFromCart(id, chocolateId);
		if(updated != null)
			return Response.ok("Shopping cart updated successfully").build();
		else
			return Response.status(Response.Status.NOT_FOUND).entity("Not found").build();
	}
}