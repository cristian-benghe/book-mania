Here we will test the use case corresponding to the functionalities of updating and deleting a book. 
To test this functionality, we can use the following sequence of requests to our API.

 - Create a user using the registering endpoint.
![img.png](screenshots/update_delete_book_flow/add-user.png)
 - Add the user as an admin via the password using the endpoint for adding an admin.
![img.png](screenshots/update_delete_book_flow/add-admin.png)
 - Add a book to the collection using the endpoint for adding a book.
![img.png](screenshots/update_delete_book_flow/add-book.png)
 - Update the previously added book using the endpoint for updating a book.
![img.png](screenshots/update_delete_book_flow/update-book.png)
 - Verify that the book has been updated by using the endpoint for getting a book.
![img.png](screenshots/update_delete_book_flow/verify-update-book.png)
 - Delete the previously added book using the endpoint for deleting a book.
![img.png](screenshots/update_delete_book_flow/delete-book.png)
 - Verify that the book has been deleted by using the endpoint for getting a book.
![img.png](screenshots/update_delete_book_flow/verify-delete-book.png)

As you can see, the book has been updated and deleted successfully.