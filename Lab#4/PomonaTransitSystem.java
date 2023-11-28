
//import java.util.Scanner;
import java.util.Scanner;
import java.sql.*;

public class PomonaTransitSystem {
       //
       private static final String JDBC_URL = "jdbc:mysql://localhost:3306/PomonaTransitSystem";
       private static final String USERNAME = "root";
       private static final String PASSWORD = "";
       private static Connection conn = null;
       private static Scanner input = new Scanner(System.in);
       private static Statement myStatement;

       //private static Scanner input = new Scanner(System.in);

       public static void main(String[] args) {
              String userInput = "";
              try {
                     //get connection using jdbc url, username, and password
                     conn = DriverManager.getConnection(JDBC_URL,USERNAME,PASSWORD);
                     //create sql statement
                     myStatement = conn.createStatement();
                     while (!userInput.equals("12")) {
                            System.out.println("Pomona Transit System");
                            System.out.println("1: Display all schedule of all trips");
                            System.out.println("2: Delete a trip offering");
                            System.out.println("3: Add a set of trip offerings");
                            System.out.println("4: Change the driver for a trip offering");
                            System.out.println("5: Change the bus for a trip offering");
                            System.out.println("6: Display the stops of a given trip");
                            System.out.println("7: Display the weekly schedule of a driver");
                            System.out.println("8: Add a driver");
                            System.out.println("9: Add a bus");
                            System.out.println("10: Delete a bus");
                            System.out.println("11: Insert the actual data of a given trip offering");
                            System.out.println("12: End Program");
                            userInput = input.nextLine();
                            switch(userInput) {
                                   case "1":
                                          getTripFromDB(conn);
                                          System.out.println();
                                          break;
                                   case "2":
                                          deleteTrip(conn);
                                          System.out.println();
                                          break;
                                   case "3":
                                          //get amount of trip offerings user wants to insert, insert amount oof trip offering specified
                                          System.out.println("Insert the number of trip offerings to be inserted: ");
                                          int newTripOfferings = input.nextInt();
                                          for(int i = 0; i < newTripOfferings; i++) {
                                                 addTripOffering(conn);
                                                 System.out.println();
                                          }
                                          System.out.println();
                                          break;
                                   case "4":
                                          changeTripDriver(conn);
                                          System.out.println();
                                          break;
                                   case "5":
                                          changeTripBusID(conn);
                                          System.out.println();
                                          break;
                                   case "6":
                                          displayTripStops(myStatement);
                                          System.out.println();
                                          break;
                                   case "7":
                                          displayDriverWeeklySchedule(conn);
                                          System.out.println();
                                          break;
                                   case "8":
                                          addDriver(myStatement);
                                          System.out.println();
                                          break;
                                   case "9":
                                          addBus(myStatement);
                                          System.out.println();
                                          break;
                                   case "10":
                                          deleteBus(myStatement);
                                          System.out.println();
                                          break;
                                   case "11":
                                          addActualTripStopInfo(conn);
                                          System.out.println();
                                          break;    
                            }
                     }
                     conn.close();
              } catch (SQLException ex) {
                     ex.printStackTrace();
              }
       }

       //method for getting trip given start location and destination
       public static void getTripFromDB(Connection conn) throws SQLException {
              int tripCount = 1;
              //get user input data for start location destination
              Scanner input = new Scanner(System.in);
              System.out.println("Enter the trip start location: ");
              String tripStart = input.nextLine();
              System.out.println("Enter the trip destination: ");
              String tripEnd = input.nextLine();
              //Create sql query with given data
              String query = "SELECT T.startlocationname, T.destinationname, TOF.date, TOF.scheduledstarttime, TOF.scheduledarrivaltime, TOF.drivername, TOF.busid " +  
              "FROM TRIP AS T, TRIPOFFERING AS TOF " + 
              "WHERE T.tripnumber = TOF.tripnumber AND T.startlocationname = " + "'" + tripStart + "'" + " AND T.destinationname = " + "'" + tripEnd + "';";
              
              //try to execute the sql query into the database and get data results
              try (Statement stmt = conn.createStatement()) {
      
                     ResultSet rs = stmt.executeQuery(query);
                     ResultSetMetaData rsmd = rs.getMetaData();
       
                     int colCount = rsmd.getColumnCount();
                     
                     //print results, display schedule of all trips
                     while (rs.next()) {
                            System.out.println("\nTrip " + tripCount + ": ");
                            for (int i = 1; i <= colCount; i++)
                            {
                                   System.out.print(rs.getString(i) + " | ");
                            }
                            System.out.println();
                     }
                     //close result set
                     rs.close();
              } catch (SQLException e) {
                     e.printStackTrace();
              }
      
       }

