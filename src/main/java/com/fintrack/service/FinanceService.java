package com.fintrack.service;

import com.fintrack.dao.BudgetDAO;
import com.fintrack.dao.ExpenseDAO;
import com.fintrack.dao.GoalDAO;
import com.fintrack.model.Budget;
import com.fintrack.model.Expense;
import com.fintrack.model.Goal;

import java.util.List;

public class FinanceService {
    private ExpenseDAO expenseDAO = new ExpenseDAO();
    private BudgetDAO budgetDAO = new BudgetDAO();
    private GoalDAO goalDAO = new GoalDAO();

    // Expenses
    public boolean addExpense(Expense expense) {
        return expenseDAO.addExpense(expense);
    }

    public List<Expense> getExpenses(int userId) {
        return expenseDAO.getExpensesByUser(userId);
    }

    // Budgets
    public boolean addBudget(Budget budget) {
        return budgetDAO.addBudget(budget);
    }

    public List<Budget> getBudgets(int userId) {
        return budgetDAO.getBudgetsByUser(userId);
    }

    // Goals
    public boolean addGoal(Goal goal) {
        return goalDAO.addGoal(goal);
    }

    public List<Goal> getGoals(int userId) {
        return goalDAO.getGoalsByUser(userId);
    }

    // Incomes
    private com.fintrack.dao.IncomeDAO incomeDAO = new com.fintrack.dao.IncomeDAO();

    public boolean addIncome(com.fintrack.model.Income income) {
        return incomeDAO.addIncome(income);
    }

    public List<com.fintrack.model.Income> getIncomes(int userId) {
        return incomeDAO.getIncomesByUser(userId);
    }
}
