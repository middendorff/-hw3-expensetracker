import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;

public class ExpenseTrackerApp {

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    // Create MVC components
    ExpenseTrackerModel model = new ExpenseTrackerModel();
    ExpenseTrackerView view = new ExpenseTrackerView();
    ExpenseTrackerController controller = new ExpenseTrackerController(model, view);
    

    // Initialize view
    view.setVisible(true);

    view.getUndoButton().addActionListener(e -> {
      if (view.getUndoButtonEnabled()) {
        // Get transaction data from view
        int rowToRemove = view.getSelectedRow();
        //Call controller to remove row
        controller.removeTransaction(rowToRemove);
        view.setUndoButtonEnabled(false);
      }
    });




    // Handle add transaction button clicks
    view.getAddTransactionBtn().addActionListener(e -> {
      // Get transaction data from view
      double amount = view.getAmountField();
      String category = view.getCategoryField();
      
      // Call controller to add transaction
      boolean added = controller.addTransaction(amount, category);
      
      if (!added) {
        JOptionPane.showMessageDialog(view, "Invalid amount or category entered");
        view.toFront();
      }
    });

      // Add action listener to the "Apply Category Filter" button
    view.addApplyCategoryFilterListener(e -> {
      try{
      String categoryFilterInput = view.getCategoryFilterInput();
      CategoryFilter categoryFilter = new CategoryFilter(categoryFilterInput);
      if (categoryFilterInput != null) {
          // controller.applyCategoryFilter(categoryFilterInput);
          controller.setFilter(categoryFilter);
          controller.applyFilter();
      }
    }catch(IllegalArgumentException exception) {
    JOptionPane.showMessageDialog(view, exception.getMessage());
    view.toFront();
  }});


    // Add action listener to the "Apply Amount Filter" button
    view.addApplyAmountFilterListener(e -> {
      try{
      double amountFilterInput = view.getAmountFilterInput();
      AmountFilter amountFilter = new AmountFilter(amountFilterInput);
      if (amountFilterInput != 0.0) {
          controller.setFilter(amountFilter);
          controller.applyFilter();
      }
    }catch(IllegalArgumentException exception) {
    JOptionPane.showMessageDialog(view,exception.getMessage());
    view.toFront();
  }});
    
  view.getTransactionsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener(){
    public void valueChanged(ListSelectionEvent event) {

      if (!event.getValueIsAdjusting() ) { //only trigger once

        int row  = view.getTransactionsTable().getSelectedRow();
        int length = view.getTransactionsTable().getRowCount();

        if ( row == -1 || row == length-1) {//dont delete last row with totals
          view.setUndoButtonEnabled(false);
        }
        else {
          view.setUndoButtonEnabled(true);
          view.setSelectedRow(row);
        }
      }
    }
});
  }
}
