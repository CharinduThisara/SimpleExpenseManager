package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.Database.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;

public class PersistentTransactionDAO extends MainActivity implements TransactionDAO {
    private final List<Transaction> transactions;
    private DatabaseHelper dbHelper;
    private Context context;

    public PersistentTransactionDAO(Context context) {
        transactions = new LinkedList<>();
        this.context = context;
        this.dbHelper =  DatabaseHelper.getInstance(context);
        addTransactionsStartUp(dbHelper.getTransactions());

    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);
        dbHelper.addTransaction(transaction);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
    private void addTransactionsStartUp(List<Transaction> transactionList){

        for(Transaction transaction : transactionList) {
            transactions.add(transaction);
        }
    }
}
