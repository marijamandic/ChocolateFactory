package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/comments.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					String id = st.nextToken().trim();
					String customerId = st.nextToken().trim();
	                String factoryId = st.nextToken().trim();
	                String text = st.nextToken().trim();
	                int rating = Integer.parseInt(st.nextToken().trim());
	                comments.put(id, new Comment(id, customerId, factoryId, text, rating));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
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