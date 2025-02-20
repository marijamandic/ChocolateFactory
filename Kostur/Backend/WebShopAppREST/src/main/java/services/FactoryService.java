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
import dao.UserDAO;

@Path("/factories")
public class FactoryService {
	@Context
	ServletContext ctx;

	private FactoryDAO factoryDAO;
	private ChocolateDAO chocolateDAO;
	
	public FactoryService(@Context ServletContext ctx) {
		String contextPath = ctx.getRealPath("");
		factoryDAO = new FactoryDAO(contextPath);
		chocolateDAO = new ChocolateDAO(contextPath);
	}
	
	@GET
    @Path("/init")
    @Produces(MediaType.TEXT_PLAIN)
    public String initMessage() {
        return "FactoryDAO initialized";
    }

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Factory> getAllFactories() {
		return factoryDAO.findAll();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFactoryById(@PathParam("id") String id) {
		Factory factory = factoryDAO.findFactoryById(id);
		if (factory == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		Collection<Chocolate> chocolatesForFactory = chocolateDAO.findByFactory(id);

		factory.setChocolates(chocolatesForFactory);
		return Response.ok(factory).build();
	}
}
