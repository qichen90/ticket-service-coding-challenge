# ticket-service-coding-challenge

Implement a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.
The solution and tests are built using Maven.

## Assumptions
1. The `Venue` has only one kind of seating arrangement as below. The total number of seats depends on the number of rows and columns   seats in the veune. The other seating arrangement will not be considered in the solution.
   ![venue](https://user-images.githubusercontent.com/8053385/40327686-d7275a9e-5d11-11e8-9de5-1821be4d4369.PNG)
2. `Seat` represents each seat in the `Venue` which has seatLocationRow, seatLocationCol and `SeatState`. `SeatState` indicates the state of the Seat which can be OPEN, RESERVED or HELD. `SeatHold` stores the list of held seats for specific customer. When the customer reserves the SeatHold, then all states of seats in the SeatHold will be changed to RESERVED.
3. The best seats are assumed as any seats which are open for held. To find the best seats, checking rows from front to the end and on each row from left to right. If there are not enough seats available for held, then return `null` which indicates held failure for input numSeats held.
4. The SeatHold will be expired after `DEFAULT_EXPIRATION_TIME_IN_SECONDS` since SeatHold is created. Every time, `findAndHoldSeats` method is call, all SeatHold will be checked for expiration by calculating the difference between dateCreated and current time. All seats of the expired SeatHold will be reset to OPEN. 
5. The same customer is able to make reservation/held several times for different seats.
6. Unique `confirmationCode` will be generated for each reservation in `CodeGenerator`. The length of the confirmation code can be specified when call the generator. The possibility of generating duplicate code is lower when the length of the code gets longer.
7. For `findAndHoldSeats` and `reserveSeats` methods, when there is no such SeatHold or not enough seats for held, `null` will be returned in the solution. The validation of email is checked by EmailValidator for email format and compared with the email stored in the SeatHold.If the email is not valid, the solution will also return `null`.

## Instructions
### Building the solution
   ./mvn compile
### Running the tests
   ./mvn test
## Built With
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Qi Chen** 
