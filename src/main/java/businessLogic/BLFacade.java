package businessLogic;

import java.util.Date;
import java.util.List;

//import domain.Booking;
import domain.Ride;
import domain.Driver;
import domain.User;
import domain.Booking;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.RideAlreadyExistException;
import exceptions.NotEnoughMoneyException;
import exceptions.NotEnoughSeatsException;
import exceptions.LateCancellationException;

 
/**
 * Interface that specifies the business logic.
 */
public interface BLFacade  {
	  
	/**
	 * This method returns all the cities where rides depart 
	 * @return collection of cities
	 */
	 public List<String> getDepartCities();
	
	/**
	 * This method returns all the arrival destinations, from all rides that depart from a given city  
	 * 
	 * @param from the depart location of a ride
	 * @return all the arrival destinations
	 */
	 public List<String> getDestinationCities(String from);


	/**
	 * This method creates a ride for a driver
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @param nPlaces available seats
	 * @param driver to which ride is added
	 * 
	 * @return the created ride, or null, or an exception
	 * @throws RideMustBeLaterThanTodayException if the ride date is before today 
 	 * @throws RideAlreadyExistException if the same ride already exists for the driver
	 */
   public Ride createRide( String from, String to, Date date, int nPlaces, float price, String driverEmail) throws RideMustBeLaterThanTodayException, RideAlreadyExistException;
	
	
	/**
	 * This method retrieves the rides from two locations on a given date 
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @return collection of rides
	 */
	 public List<Ride> getRides(String from, String to, Date date);
	
	/**
	 * This method retrieves from the database the dates a month for which there are events
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride 
	 * @param date of the month for which days with rides want to be retrieved 
	 * @return collection of rides
	 */
	 public List<Date> getThisMonthDatesWithRides(String from, String to, Date date);
	 
	 public User login(String email, String password);
	 
	 public boolean register(String email, String name, String password, String type);
	 
	 public List<User> getAllUsers();
	 
	 public void banUser(String email, int duration, String timeUnit);
	 
	 public void unbanUser(String email);
	 
	 public List<Ride> getAllRides();
	 
	 public List<Ride> getRidesByDriver(String driverEmail);
	 
	 public void deleteRide(Ride ride);
	
	/**
	 * This method calls the data access to initialize the database with some events and questions.
	 * It is invoked only when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	 public void initializeBD();
	 
	 public User getUser(String email);
	 public void depositMoney(String email, double amount);
	 public boolean withdrawMoney(String email, double amount);
	 
	 public void bookRide(Integer rideNumber, String travelerEmail, int seats) throws NotEnoughSeatsException, NotEnoughMoneyException;
	 
	 public List<Booking> getBookingsByTraveler(String email);
	 
	 public double cancelBooking(Integer bookingNumber) throws LateCancellationException;

	
}
