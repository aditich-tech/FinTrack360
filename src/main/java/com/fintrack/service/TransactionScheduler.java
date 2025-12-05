package com.fintrack.service;

import com.fintrack.dao.ExpenseDAO;
import com.fintrack.dao.IncomeDAO;
import com.fintrack.dao.RecurringTransactionDAO;
import com.fintrack.model.Expense;
import com.fintrack.model.Income;
import com.fintrack.model.RecurringTransaction;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class TransactionScheduler implements ServletContextListener {

    private ScheduledExecutorService scheduler;
    private RecurringTransactionDAO recurringDAO = new RecurringTransactionDAO();
    private ExpenseDAO expenseDAO = new ExpenseDAO();
    private IncomeDAO incomeDAO = new IncomeDAO();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Run once a day, with 10 seconds initial delay to ensure DB is ready
        scheduler.scheduleAtFixedRate(this::processTransactions, 10, 86400, TimeUnit.SECONDS);
    }

    private void processTransactions() {
        Date today = Date.valueOf(LocalDate.now());
        List<RecurringTransaction> dueTransactions = recurringDAO.getDueTransactions(today);

        for (RecurringTransaction rt : dueTransactions) {
            if ("EXPENSE".equals(rt.getType())) {
                Expense expense = new Expense();
                expense.setUserId(rt.getUserId());
                expense.setAmount(new java.math.BigDecimal(rt.getAmount()));
                expense.setCategory(rt.getCategory());
                expense.setDescription(rt.getDescription() + " (Recurring)");
                expense.setDate(today);
                expenseDAO.addExpense(expense);
            } else {
                Income income = new Income();
                income.setUserId(rt.getUserId());
                income.setAmount(new java.math.BigDecimal(rt.getAmount()));
                income.setSource(rt.getCategory());
                income.setDescription(rt.getDescription() + " (Recurring)");
                income.setDate(today);
                incomeDAO.addIncome(income);
            }

            // Calculate next run date
            LocalDate nextDate = rt.getNextRunDate().toLocalDate();
            switch (rt.getFrequency()) {
                case "DAILY":
                    nextDate = nextDate.plusDays(1);
                    break;
                case "WEEKLY":
                    nextDate = nextDate.plusWeeks(1);
                    break;
                case "MONTHLY":
                    nextDate = nextDate.plusMonths(1);
                    break;
            }
            recurringDAO.updateNextRunDate(rt.getId(), Date.valueOf(nextDate));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}
