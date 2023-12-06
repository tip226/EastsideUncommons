Tina Pham
tip226@lehigh.edu
CSE241 Final Project

To run and test this program:
```
java -jar Test.jar
```
To populate the desired data, feel free to edit the .txt files

jdbc hw4:
```
java -cp ojdbc11.jar Test.java
```
Commands for submission:
zip -r ../tip226pham.zip *

## Running as a Jar File

Include all the necessary `.class` files in the jar file
```
jar cvfm Test.jar Manifest.txt -C . .
```

Verify the contents of the JAR file with:
```
jar tf Test.jar
```

Run the jar file with:
```
java -jar Test.jar
```

There's a makefile included in the submission. To compile the program, simply run make. This will compile all the files and run the cde. I used Console.readPassword() to allow for the input of the password to be hidden from view.

After entering username and password, you will have an option to choose between interfaces. Note that Developer Interface is my own interface that I created to help me populate the table for checkpoint 3 so there's no need to test that interface. Here's a walk through of testing my program.
Go ahead and choose option 1 to test the Property Manager Interface. Here feel free to intentionally input invalid choice such as a letter or any number that is not between 1 and 6.

Property Manager Interface
A menu of different options will be displayed. Go ahead and choose option 1 to test Record Visit Data. Here feel free to intentionally input invalid choice such as a letter or any number that is not between 1 and 7. Record visit data is meant to add a new prospectve tenant to the ProspectiveTenant table. Now enter the following information and feel free to intentionally input invalid data to test the validation.
1. Record Visit Data
* Name: here I have added validation to make sure user enter both first name and last name
* Email: here I have added validation to make sure user enter a valid email with the character '@'
* Phone Number: here I have added validation to make sure user enter a valid phone number with 10 digits
* Date of Visit: here I have added validation to make sure user enter a valid date in the format of YYYY-MM-DD
* Apartment ID: An Apartment table will be displayed and you can choose an apartment id. Here I have added validation to make sure user enter a valid apartment ID that is in the Apartment table
* Annual income: here I have added validation to make sure user enter a valid annual income that has a maximum of 18 digits and 2 decimal places
* Credit Score: here I have added validation to make sure user enter a valid credit score that is between 300 and 850

2. Record Lease Data
* Apartment ID: An Apartment table will be displayed and you can choose an apartment id. Here I have added validation to make sure user enter a valid apartment ID that is in the Apartment table
* Lease Start Date: here I have added validation to make sure user enter a valid date in the format of YYYY-MM-DD
* Lease End Date: here I have added validation to make sure this date is after the lease start date
* Monthly Rent: here I have added validation to make sure user enter a valid monthly rent that has a maximum of 8 digits and 2 decimal places
* Security Deposit: here I have added validation to make sure user enter a valid security deposit that has a maximum of 8 digits and 2 decimal places
* Number of tenants: User has to enter a number that is >= 1 and <= capacity (capacity is the number of bedrooms in the apartment). For now, a pet will be treated as a tenant so if the user has a pet, the number of tenants will be 1 + number of pets. Depends on how many tenants you enter, the loop will keep asking you to enter the info for the tenants and pets. 
Enter T for tenant and P for pet here. 

* If you choose T, Then choose M to add a tenant manually or P to select from ProspectiveTenant table.
* If you choose M, you will be asked to enter the following info:
* Name: here I have added validation to make sure user enter both first name and last name

The program is pretty straightforward but below I noted down some assumptions and project notes that I think might be helpful for you to understand the program. While testing out the program, feel free to intentionally input invalid data to test the validation.
Assumptions and project notes:
* A tenant can only sign a lease for 1 apartment
* An apartment can only have 1 lease. When recording a lease, it will display the Apartment table. If user enter an apartment id that already has a lease, user will have to enter another apartment id.
* For record lease data, user has to enter a number of tenants that is >= 1 and <= capacity (capacity is the number of bedrooms in the apartment). For now, a pet will be treated as a tenant so if the user has a pet, the number of tenants will be 1 + number of pets. Depends on how many tenants you enter, the loop will keep asking you to enter the info for the tenants and pets. I assume user will enter the info of at least a tenant when recording lease data.
* For record lease data, I assume that user won't abruptly terminate the program until they finish entering all the info for the tenants and pets. If they do, the lease might be created without associating with a tenant.
* If user chooses to enter a tenant info using existing info from prospective tenant table, the prospective tenant will be deleted from the ProspectiveTenant table after the person's info is transferred to the Tenant table.
* When using the tenant interface, user has to first enter a tenant id and a date. The date is for the payment calculations.
    * Before lease start date payment: security deposit + one-time amenities
    * On and after lease start date payment: monthly rent + monthly amenities
    * On and after lease end date payment: return deposit if no damages (property manager can input damage when setting move-out date)
    * Assumption: All amenities one-time cost are added before tenants go to pay the first time
    * Assumption: Tenants don't have past dues. The program will check if there are any payments made withn the month that user enters when first prompted at the beginning of the tenant interface. If there is already a record of payment in the table for that month, it will say all payments for the month are up to date.
* Both property manager and tenant can add tenant to lease or add pet to tenant. Before adding a tenant to lease, the program will check for capacity.

Citation
I used the code from here to display most of the tables in my program: https://github.com/htorun/dbtableprinter. I found this github code by looking up on Google and found this stackoverflow post: https://stackoverflow.com/questions/15444982/how-to-display-or-print-the-contents-of-a-database-table-as-is. The only file that contains this code is DBTablePrinter.java
Some of my data are from https://www.mockaroo.com/ 

