Test task impl for https://gist.github.com/juozapas/f20b55e4568d7f5c63b1
Design and implement a JSON API using Hibernate/Spring/SpringMVC without frontend.

The task is:

Build a voting system for deciding where to have lunch.

------------------------------------------------------------------------------------------------

### Implementation details:
* Because my questions wasn't answered I made some assumptions.
  - Auth is very simple - basic http. If someone tries to hit endpoint without auth he should get 401 response. If any user is specified with "" password then he is treated as normal user. If user name is admin with "" pass then user is admin
  - "Each restorant provides new menu each day." I treated this in the following way - at app start and the 1st second of the new day clean everything from DB that is older that todays start of the day



### Public API rest endpoints:
* /vote/vote-by-name/{restaurantName} - do actual voting till 11:00, returns 200 - OK if vote counted, any other error code otherwise. If vote after 11 then returns 404. Usees method PUT. User is a user from basic auth
* /vote/showWinner - returns json array of restaurant name and how many votes for it, sorted so winner should be on the top. Usees method GET

* /restaurant/add - you can add restaurant. Only admin can do this. if you try to add restaurant with the name that already exist new one will totally override restaurant, its menu and votes, so be wise!. Uses method PUT and json content type. Example of json will be provided below
* /restaurant/delete/{restaurantName} - delete existing restaurant. allowed to admin only, deletes restaurant its menu and votes, be wise !. No protection of specifying not existing name :)

* /restaurant/list - returns json array with all available restaurants with menus. uses method GET.


### JSON example to add restaurant:
```JSON
{
  "Restaurant" : {
    "restaurantName" : "BBBB",
    "menuList" : [ {      
      "name" : "crabs",
      "price" : 1.0
    }, {
      "name" : "prawns",
      "price" : 2.0
    }, {
      "name" : "carrot",
      "price" : 3.0
    }, {
      "name" : "lemon",
      "price" : 4.0
    } ]
  }
}
```


### Implementation details
* Used spring-boot
* No xml configs this time
* Uses in mem h2 db with schema generated on app startup
* Uses http basic-auth and spring security for role-based method protection
* No users are defined anywhere, user with name admin is admin all other treated as users
* Uses spring data repositories
* No wrong input protection implemented
* It even has some tests


### Package layout
* bean - JPA, JSON and any other classes that are meant to be simple containers for data
* clock - time service to provide ability to replace it in test
* config - spring configs and spring boot starter lives there
* exception - custom exceptions, currenly the one that is thrown when you try to vote after 11
* manager - "wrapers" for dao, basically things that represent business logic, also transaction demarkation is happening there
* repo - spring data repositories interfaces
* rest - rest endpoints
* scheduler - scheduler that cleans everything on startup and beginning on new day


### DB schema:
* Restaurant table
* Two child tables: Vote and MenuItem that are connected with Restaurant with one-to-many unidirectional relationship

### How to run all this:
- Use the force - read the source
- Maven, use maven Luke

you can do "mvn clean test" to run the tests - they will bring app up and hit couple of rest endpoints
To start this app do "mvn clean spring-boot:run" wait a bit and it should start up and you can hit endpoints that are listed in pulblic api section, remember not wrong imput protection, so be gentle and polite. I would suggest following steps:
- Add couple of restaurants, remember to use admin user
- Vote for them using different users
- ask for a winner

Because I'm using Windows I have no curl, so can't provide 100% sure working lines






