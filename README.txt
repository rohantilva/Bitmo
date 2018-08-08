Frank Miao fmiao1
Alex Owen aowen10
Mateo Paredes mparede5
Rohan Tilva rtilva1


The User Interface for the Home Screen, Payment Screen, Settings Screen, 
Profile Screen, and Edit Profile Screen have been completed. We moved the 
incomplete requests list to the Home Screen. List View has buttons for 
payment, remind, and cancel functions. Requests can be completed or canceled 
through the Home Screen. All classes for payment are written, and are 
connected to temporary local databases. Within settings, there is a button
to add 500 bitcoin to your profile. Edit profile also can save information on 
to a database. 

Test Cases:
Go to settings and press the add button at the bottom left corner. 500 bitcoin
should be added to your value on the Home Screen. The corresponding dollar amount
will appear below it. 

Go to pay screen using dollar sign at top right of Home Screen. For Address, any 
name can be inputted. When entering a bitcoin, enter it into BTC. The converted
dollar amount will not appear, because it is not implemented yet. You can also 
add a message, then pay or request. If you pay, that amount of bitcoin will be 
removed from your balance. If you request, the request will appear on the list
on the Home Screen.

You should be able to remind and cancel a request to another user, and pay or 
cancel the hard coded request from another user. 

You can also go into profile to edit your profile. You can click save to save
profile and cancel to exit edit profile screen.