       //method for deleting a trip based on trip offering
       public static void deleteTrip(Connection conn) throws SQLException {
              //get user input trip number, date, and start time
              Scanner input = new Scanner(System.in);
              System.out.println("Enter the trip number: ");
              String tripNumber = input.nextLine();
              System.out.println("Enter the trip date(yyyy-mm-dd): ");
              String tripDate = input.nextLine();
              System.out.println("Enter the trip start time(hh:mm:ss): ");
              String tripStartTime = input.nextLine();

              //create sql query for deleting trip offering using user input data
              String query = "DELETE FROM TRIPOFFERING " +
                      "WHERE tripnumber = " + tripNumber + " AND " +
                      "date = '" + tripDate + "' AND " +
                      "ScheduledStartTime = '" + tripStartTime + "'";
              
              //query for user after deleting to display altered Trip offering table
              String afterDelete = "SELECT * FROM TRIPOFFERING";
              
              //try to delete a trip using created query and execute afterDelete query to display table after deletion
              try (Statement stmt = conn.createStatement()) {
                     stmt.executeUpdate(query);
      
                     ResultSet rs = stmt.executeQuery(afterDelete);
                     ResultSetMetaData rsmd = rs.getMetaData();
       
                     int colCount = rsmd.getColumnCount();
                     
                     //print table after deletion 
                     System.out.println("\nAFTER DELETE:");
                     while (rs.next()) {
                            for (int i = 1; i <= colCount; i++)
                            {
                                   System.out.print(rs.getString(i) + " | ");
                            }
                            System.out.println();
                     }
                     //close result set
                     rs.close();
              } catch (SQLException e) {
                     e.printStackTrace();
              }
       }

       //method for adding a trip offering
       public static void addTripOffering(Connection conn) throws SQLException {
              //get user input trip offering data
              Scanner input = new Scanner(System.in);
              System.out.println("Enter the trip number: ");
              String tripNumber = input.nextLine();
              System.out.println("Enter the trip date(yyyy-mm-dd): ");
              String tripDate = input.nextLine();
              System.out.println("Enter the trip start time(hh:mm:ss): ");
              String tripStartTime = input.nextLine();
              System.out.println("Enter the trip arrival time(hh:mm:ss): ");
              String tripArrivalTime = input.nextLine();
              System.out.println("Enter the driver's name: ");
              String driverName = input.nextLine();
              System.out.println("Enter the bus's ID: ");
              String busID = input.nextLine();
              
              //create sql query for inserting a trip offering to trip offerings table
              String query = "INSERT INTO TRIPOFFERING VALUES ('" + tripNumber + "', '" + tripDate + "', '" + tripStartTime + "', '"
                     + tripArrivalTime + "', '" + driverName + "', '" + busID + "')";

              //sql query for displaying table after insertion
              String afterInsert = "SELECT * FROM TRIPOFFERING";

              //try to execute sql query to insert trip offering and display results
              try (Statement stmt = conn.createStatement()) {
                     stmt.executeUpdate(query);

                     ResultSet rs = stmt.executeQuery(afterInsert);
                     ResultSetMetaData rsmd = rs.getMetaData();

                     int colCount = rsmd.getColumnCount();

                     //print results after insertion
                     System.out.println("AFTER INSERTION: \n");
                     while (rs.next()) {
                            for (int i = 1; i <= colCount; i++)
                            {
                                   System.out.print(rs.getString(i) + " | ");
                            }
                            System.out.println();
                     }
                     //close result set
                     rs.close();
              } catch (SQLException e) {
                     e.printStackTrace();
              }
       }

