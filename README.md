An Remove row button has been added. This button is disabled when no row (or the total cost row) is selected. When a valid row is selected in the table, the button is enabled. When clicked, the button removes the row from the table and disables the button (as no row is now selected). When a row is removed, the total cost is recomputed and updated in the table to reflect the change.

Test: New test 1-6 are written in the test file. In order to complete all necessary tests, new files are imported and listeners are now initiated in the test file. 
test1 - tests add transaction with amount 50.0 and category "food" using the view component, make sure the transaction is added to the table and total is reflected
test2 - tests that when invalid input is entered, the responding error message is returned. Test also checks to make sure the total cost remains unchanged
test3 - test with three transactions with different amounts and same category that only the targetted amount is filtered by the amount filter, and the amount matches expecation
test4 - test with three transations with different categories and same amount that only the targetted category is filtered by the category filter, and the category matches expectation
test5 - test that when attempting to undo while the transaction is empty - UI widget button is disabled 
test6 - test adds a transaction and when a row is selected, the button is enabled, the transaction is removed, and the total cost is updated correctly.