package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import beans.Comment;

public class CommentDAO {
	private Map<String, Comment> comments = new HashMap<>();
	
	public CommentDAO() {}
	
	public CommentDAO(String contextPath) {
		loadComments(contextPath);
	}
	
	private void loadComments(String contextPath) {
        try (BufferedReader in = new BufferedReader(new FileReader(new File(contextPath + "/comments.csv")))) {
            String line;
            in.readLine();

            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) 
                    continue;

                String[] tokens = line.split(",");
                if (tokens.length == 5) {
                    String id = tokens[0].trim();
                    String customerId = tokens[1].trim();
                    String factoryId = tokens[2].trim();
                    String text = tokens[3].trim();
                    int rating = Integer.parseInt(tokens[4].trim());

                    comments.put(id, new Comment(id, customerId, factoryId, text, rating));
                } else {
                    System.err.println("Invalid comment entry: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public Collection<Comment> findCommentsByFactoryId(String factoryId) {
		Collection<Comment> commentsFactory = new ArrayList<>();
		for(Comment comment : comments.values()) {
			if(comment.getFactoryId().equals(factoryId)) {
				commentsFactory.add(comment);
			}
		}
		return commentsFactory;
	}
	
	public double calculateAverageRating(String factoryId) {
		Collection<Comment> comments = findCommentsByFactoryId(factoryId);
        if (comments.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Comment comment : comments) {
            sum += comment.getRating();
        }
        return sum / comments.size();
    }
}