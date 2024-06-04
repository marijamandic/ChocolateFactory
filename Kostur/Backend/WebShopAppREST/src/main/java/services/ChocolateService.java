package services;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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

import beans.Chocolate;
import dao.ChocolateDAO;

@Path("/chocolates")
public class ChocolateService {
	@Context
	ServletContext ctx;
	@Context
    HttpServletRequest request;

	
	public ChocolateService() {}
	
	@PostConstruct
	public void init() {
		if (ctx.getAttribute("chocolateDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("chocolateDAO", new ChocolateDAO(contextPath));
		}
	}
	
	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Chocolate getChocolateById(@PathParam("id") String id) {
		ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        Chocolate chocolate = dao.findChocolateById(id);
        if (chocolate != null) {
            return chocolate;
        } 
        return null;
    }
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addChocolate(Chocolate chocolate) { 
	        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
	        //chocolate.setFactory(.getChocolateFactory());
	        dao.addChocolate(chocolate);
	        return Response.ok().build();
	}
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
    public Response updateChocolate(@PathParam("id") String id, Chocolate chocolate) {
            ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
            dao.updateChocolate(id, chocolate);
            return Response.ok().build();
    }
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProduct(@PathParam("id") String id) {
            ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
            dao.deleteChocolate(id);
            return Response.ok().build();
        
	}

}
