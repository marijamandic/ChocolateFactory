package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Chocolate;
import beans.LoginRequest;
import beans.LoginResponse;
import beans.User;
import dao.ChocolateDAO;
import dao.UserDAO;

@Path("/users")
public class UserService{
	@Context
	ServletContext ctx;
	@Context
    HttpServletRequest request;
	
	public UserService() {}
	
	@PostConstruct
	public void init() {
		if (ctx.getAttribute("userDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("userDAO", new UserDAO(contextPath));
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<User> getAllUsers() {
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		return dao.findAll();
	}
	
	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserById(@PathParam("id") String id) {
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		User user = dao.findById(id);
        if (user != null) {
            return user;
        } 
        return null;
    }
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addUser(User user) {
	    UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
	    System.out.println("Received user: " + user.getUsername());
	    User addedUser = dao.addUser(user);
	    System.out.println("Added user: " + addedUser.getUsername());
	    return Response.ok(addedUser).build();
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(LoginRequest user) {
	    UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
	    String username = user.getUsername();
	    String password = user.getPassword();
	    String token = dao.authenticate(username, password);

	    if (token != null) {
	        return Response.ok("{\"token\":\"" + token + "\"}").build();
	    } else {
	        return Response.status(Response.Status.UNAUTHORIZED)
	                       .entity("{\"error\":\"Invalid username or password\"}")
	                       .build();
	    }
	}

	
	
	
}