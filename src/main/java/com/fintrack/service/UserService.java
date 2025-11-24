package com.fintrack.service;

import com.fintrack.dao.UserDAO;
import com.fintrack.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public boolean register(String name, String email, String password, String role) {
        // Check if email exists
        if (userDAO.findByEmail(email) != null) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(name, email, hashedPassword, role);
        return userDAO.registerUser(user);
    }

    public User login(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }
}
