// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;

//import filters
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }

    public void checkTransaction(double amount, String category, Transaction transaction) {
	    assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }

    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
        double amount = 50.0;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	    //                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
        Transaction firstTransaction = model.getTransactions().get(0);
        checkTransaction(amount, category, firstTransaction);
	
	    // Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
        double amount = 50.0;
        String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	    //                the added transaction
        assertEquals(1, model.getTransactions().size());
        Transaction firstTransaction = model.getTransactions().get(0);
        checkTransaction(amount, category, firstTransaction);
	
	   // Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }

    //test1
    @Test
    public void testAddTransactionWithView(){
        //pre-condition: list of transactions is empty
        assertEquals(0, view.getTransactionsTable().getRowCount());
        
        //perform the action: add a transaction with amount 50.00 and category "food"
        double amount = 50.00;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));

        //post-condition: view displays the added transaction and a row for total
        assertEquals(2, view.getTableModel().getRowCount());

        //check the contents of the list, making sure amount and category are displayed correctly
        assertEquals(amount, view.getTransactionsTable().getValueAt(0,1));
        assertEquals(category, view.getTransactionsTable().getValueAt(0,2));

        //post-condition: the total cost is at the total row is equal to the only added transaction
        assertEquals(amount,view.getTransactionsTable().getValueAt(1,3));
    }

    //test2
    @Test
    public void invalidInputHandling(){
        //pre-condition: total cost is initially zero
        assertEquals(0.0, getTotalCost(), 0.01);
        //precondition: list of transactions is empty
        assertEquals(0, model.getTransactions().size());

        //action 1 : invalid amount with valid category
        double invalidAmount = -10.0;
        String validCategory = "food";
        assertTrue(!controller.addTransaction(invalidAmount, validCategory));

        //check error messages 
        try{
            new Transaction(invalidAmount, validCategory);
        }
        catch(IllegalArgumentException e){
            assertEquals("The amount is not valid.", e.getMessage());
        }

        //postcondition: total cost ramains zero
        assertEquals(0.0, getTotalCost(), 0.01);
        //postcondition: list of transactions stays empty
        assertEquals(0, model.getTransactions().size());

        //action 2: invalid category with valid amount
        double validAmount = 50.0;
        String invalidCategory = "test";
        assertTrue(!controller.addTransaction(validAmount, invalidCategory));

        //check error messages 
        try{
            new Transaction(validAmount, invalidCategory);
        }
        catch(IllegalArgumentException e){
            assertEquals("The category is not valid.", e.getMessage());
        }

        //postcondition: total cost remains zero
        assertEquals(0.0, getTotalCost(), 0.01);
        //postcondition: list remains empty
        assertEquals(0, model.getTransactions().size());

    }

    //test3
    @Test
    public void filterByAmount(){
        //precondition : list of transactions is empty
        assertEquals(0, model.getTransactions().size());

        //action: add multiple transactions with different amounts
        double amount1 = 50.0;
        double amount2 = 30.0;
        double amount3 = 10.0;
        String category = "food";
        
        model.addTransaction(new Transaction(amount1, category));
        model.addTransaction(new Transaction(amount2, category));
        model.addTransaction(new Transaction(amount3, category));

        //postcondition: list of transaction contains the added transactions
        assertEquals(3, model.getTransactions().size());

        //establish an amountFilter to filter out targetted transatcions
        AmountFilter amountFilter = new AmountFilter(50.0);
        List<Transaction> filteredTransactions = amountFilter.filter(model.getTransactions());

        //postcondition: verify that only the targetted transaction was returned
        assertEquals(1, filteredTransactions.size());
        assertEquals(50.0,filteredTransactions.get(0).getAmount(),0.01 );
        //another possible way of assertion
        //assertTrue(filteredTransactions.get(0).getAmount() == 50.0);
    }

    //test4
    public void filterByCategory(){
        //precondition : list of transactions is empty
        assertEquals(0, model.getTransactions().size());

        //action: add multiple transactions with different categories
        String category1 = "food";
        String category2 = "bills";
        String category3 = "other";
        double amount = 50.0;
        model.addTransaction(new Transaction(amount, category1));
        model.addTransaction(new Transaction(amount, category2));
        model.addTransaction(new Transaction(amount, category3));

        //postcondition: list of transaction contains the added transactions
        assertEquals(3, model.getTransactions().size());

        //establish an amountFilter to filter out targetted transatcions
        CategoryFilter categoryFilter = new CategoryFilter("bills");
        List<Transaction> filteredTransactions = categoryFilter.filter(model.getTransactions());

        //postcondition: verify that only the targetted transaction was returned
        assertEquals(1, filteredTransactions.size());
        assertEquals(category1, filteredTransactions.get(0).getCategory());
    }


}
