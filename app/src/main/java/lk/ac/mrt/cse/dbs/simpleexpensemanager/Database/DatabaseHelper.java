package lk.ac.mrt.cse.dbs.simpleexpensemanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper dbHelper = null;
    private static final int VERSION = 1;
    public static final String DATABASE_NAME = "ExpenseManager.db";

    public static final String TABLE1_NAME = "account_info";
    public static final String TABLE1_COL_1 = "accountNo";
    public static final String TABLE1_COL_2 = "bankName";
    public static final String TABLE1_COL_3 = "accountHolderName";
    public static final String TABLE1_COL_4 = "balance";

    public static final String TABLE2_NAME = "transaction_info";
    public static final String TABLE2_COL_0 = "transaction_ID";
    public static final String TABLE2_COL_1 = "date";
    public static final String TABLE2_COL_2 = "accountNo";
    public static final String TABLE2_COL_3 = "expenseType";
    public static final String TABLE2_COL_4 = "amount";



    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null,VERSION);
    }

    public static DatabaseHelper getInstance(Context context){
        if (dbHelper==null)
            dbHelper = new DatabaseHelper(context);
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE1_NAME + " (" +
                    TABLE1_COL_1 + " varchar(15),"+
                    TABLE1_COL_2 + " varchar(20),"+
                    TABLE1_COL_3 + " varchar(25),"+
                    TABLE1_COL_4 + " numeric(12,2) check("+TABLE1_COL_4+">0),"+
                   "primary key("+TABLE1_COL_1+") );");

        db.execSQL("create table "+TABLE2_NAME + " (" +
                TABLE2_COL_0 + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                TABLE2_COL_1 + " INTEGER,"+
                TABLE2_COL_2 + " varchar(20),"+
                TABLE2_COL_3 + " varchar(25),"+
                TABLE2_COL_4 + " numeric(12,2) check("+TABLE2_COL_4+">0)"+
                " );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE1_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE2_NAME);
        onCreate(db);
    }
    public List<Account> getAccounts(){
        List<Account> accountList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE1_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String accountNO = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHoldersName = cursor.getString(2);
                double balance = Double.parseDouble(cursor.getString(3));

                Account account = new Account(accountNO,bankName,accountHoldersName,balance);
                accountList.add(account);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return accountList;
    }

    public void addAccount(Account account){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE1_COL_1,account.getAccountNo());
        contentValues.put(TABLE1_COL_2,account.getBankName());
        contentValues.put(TABLE1_COL_3,account.getAccountHolderName());
        contentValues.put(TABLE1_COL_4,account.getBalance());

        sqLiteDatabase.insert(TABLE1_NAME,null,contentValues);
        sqLiteDatabase.close();
    }

    public void removeAccount(Account account){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(TABLE1_NAME, TABLE1_COL_1 + " = ?",
                new String[] { String.valueOf(account.getAccountNo()) });

        sqLiteDatabase.close();
    }

    public void updateAccount(Account account){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE1_COL_4,account.getBalance());

        sqLiteDatabase.update(TABLE1_NAME,contentValues,TABLE1_COL_1 + " = ?",
                new String[] { String.valueOf(account.getAccountNo())});

        sqLiteDatabase.close();
    }

    public void addTransaction(Transaction transaction){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE2_COL_1,new SimpleDateFormat("dd/MM/yyyy").format(transaction.getDate()));
        contentValues.put(TABLE2_COL_2,transaction.getAccountNo());
        contentValues.put(TABLE2_COL_3,transaction.getExpenseType().toString());
        contentValues.put(TABLE2_COL_4,transaction.getAmount());

        sqLiteDatabase.insert(TABLE2_NAME,null,contentValues);
        sqLiteDatabase.close();
    }

    public List<Transaction> getTransactions(){
        List<Transaction> transactionList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE2_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            Date date = null;
            do {
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(1));
                }
                catch (Exception e){}

                String accountNO = cursor.getString(2);
                String expenseType = cursor.getString(3);
                ExpenseType expenseTypeEn;
                if (expenseType.equals("EXPENSE"))
                    expenseTypeEn = ExpenseType.EXPENSE;
                else
                    expenseTypeEn = ExpenseType.INCOME;
                double amount = Double.parseDouble(cursor.getString(4));

                Transaction transaction = new Transaction(date,accountNO,expenseTypeEn,amount);
                transactionList.add(transaction);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return transactionList;
    }
}
