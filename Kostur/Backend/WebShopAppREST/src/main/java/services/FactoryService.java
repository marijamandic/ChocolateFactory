package services;

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

import beans.Factory;
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
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Factory> getAllFactories() {
		FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
		return dao.getFilteredFactories();
	}
	
	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFactoryById(@PathParam("id") String id) {
		FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        Factory factory = dao.findFactoryById(id);
        if (factory != null) {
            return Response.ok(factory).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
	
}
