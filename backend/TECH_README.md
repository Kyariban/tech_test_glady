# Glady Technical Test

*The following document will describe the various notes and technical observation regarding the technical test*

## Building the application

### Building and running the app locally
Open a terminal in the backend folder and type the following command.
`mvn clean install`

This maven command also triggers the code generation step for OpenApi.

Once maven has finished you can use the next command to start the application :   
`mvn spring-boot:run`

Or you can choose the IDE of your choice to run the application.

### Testing the app
*Default data is loaded at the app start, you can find it in the data.sql file, in the resources of the project*  

The application will start on port 8080, you can start calling it using the given postman collection.
Or you can use swagger at the following link : 

http://localhost:8080/swagger-ui/index.html

Swagger allows for a quicker access where the postman collection is better for more in depth testing.

### Database

The application runs an internal h2 database, to access the admin console you can use the following link :  
http://localhost:8080/h2-console

Use the following parameters to access it :
```
Username : sa  
password: password 
```
The other parameters are left as default.

### API documentation

The best way to see the different available endpoints is to go to swagger, but Here's the list of the available endpoint and how to interact with them.  

- `GET /api/user/{userId}/balance`   
  Simply pass the userId of the user you want to know the balance of.

- `POST /api/company/{companyId}/user/{userId}/deposit`  
  Perform a new deposit from the given company to the given user, needs the following application/json body : 

```json
{
     "amount": "[number | Mandatory]",
     "depositType": "[MEAL / GIFT]"
}
  ```

The given postman collection only displays working cases, but it's still a good entry point to test the different possible behaviours.

the .git folder can be found in the .git.rar archive file