       //method for editing the driver of a trip
       public static void changeTripDriver(Connection conn) throws SQLException {
              //get user input data for creating query
              Scanner input = new Scanner(System.in);
              System.out.println("Enter the trip number: ");
              String tripNumber = input.nextLine();
              System.out.println("Enter the trip date(yyyy-mm-dd): ");
              String tripDate = input.nextLine();
              System.out.println("Enter the trip start time(hh:mm:ss): ");
              String tripStartTime = input.nextLine();
              System.out.println("Enter the new driver name: ");
              String newDriver = input.nextLine();
              
              //create query based on inputted data
              String query = "UPDATE TRIPOFFERING " +
                     "SET drivername = '" + newDriver + "' " +
                     "WHERE tripnumber = '" + tripNumber + "' AND " +
                     "date = '" + tripDate + "' AND " +
                     "scheduledstarttime = '" + tripStartTime + "'";

              //create query to select al from trip offering to show changed table
              String afterUpdate = "SELECT * FROM TRIPOFFERING";

              //try to execute query to change a driver's name, if successful print altered table to console
              try (Statement stmt = conn.createStatement()) {
                     stmt.executeUpdate(query);

                     ResultSet rs = stmt.executeQuery(afterUpdate);
                     ResultSetMetaData rsmd = rs.getMetaData();

                     int colCount = rsmd.getColumnCount();
                     //print altered table
                     System.out.println("THE TABLE AFTER UPDATE: ");
                     while (rs.next()) {
                            for (int i = 1; i <= colCount; i++)
                            {
                                   System.out.print(rs.getString(i) + " | ");
                            }
                            System.out.println();
                     }
                     //close meta data
                     rs.close();
              } catch (SQLException e) {
                     e.printStackTrace();
              }
       }

       //method for changing the bus for a given trip offering
       public static void changeTripBusID(Connection conn) throws SQLException {
              //get user input for trip offering data and new bus ID
              Scanner input = new Scanner(System.in);
              System.out.println("Enter the trip number: ");
              String tripNumber = input.nextLine();
              System.out.println("Enter the trip date(yyyy-mm-dd): ");
              String tripDate = input.nextLine();
              System.out.println("Enter the trip start time(hh:mm:ss): ");
              String tripStartTime = input.nextLine();
              System.out.println("Enter the new bus ID: ");
              String newBusID = input.nextLine();


              //create query for changing a tripoffering's bus
              String query = "UPDATE TRIPOFFERING " +
              "SET busID = '" + newBusID + "' " +
              "WHERE tripnumber = '" + tripNumber + "' AND " +
              "date = '" + tripDate + "' AND " +
              "scheduledstarttime = '" + tripStartTime + "'";

              //query for selecting all items from tripoffering to display table after update
              String afterUpdate = "SELECT * FROM TRIPOFFERING";

              //try to execute query for changing a bus, if successuful print table after update
              try (Statement stmt = conn.createStatement()) {
                     stmt.executeUpdate(query);

                     ResultSet rs = stmt.executeQuery(afterUpdate);
                     ResultSetMetaData rsmd = rs.getMetaData();

                     int colCount = rsmd.getColumnCount();

                     //print resulting table
                     System.out.println("THE TABLE AFTER UPDATE: ");
                     while (rs.next()) {
                            for (int i = 1; i <= colCount; i++)
                            {
                                   System.out.print(rs.getString(i) + " | ");
                            }
                            System.out.println();
                     }
                     //close result set
                     rs.close();
              } catch (SQLException e) {
                     e.printStackTrace();
              }
       }
       
