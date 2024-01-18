Here we will test the use case corresponding to the functionalities of displaying system-wide analytics
To test this functionality, we can use the following sequence of requests to our API.

- Create several users using the registering endpoint.
![img.png](screenshots/analytics_user_flow/add-user1.png)
![img.png](screenshots/analytics_user_flow/add-user2.png)
![img.png](screenshots/analytics_user_flow/add-user3.png)
- Make one of the users admin to be able to add books
![img.png](screenshots/analytics_user_flow/admin-user1.png)
- Create several books using the endpoint for adding a book.
![img.png](screenshots/analytics_user_flow/add-book1.png)
![img.png](screenshots/analytics_user_flow/add-book2.png)
- Set the favorite books and favorite genres of the users using the endpoint for updating a user's details
![img.png](screenshots/analytics_user_flow/change-user1.png)
![img.png](screenshots/analytics_user_flow/change-user2.png)
![img.png](screenshots/analytics_user_flow/change-user3.png)
- Enable the privacy setting of all users using the endpoint
![img.png](screenshots/analytics_user_flow/enable-collection-user1.png)
![img.png](screenshots/analytics_user_flow/enable-collection-user2.png)
![img.png](screenshots/analytics_user_flow/enable-collection-user3.png)
- Login 2 times as user1 and 2 times as user2 using the login endpoint.
![img.png](screenshots/analytics_user_flow/login-user1.png)
![img.png](screenshots/analytics_user_flow/login-user2.png)
- Fetch book1 4 times using the endpoint for getting a book
![img.png](screenshots/analytics_user_flow/get-book1.png)
- Show the analytics using the endpoint for getting the analytics
![img.png](screenshots/analytics_user_flow/fetch-analytics.png)
- Change the privacy setting of a user using the endpoint
![img.png](screenshots/analytics_user_flow/disable-collection-user1.png)
- Show the analytics again using the endpoint for getting the analytics
![img.png](screenshots/analytics_user_flow/fetch-analytics-2.png)
- Verify that the analytics (loginActivity) are different because the user's analytic data is purged

As you can see by the result of calling the analytics endpoint, the analytics have been displayed correctly.