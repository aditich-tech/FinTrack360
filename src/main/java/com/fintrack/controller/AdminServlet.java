package com.fintrack.controller;

import com.fintrack.model.SecurityLog;
import com.fintrack.model.User;
import com.fintrack.service.SecurityService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {
    private SecurityService securityService = new SecurityService();
    private com.fintrack.dao.UserDAO userDAO = new com.fintrack.dao.UserDAO();
    private com.fintrack.dao.ExpenseDAO expenseDAO = new com.fintrack.dao.ExpenseDAO();
    private com.fintrack.dao.BudgetDAO budgetDAO = new com.fintrack.dao.BudgetDAO();
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getAuthenticatedAdmin(req);
        if (user == null) {
            resp.sendError(403, "Access Denied");
            return;
        }

        String path = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if ("/logs".equals(path)) {
            List<SecurityLog> logs = securityService.getLogs();
            out.print(gson.toJson(logs));
        } else if ("/users".equals(path)) {
            List<User> users = userDAO.getAllUsers();
            out.print(gson.toJson(users));
        } else if ("/stats".equals(path)) {
            int userCount = userDAO.getUserCount();
            int expenseCount = expenseDAO.getExpenseCount();
            int budgetCount = budgetDAO.getBudgetCount();
            out.print(String.format("{\"userCount\": %d, \"expenseCount\": %d, \"budgetCount\": %d}", userCount,
                    expenseCount, budgetCount));
        } else {
            resp.setStatus(404);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getAuthenticatedAdmin(req);
        if (user == null) {
            resp.sendError(403, "Access Denied");
            return;
        }

        String path = req.getPathInfo();
        if ("/users".equals(path)) {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                if (userDAO.deleteUser(id)) {
                    resp.setStatus(200);
                    resp.getWriter().write("{\"message\": " + "\"User deleted\"" + "}");
                } else {
                    resp.setStatus(500);
                }
            } else {
                resp.setStatus(400);
            }
        } else {
            resp.setStatus(404);
        }
    }

    private User getAuthenticatedAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null && "ADMIN".equals(user.getRole())) {
                return user;
            }
        }
        return null;
    }
}