       //method for displaying the stops of a given trip
       public static void displayTripStops(Statement mystatement) throws SQLException{
              //get user inptted trip numbeer
              Scanner input = new Scanner(System.in);
              System.out.println("Enter the trip number: ");
              int tripNumber = input.nextInt();
              //create query for selecting trip stop info with provided trip number 
              String query = String.format("SELECT * FROM tripstopinfo WHERE tripnumber = " + "(%d);", tripNumber);
              //execute query as myres and print resulting 
              ResultSet myres = mystatement.executeQuery(query);
              System.out.println("STOPS OF GIVEN TRIP: ");
              System.out.println("| TripNumber | StopNumber | SequenceNumber | DrivingTime |");
              while(myres.next()){
                     System.out.println("|      " + myres.getString("TripNumber") + "     |      " + myres.getString("StopNumber") 
                    + "     |        " + myres.getString("SequenceNumber") + "       |   " + myres.getString("DrivingTime")+ "  |");
              }
       }
       
       //method for displaying a driver's weekly schedule
       private static void displayDriverWeeklySchedule(Connection conn) throws SQLException{
              //get user input driver's name
              Scanner input = new Scanner(System.in);
              System.out.println("Enter the driver name: ");
              String driverName = input.nextLine();
              
              //create query for getting a driver's schedule using a driver's inputted name
              String query = "SELECT tripnumber, date, scheduledstarttime, scheduledarrivaltime FROM TRIPOFFERING WHERE drivername = '" + driverName +  "' ORDER BY date";

              //try to execute query and print weekly schedule of a driver
              try (Statement stmt = conn.createStatement()) {

                     ResultSet rs = stmt.executeQuery(query);
                     ResultSetMetaData rsmd = rs.getMetaData();

                     int colCount = rsmd.getColumnCount();

                     //print schedule
                     System.out.println("WEEKLY SCHEDULE FOR SELECTED DRIVER " + driverName + ": ");
                     while (rs.next()) {
                            for (int i = 1; i <= colCount; i++) {
                                   System.out.print(rs.getString(i) + " | ");
                            }
                            System.out.println();
                     }
                     //close result set
                     rs.close();
              } catch (SQLException e) {
                     e.printStackTrace();
              }
       }

       //method for adding a driver to driver table
       private static void addDriver(Statement mystatement) throws SQLException {
              //get user input driver information
              Scanner input = new Scanner(System.in);
              System.out.println("What is the driver name: ");
              String driverName = "'" + input.nextLine() + "'";
              System.out.println("Enter the driver's phone number(xxx-xxx-xxxx): ");
              String driverPhoneNumber = "'" + input.nextLine() + "'";

              //create query for inserting a new driver with provided info
              String query = String.format("INSERT INTO PomonaTransitSystem.Driver(DriverName, DriverTelephoneNumber) " + "VALUES (%s, %s);", driverName, driverPhoneNumber);

              //execute query
              mystatement.executeUpdate(query);

              //print update driver table
              System.out.println();
              ResultSet myres = mystatement.executeQuery("Select * FROM Driver");
              System.out.println("UPDATED DRIVER TABLE: ");
              while (myres.next()) {
                     System.out.println("| " + myres.getString("DriverName") +  " | " + myres.getString("DriverTelephoneNumber") + " |");
              }
              System.out.println();
              
       }

       //method for adding a bus to bus table
       private static void addBus(Statement mystatement) throws SQLException {
              //get user input bus id, model, and year
              Scanner input = new Scanner(System.in);
              int BusID = 0;
              String Model = "";
              int Year = 2021;
              System.out.println("Enter an ID for bus: ");
              BusID = input.nextInt();
              input.nextLine();
              System.out.println("Enter model of the bus: ");
              Model = "'" + input.nextLine() + "'";
              System.out.println("Enter manufacture year: ");
              Year = input.nextInt();
              
              //create query for inserting new bus to bus table with provided info
              String query = String.format("INSERT INTO PomonaTransitSystem.Bus(BusID, Model, Year) " + "VALUES (%d, %s, %d);", BusID, Model, Year);
              
              //execute sql query
              mystatement.executeUpdate(query);
      
              //print updated bus table
              System.out.println();
              ResultSet myres = mystatement.executeQuery("Select * FROM Bus");
              System.out.println("UPDATED BUS TABLE: ");
              while (myres.next()) {
                  System.out.println("| " + myres.getString("BusID") +  " | " + myres.getString("Model") + " | " + myres.getString("Year") + " |");
              }
       }

