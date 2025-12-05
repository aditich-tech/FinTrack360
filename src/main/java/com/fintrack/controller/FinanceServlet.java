package com.fintrack.controller;

import com.fintrack.model.Budget;
import com.fintrack.model.Expense;
import com.fintrack.model.Goal;
import com.fintrack.model.User;
import com.fintrack.service.FinanceService;
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

@WebServlet("/api/finance/*")
public class FinanceServlet extends HttpServlet {
    private FinanceService financeService = new FinanceService();
    private com.fintrack.dao.RecurringTransactionDAO recurringDAO = new com.fintrack.dao.RecurringTransactionDAO();
    private com.fintrack.dao.UserDAO userDAO = new com.fintrack.dao.UserDAO();
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            resp.sendError(401, "Unauthorized");
            return;
        }

        String path = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if ("/expenses".equals(path)) {
            List<Expense> expenses = financeService.getExpenses(user.getId());
            out.print(gson.toJson(expenses));
        } else if ("/budgets".equals(path)) {
            List<Budget> budgets = financeService.getBudgets(user.getId());
            out.print(gson.toJson(budgets));
        } else if ("/goals".equals(path)) {
            List<Goal> goals = financeService.getGoals(user.getId());
            out.print(gson.toJson(goals));
        } else if ("/incomes".equals(path)) {
            List<com.fintrack.model.Income> incomes = financeService.getIncomes(user.getId());
            out.print(gson.toJson(incomes));
        } else if ("/recurring".equals(path)) {
            List<com.fintrack.model.RecurringTransaction> list = recurringDAO.getByUserId(user.getId());
            out.print(gson.toJson(list));
        } else {
            resp.setStatus(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            resp.sendError(401, "Unauthorized");
            return;
        }

        String path = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if ("/expenses".equals(path)) {
            Expense expense = gson.fromJson(req.getReader(), Expense.class);
            expense.setUserId(user.getId());
            if (financeService.addExpense(expense)) {
                out.print(gson.toJson(new ResponseMessage("Expense added")));
            } else {
                resp.setStatus(500);
            }
        } else if ("/budgets".equals(path)) {
            Budget budget = gson.fromJson(req.getReader(), Budget.class);
            budget.setUserId(user.getId());
            if (financeService.addBudget(budget)) {
                out.print(gson.toJson(new ResponseMessage("Budget created")));
            } else {
                resp.setStatus(500);
            }
        } else if ("/goals".equals(path)) {
            Goal goal = gson.fromJson(req.getReader(), Goal.class);
            goal.setUserId(user.getId());
            if (financeService.addGoal(goal)) {
                out.print(gson.toJson(new ResponseMessage("Goal created")));
            } else {
                resp.setStatus(500);
            }
        } else if ("/incomes".equals(path)) {
            com.fintrack.model.Income income = gson.fromJson(req.getReader(), com.fintrack.model.Income.class);
            income.setUserId(user.getId());
            if (financeService.addIncome(income)) {
                out.print(gson.toJson(new ResponseMessage("Income added")));
            } else {
                resp.setStatus(500);
            }
        } else if ("/recurring".equals(path)) {
            com.fintrack.model.RecurringTransaction rt = gson.fromJson(req.getReader(),
                    com.fintrack.model.RecurringTransaction.class);
            rt.setUserId(user.getId());
            if (recurringDAO.create(rt)) {
                out.print(gson.toJson(new ResponseMessage("Recurring transaction created")));
            } else {
                resp.setStatus(500);
            }
        } else {
            resp.setStatus(404);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            resp.sendError(401, "Unauthorized");
            return;
        }

        String path = req.getPathInfo();
        if ("/recurring".equals(path)) {
            int id = Integer.parseInt(req.getParameter("id"));
            if (recurringDAO.delete(id)) {
                resp.setStatus(200);
            } else {
                resp.setStatus(500);
            }
        }
    }

    private User getAuthenticatedUser(HttpServletRequest req) {
        // First check request attributes (set by JwtAuthFilter)
        String email = (String) req.getAttribute("userEmail");
        if (email != null) {
            return userDAO.findByEmail(email);
        }

        // Fallback to session (for legacy support or admin console if not updated)
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }

    class ResponseMessage {
        String message;

        ResponseMessage(String message) {
            this.message = message;
        }
    }
}
