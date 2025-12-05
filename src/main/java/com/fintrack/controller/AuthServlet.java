package com.fintrack.controller;

import com.fintrack.model.User;
import com.fintrack.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private UserService userService = new UserService();
    private com.fintrack.service.SecurityService securityService = new com.fintrack.service.SecurityService();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if ("/login".equals(path)) {
            handleLogin(req, resp, out);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp, out);
        } else if ("/logout".equals(path)) {
            handleLogout(req, resp, out);
        } else {
            resp.setStatus(404);
            out.print(gson.toJson(new ResponseMessage("Invalid endpoint")));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws IOException {
        JsonObject json = gson.fromJson(req.getReader(), JsonObject.class);
        String email = json.get("email").getAsString();
        String password = json.get("password").getAsString();

        User user = userService.login(email, password);
        if (user != null) {
            // Generate JWT Token
            String token = com.fintrack.util.JwtUtil.generateToken(user.getEmail(), user.getRole());

            // Create response object with user and token
            JsonObject response = new JsonObject();
            response.add("user", gson.toJsonTree(user));
            response.addProperty("token", token);

            securityService.log(user.getId(), "LOGIN_SUCCESS", "User logged in successfully", req.getRemoteAddr());
            out.print(gson.toJson(response));
        } else {
            securityService.log(0, "LOGIN_FAILURE", "Failed login attempt for email: " + email, req.getRemoteAddr());
            resp.setStatus(401);
            out.print(gson.toJson(new ResponseMessage("Invalid credentials")));
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws IOException {
        JsonObject json = gson.fromJson(req.getReader(), JsonObject.class);
        String name = json.get("name").getAsString();
        String email = json.get("email").getAsString();
        String password = json.get("password").getAsString();
        String role = json.has("role") ? json.get("role").getAsString() : "USER";

        if (userService.register(name, email, password, role)) {
            securityService.log(0, "REGISTER", "New user registered: " + email, req.getRemoteAddr());
            out.print(gson.toJson(new ResponseMessage("Registration successful")));
        } else {
            resp.setStatus(400);
            out.print(gson.toJson(new ResponseMessage("Registration failed (Email might be taken)")));
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                securityService.log(user.getId(), "LOGOUT", "User logged out", req.getRemoteAddr());
            }
            session.invalidate();
        }
        out.print(gson.toJson(new ResponseMessage("Logged out")));
    }

    class ResponseMessage {
        String message;

        ResponseMessage(String message) {
            this.message = message;
        }
    }
}