       //method for deleting bus from bus table
       private static void deleteBus(Statement mystatement) throws SQLException{
              Scanner input = new Scanner(System.in);
      
              //execute query to get all for bus table, print bus table contents
              ResultSet myres = mystatement.executeQuery("Select * FROM Bus");
              System.out.println("CURRENT BUS TABLE:");
              while (myres.next()) {
                     System.out.println("| " + myres.getString("BusID") +  " | " + myres.getString("Model") + " | " + myres.getString("Year") + " |");
              }
              System.out.println();
              
              //get user input for bus to be deleted
              System.out.println("input the bus ID to remove: ");
              int BusID = input.nextInt();
              input.nextLine();
              
              //create query for deleting given bus from bus table
              String query = String.format("DELETE FROM PomonaTransitSystem.Bus WHERE BusID = %d", BusID);
              
              //execute query
              mystatement.executeUpdate(query);
      
              //display contents of updated bus table
              myres = mystatement.executeQuery("Select * FROM Bus");
              System.out.println();
              System.out.println("UPDATED BUS TABLE: ");
              while (myres.next()) {
                     System.out.println("| " + myres.getString("BusID") +  " | " + myres.getString("Model") + " | " + myres.getString("Year") + " |");
              }
       }

       //method for recording data of given trip(add entry to actualTripStopInfo table)
       public static void addActualTripStopInfo(Connection conn) throws SQLException {
              //get user input data for ActualTripStopinfo
              Scanner input = new Scanner(System.in);
              System.out.println("Enter the trip number: ");
              String tripNumber = input.nextLine();
              System.out.println("Enter the trip date(yyyy-mm-dd): ");
              String tripDate = input.nextLine();
              System.out.println("Enter the scheduled start time(hh:mm:ss): ");
              String scheduledStartTime = input.nextLine();
              System.out.println("Enter the stop number: ");
              String stopNumber = input.nextLine();
              System.out.println("Enter the scheduled arrival time(hh:mm:ss): ");
              String scheduledArrivalTime = input.nextLine();
              System.out.println("Enter the actual start time(hh:mm:ss): ");
              String actualStartTime = input.nextLine();
              System.out.println("Enter the actual arrival time(hh:mm:ss): ");
              String actualArrivalTime = input.nextLine();
              System.out.println("Enter the number of passengers that came in: ");
              String passengerIn = input.nextLine();
              System.out.println("Enter the number of passengers that came out: ");
              String passengerOut = input.nextLine();

              //create query for inserting new entry to actualTripStopInfo
              String query = "INSERT INTO actualtripstopinfo VALUES('" + tripNumber + "', '" + tripDate + "', '" + scheduledStartTime
                     + "', '" + stopNumber + "', '" + scheduledArrivalTime + "', '" + actualStartTime + "', '" + actualArrivalTime + "', '" + passengerIn
                     + "', '" + passengerOut + "') ";

              //query for displaying results after insertion
              String afterInsertion = "SELECT * FROM ACTUALTRIPSTOPINFO;";

              //try to execute query and print resulting table
              try (Statement stmt = conn.createStatement()) {
                     stmt.executeUpdate(query);

                     ResultSet rs = stmt.executeQuery(afterInsertion);
                     ResultSetMetaData rsmd = rs.getMetaData();

                     int colCount = rsmd.getColumnCount();

                     System.out.println("AFTER INSERTION: ");
                     while (rs.next()) {
                            for (int i = 1; i <= colCount; i++)
                            {
                                   System.out.print(rs.getString(i) + " | ");
                            }    
                            System.out.println();
                     }
                     rs.close();
              } catch (SQLException e) {
                     e.printStackTrace();
              }
       }

}