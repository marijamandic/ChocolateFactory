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
import beans.Factory;
import beans.LoginRequest;
import beans.LoginResponse;
import beans.User;
import beans.enums.Role;
import dao.ChocolateDAO;
import dao.FactoryDAO;
import dao.UserDAO;

@Path("/users")
public class UserService{
	@Context
	ServletContext ctx;
	@Context
    HttpServletRequest request;
	
	private UserDAO userDAO;
	
	public UserService(@Context ServletContext ctx) {
		String contextPath = ctx.getRealPath("");
		userDAO = new UserDAO(contextPath);
	}
	
	@GET
    @Path("/init")
    @Produces(MediaType.TEXT_PLAIN)
    public String initMessage() {
        return "UserDAO initialized";
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<User> getAllUsers() {
		return userDAO.findAll();
	}
	
	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserById(@PathParam("id") String id) {
		User user = userDAO.findById(id);
        if (user != null) {
            return user;
        } 
        return null;
    }
	
	@GET
    @Path("/getByUsername/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserByUsername(@PathParam("username") String username) {
		User user = userDAO.findUserByUsername(username);
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
	    System.out.println("Received user: " + user.getUsername());
	    User addedUser = userDAO.addUser(user);
	    System.out.println("Added user: " + addedUser.getUsername());
	    return Response.ok(addedUser).build();
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(LoginRequest user) {
	    String username = user.getUsername();
	    String password = user.getPassword();
	    String token = userDAO.authenticate(username, password);

	    if (token != null) {
	        return Response.ok("{\"token\":\"" + token + "\"}").build();
	    } else {
	        return Response.status(Response.Status.UNAUTHORIZED)
	                       .entity("{\"error\":\"Invalid username or password\"}")
	                       .build();
	    }
	}

	@GET
    @Path("/getFactory/{managerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String findFactoryByManager(@PathParam("managerId") String id) {
		String factory = userDAO.getFactoryByManager(id);
        if (factory != null) {
            return factory;
        } 
        return null;
    }
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("id") String id, User updatedUser) {
		boolean success = userDAO.updateUser(updatedUser);
		if (success) {
            return Response.ok("User updated successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
	}
	
	@GET
	@Path("/check-username/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkUsername(@PathParam("username") String username) {
	    boolean exists = userDAO.checkUsername(username);

	    return Response.ok(exists).build();
	}
}