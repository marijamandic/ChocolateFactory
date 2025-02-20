package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import beans.CustomerType;
import beans.Factory;
import beans.LoginRequest;
import beans.User;
import beans.enums.Gender;
import beans.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class UserDAO {
	private Map<String, User> users = new HashMap<>();
	private String contextPath;
	private FactoryDAO factoryDAO;
	private CustomerTypeDAO customerTypeDAO;
	private static final String SECRET_KEY = "mySecretKey";

	
	public UserDAO() {}
	
	public UserDAO(String contextPath) {
		this.contextPath = contextPath;
		this.factoryDAO = new FactoryDAO(contextPath);
		this.customerTypeDAO = new CustomerTypeDAO(contextPath);
		loadUsers(contextPath);
	}
	
	private void loadUsers(String contextpath){
		String filePath = contextPath + "users.csv";
		File file = new File(filePath);
		
		if(!file.exists()) {
			System.out.println("ERROR: users.csv file not found at " + filePath);
			return;
		} else {
			System.out.println("Loading users from: " + filePath);
		}
		
		try(BufferedReader in = new BufferedReader(new FileReader(file))){
			in.readLine();	// preskakanje zaglavlja
			
			String line;
			while((line = in.readLine()) != null) {
				line = line.trim();
				if(line.isEmpty() || line.startsWith("#"))
					continue;
				
				String[] parts = line.split(",");
				if(parts.length < 11) {
					System.out.println("Invalid user entry: " + line);
					continue;
				}
				
				String id = parts[0].trim();
				String username = parts[1].trim();
				String password = parts[2].trim();
				String name = parts[3].trim();
				String surname = parts[4].trim();
				Gender gender = Gender.valueOf(parts[5].trim());
				SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
				
	            Date birthday = date.parse(parts[6].trim());
	            System.out.println(date);
				
				Role role = Role.valueOf(parts[7].trim());
				
				
				Factory factory = factoryDAO.findFactoryById(parts[8].trim());
				double score = Double.parseDouble(parts[9].trim());
				CustomerType customerType = customerTypeDAO.findById(parts[10].trim());				
				
				if(role != Role.CUSTOMER && role != Role.MANAGER) {
					users.put(id, new User(id, username, password, name, surname, gender, birthday, role));
				} else if(role == Role.CUSTOMER) {
					users.put(id, new User(id, username, password, name, surname, gender, birthday, role, customerType, score));
				} else if(role == Role.MANAGER) {
					users.put(id,  new User(id, username, password, name, surname, gender, birthday, role, factory));
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Error while writing user to file.");
		}
	}
	
	public User findById(String id) {
		User user = users.get(id);
		if(user == null) {
			System.out.println("User with id: " + id + " not found in the users map.");
		}
		return user;
	}
	
	public List<User> findAll(){
		return users.values().stream()
                .filter(user -> !user.getRole().equals(Role.ADMIN))
                .collect(Collectors.toList());
	}
	
	public String generateNewId() {
	    int maxId = 0;

	    String filePath = contextPath + "users.csv";
	    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] fields = line.split(",");
	            String idString = fields[0].trim();
	            try {
	                int id = Integer.parseInt(idString);
	                if (id > maxId) {
	                    maxId = id;
	                }
	            } catch (NumberFormatException e) {
	                System.out.println("Skipping invalid ID: " + idString);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    int newId = maxId + 1;
	    
	    return Integer.toString(newId);
	}


	
	public User addUser(User user) {

	    if (users.containsKey(user.getId())) {
	        System.out.println("User with ID " + user.getId() + " already exists.");
	        return null;
	    }
	    
	    user.setId(generateNewId());
	    users.put(user.getId(), user);
	    System.out.println("User " + user.getUsername() + " added successfully.");
	    
	    
	    //TODO: promeni apsolutnu putanju u relativnu
	    String filePath = contextPath + "users.csv";
	    System.out.println("Writing to file: " + filePath);

	    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
	        if (user.getRole().equals(Role.valueOf("CUSTOMER")))
	            writer.println(customerToFileFormat(user));
	        else if (user.getRole().equals(Role.valueOf("MANAGER")))
	            writer.println(managerToFileFormat(user));
	        else if (user.getRole().equals(Role.valueOf("WORKER")))
	            writer.println(workerToFileFormat(user));
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.out.println("Error while writing user to file.");
	    }

	    return user;
	}
	
	private String customerToFileFormat(User user) {
		return user.getId() + "," + 
				user.getUsername() + "," + 
				user.getPassword() + "," +
				user.getName() + "," + 
				user.getSurname() + "," +
				user.getGender() + "," + 
				formatDate(user.getBirthday()) + "," +
				user.getRole() + "," + 
				"-1," + 
				user.getScore() + "," +
				user.getCustomerType().getId();
	}
	
	private String managerToFileFormat(User user) {
		return user.getId() + "," + 
				user.getUsername() + "," + 
				user.getPassword() + "," +
				user.getName() + "," + 
				user.getSurname() + "," +
				user.getGender() + "," + 
				formatDate(user.getBirthday()) + "," +
				user.getRole() + "," + 
				user.getFactory().getId() +"," + 
				"-1," +
				"-1";
	}
	
	private String workerToFileFormat(User user) {
		return user.getId() + "," + 
				user.getUsername() + "," + 
				user.getPassword() + "," +
				user.getName() + "," + 
				user.getSurname() + "," +
				user.getGender() + "," + 
				formatDate(user.getBirthday()) + "," +
				user.getRole() + "," + 
				"-1," + 
				"-1," +
				"-1";
	}
	
	public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
	
	public User findUserByUsername(String username) {
	    User user = users.values().stream()
	                     .filter(u -> u.getUsername().equals(username))
	                     .findFirst()
	                     .orElse(null);

	    if (user == null) {
	        System.out.println("User with username " + username + " not found in users map.");
	    }
	    return user;
	}


	public String authenticate(String username, String password) {
	    User user = findUserByUsername(username);
	    if (user != null && user.getPassword().equals(password)) {
	        LoginRequest loggedInUser = new LoginRequest(username, password);
	        System.out.println("POZIVA SE GENERATE TOKEN");
	        String token = generateToken(loggedInUser);
	        System.out.println("Generated JWT Token: " + token);
	        return token;
	    } else {
	        System.out.println("Invalid username or password");
	        return null;
	    }
	}


	public String generateToken(LoginRequest user) {
	    long expirationTime = 1000 * 60 * 60;
	    Date now = new Date();
	    Date expirationDate = new Date(now.getTime() + expirationTime);

	    String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
	    String payload = "{\"sub\":\"" + user.getUsername() + "\",\"exp\":\"" + expirationDate.getTime() / 1000 + "\"}";

	    String base64Header = Base64.getEncoder().encodeToString(header.getBytes());
	    String base64Payload = Base64.getEncoder().encodeToString(payload.getBytes());

	    String token = base64Header + "." + base64Payload;

	    try {
	        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
	        SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
	        sha256_HMAC.init(secret_key);
	        byte[] signatureBytes = sha256_HMAC.doFinal(token.getBytes());
	        String base64Signature = Base64.getEncoder().encodeToString(signatureBytes);

	        return token + "." + base64Signature;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}


}
