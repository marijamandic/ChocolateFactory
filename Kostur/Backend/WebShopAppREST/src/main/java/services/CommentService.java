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

import beans.Comment;
import dao.CommentDAO;
import dao.UserDAO;

@Path("/comments")
public class CommentService {
	@Context
	ServletContext ctx;
	    
	private CommentDAO commentDAO;
	
	public CommentService(@Context ServletContext ctx) {
		String contextPath = ctx.getRealPath("");
		commentDAO = new CommentDAO(contextPath);
	}
	
	@GET
    @Path("/init")
    @Produces(MediaType.TEXT_PLAIN)
    public String initMessage() {
        return "CommentDAO initialized";
    }
	
	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Comment> getCommentsByFactoryId(@PathParam("id") String id) {
        return commentDAO.findCommentsByFactoryId(id);
    }
}
