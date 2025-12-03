package businessLogic;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import configuration.ConfigXML;
import dataAccess.HibernateDataAccess;
import domain.Ride;
import domain.Driver;
import domain.User;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.RideAlreadyExistException;
import exceptions.NotEnoughMoneyException;
import exceptions.NotEnoughSeatsException;

/**
 * It implements the business logic as a web service.
 */

public class BLFacadeImplementation  implements BLFacade {
	HibernateDataAccess dbManager;

	public BLFacadeImplementation()  {		
		System.out.println("Creating BLFacadeImplementation instance");
		
		
		    dbManager=new HibernateDataAccess();
		    
		//dbManager.close();

		
	}
	
    public BLFacadeImplementation(HibernateDataAccess da)  {
		dbManager=da;		
	}
    
    
    /**
     * {@inheritDoc}
     */
     public List<String> getDepartCities(){
    	dbManager.open();	
		
		 List<String> departLocations=dbManager.getDepartCities();		

		dbManager.close();
		
		return departLocations;
    	
    }
    /**
     * {@inheritDoc}
     */
	 public List<String> getDestinationCities(String from){
		dbManager.open();	
		
		 List<String> targetCities=dbManager.getArrivalCities(from);		

		dbManager.close();
		
		return targetCities;
	}

	/**
	 * {@inheritDoc}
	 */
   
   public Ride createRide( String from, String to, Date date, int nPlaces, float price, String driverEmail ) throws RideMustBeLaterThanTodayException, RideAlreadyExistException{
	   
		dbManager.open();
		Ride ride=dbManager.createRide(from, to, date, nPlaces, price, driverEmail);		
		dbManager.close();
		return ride;
   };
	
   /**
    * {@inheritDoc}
    */
	
	public List<Ride> getRides(String from, String to, Date date){
		dbManager.open();
		List<Ride>  rides=dbManager.getRides(from, to, date);
		dbManager.close();
		return rides;
	}

    
	/**
	 * {@inheritDoc}
	 */
	
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date){
		dbManager.open();
		List<Date>  dates=dbManager.getThisMonthDatesWithRides(from, to, date);
		dbManager.close();
		return dates;
	}
	
	
	public void close() {
		HibernateDataAccess dB4oManager=new HibernateDataAccess();

		dB4oManager.close();

	}

	/**
	 * {@inheritDoc}
	 */
    
	 public void initializeBD(){
    	dbManager.open();
		dbManager.initializeDB();
		dbManager.close();
	}

	public User login(String email, String password) {
		dbManager.open();
		User user = dbManager.getUser(email);
		dbManager.close();
		if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
			if (user.isBanned()) {
				throw new RuntimeException("User is banned until " + user.getBanExpirationDate());
			}
			return user;
		}
		return null;
	}

	public boolean register(String email, String name, String password, String type) {
		dbManager.open();
		User user = dbManager.getUser(email);
		if (user != null) {
			dbManager.close();
			return false;
		}
		dbManager.storeUser(email, name, password, type);
		dbManager.close();
		return true;
	}
	
	public List<User> getAllUsers() {
		dbManager.open();
		List<User> users = dbManager.getAllUsers();
		dbManager.close();
		return users;
	}
	
	public void banUser(String email, int duration, String timeUnit) {
		dbManager.open();
		Date date = new Date();
		long millisToAdd = 0;
		if ("Minutes".equals(timeUnit)) {
			millisToAdd = (long)duration * 60 * 1000;
		} else if ("Hours".equals(timeUnit)) {
			millisToAdd = (long)duration * 60 * 60 * 1000;
		} else { // Days
			millisToAdd = (long)duration * 24 * 60 * 60 * 1000;
		}
		date.setTime(date.getTime() + millisToAdd);
		dbManager.banUser(email, date);
		dbManager.close();
	}
	
	public void unbanUser(String email) {
		dbManager.open();
		dbManager.unbanUser(email);
		dbManager.close();
	}
	
	public List<Ride> getAllRides() {
		dbManager.open();
		List<Ride> rides = dbManager.getAllRides();
		dbManager.close();
		return rides;
	}
	
	public List<Ride> getRidesByDriver(String driverEmail) {
		dbManager.open();
		List<Ride> rides = dbManager.getRidesByDriver(driverEmail);
		dbManager.close();
		return rides;
	}
	
	public void deleteRide(Ride ride) {
		dbManager.open();
		dbManager.deleteRide(ride);
		dbManager.close();
	}
	
	public User getUser(String email) {
		dbManager.open();
		User user = dbManager.getUser(email);
		dbManager.close();
		return user;
	}

	public void depositMoney(String email, double amount) {
		dbManager.open();
		dbManager.depositMoney(email, amount);
		dbManager.close();
	}

	public boolean withdrawMoney(String email, double amount) {
		dbManager.open();
		boolean success = dbManager.withdrawMoney(email, amount);
		dbManager.close();
		return success;
	}
	
	public void bookRide(Integer rideNumber, String travelerEmail, int seats) throws NotEnoughSeatsException, NotEnoughMoneyException {
		dbManager.open();
		try {
			dbManager.bookRide(rideNumber, travelerEmail, seats);
		} catch (NotEnoughSeatsException | NotEnoughMoneyException e) {
			throw e;
		} finally {
			dbManager.close();
		}
	}

}

