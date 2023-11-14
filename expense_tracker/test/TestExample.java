// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;


public class TestExample {
  
    private ExpenseTrackerModel model;
    private ExpenseTrackerView view;
    private ExpenseTrackerController controller;

    @Before
    public void setup() {
        model = new ExpenseTrackerModel();
        view = new ExpenseTrackerView();
        controller = new ExpenseTrackerController(model, view);

        //add listeners

        view.getUndoButton().addActionListener(e -> {
            if (view.getUndoButtonEnabled()) {
                // Get transaction data from view
                int rowToRemove = view.getSelectedRow();
                //Call controller to remove row
                controller.removeTransaction(rowToRemove);
                view.setUndoButtonEnabled(false);
            }
        });

        view.getTransactionsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {

                if (!event.getValueIsAdjusting() ) { //only trigger once

                    int row  = view.getTransactionsTable().getSelectedRow();
                    int length = view.getTransactionsTable().getRowCount();

                    if ( row == -1 || row == length-1) {//dont delete last row with totals
                    view.setUndoButtonEnabled(false);
                    view.setSelectedRow(row);

                    }
                    else {

                    view.setUndoButtonEnabled(true);
                    view.setSelectedRow(row);
                    }
                }
            }
        });
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

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }



    @Test
    public void testRemoveTransactionDisallowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, view.getTransactionsTable().getRowCount());
    
	    // Perform the action: Test to ensure the button is disabled when the list of transactions is empty
        assertEquals(view.getUndoButtonEnabled(), false);
    
        // Post-condition: List of transactions is empty
        assertEquals(0, view.getTransactionsTable().getRowCount());
    
    }

    @Test
    public void testRemoveTransactionAllowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, view.getTransactionsTable().getRowCount());
    
        //perform the action: add a transaction with amount 50.00 and category "food"
        double amount = 50.00;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Pre-condition: List of transactions contains only
	    //                the added transaction
        //check the contents of the list, making sure amount and category are displayed correctly
        assertEquals(amount, view.getTransactionsTable().getValueAt(0,1));
        assertEquals(category, view.getTransactionsTable().getValueAt(0,2));

        //post-condition: the total cost is at the total row is equal to the only added transaction
        assertEquals(amount,view.getTransactionsTable().getValueAt(1,3));


	
        // Precondition: Test to ensure the button is disabled when no row selected
        assertEquals(false, view.getUndoButtonEnabled());
        

        //Perform the action: select a row 0
        view.getTransactionsTable().setRowSelectionInterval(0, 0);

        // Perform the test: row is selected and button is enabled
        assertEquals(0, view.getSelectedRow());
        assertEquals(true, view.getUndoButtonEnabled());

        //Perform the action: remove the selected row
        view.getUndoButton().doClick();

        //Post-condition: no selected rows 
        assertEquals(-1, view.getSelectedRow());
        // Precondition: Test to ensure the button is disabled after row deletion
        assertEquals(false, view.getUndoButtonEnabled());


    
        // Post-condition: List of transactions contains only to total cost row
        assertEquals(1, view.getTableModel().getRowCount());
    
        // Check the total cost after removing the transaction

        //post-condition: the total cost is at the total row is equal to 0
        assertEquals(0.0,view.getTransactionsTable().getValueAt(0,3));

    }
    
}
