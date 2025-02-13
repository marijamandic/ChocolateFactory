package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Chocolate;
import beans.Factory;
import dao.ChocolateDAO;
import dao.FactoryDAO;

@Path("/factories")
public class FactoryService {
	@Context
	ServletContext ctx;
	    
	public FactoryService() {}
	    
	@PostConstruct
	public void init() {
		if (ctx.getAttribute("factoryDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("factoryDAO", new FactoryDAO(contextPath));
			System.out.println("FactoryDAO initialized");
		}
		if (ctx.getAttribute("chocolateDAO") == null) {
            ctx.setAttribute("chocolateDAO", new ChocolateDAO());
            System.out.println("ChocolateDAO initialized");
        }
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Factory> getAllFactories() {
		FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
		return dao.findAll();
	}
	
	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFactoryById(@PathParam("id") String id) {
		FactoryDAO factoryDAO = (FactoryDAO) ctx.getAttribute("factoryDAO");
	    ChocolateDAO chocolateDAO = (ChocolateDAO) ctx.getAttribute("chocolateDAO");

	    Factory factory = factoryDAO.findFactoryById(id);
	    if (factory == null) {
	        return Response.status(Response.Status.NOT_FOUND).build();
	    }

	    Collection<Chocolate> chocolatesForFactory = factory.getChocolates();
	    for (Chocolate chocolate : chocolatesForFactory) {
	        Chocolate choco = chocolateDAO.findChocolateById(chocolate.getId());
	        if (choco != null) {
	            chocolatesForFactory.add(choco);
	        }
	    }

	    factory.setChocolates(chocolatesForFactory);
	    return Response.ok(factory).build();
    }
}
