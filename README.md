Undo Functionality:
An Remove row button has been added. This button is disabled when no row (or the total cost row) is selected. When a valid row is selected in the table, the button is enabled. When clicked, the button removes the row from the table and disables the button (as no row is now selected). When a row is removed, the total cost is recomputed and updated in the table to reflect the change.
In Controller:
Added publicRemoveTransaction method, takes in an int representin which row to remove, and removes it from both the model and the view and then refreshes
In View:
Added getUndoButton method that returns the undoButton
Added getSelectedRow method that returns the index of the selected row of the table
Added setSelectedRow method that sets a row as selecte given the index
Added setUndoButtonEnabled method that sets the undo button as enabled
Added getUndoButtonEnabled method that returns the isEnabled boolean of the undo button
In ExpenseTrackerApp file:
Added listener to undo button of the view so that when the button is clicked, it tells the controller to remove the transation from the selected row, which is reported from the view
Added a transaction table row selection listner which would update the view's selected row and set the undo button enabled 


Test: 
New test 1-6 are written in the test file. In order to complete all necessary tests, new files are imported and listeners are now initiated in the test file. 
test1 - tests add transaction with amount 50.0 and category "food" using the view component, make sure the transaction is added to the table and total is reflected
test2 - tests that when invalid input is entered, the responding error message is returned. Test also checks to make sure the total cost remains unchanged
test3 - test with three transactions with different amounts and same category that only the targetted amount is filtered by the amount filter, and the amount matches expecation
test4 - test with three transations with different categories and same amount that only the targetted category is filtered by the category filter, and the category matches expectation
test5 - test that when attempting to undo while the transaction is empty - UI widget button is disabled 
test6 - test adds a transaction and when a row is selected, the button is enabled, the transaction is removed, and the total cost is updated correctly.