package dataAccess;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import configuration.UtilDate;
import domain.Driver;
import domain.Ride;
import domain.User;
import domain.Traveler;
import domain.Admin;
import domain.Booking;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.NotEnoughMoneyException;
import exceptions.NotEnoughSeatsException;

import exceptions.LateCancellationException;

/**
 * It implements the data access to the database using Hibernate with JPA
 */
public class HibernateDataAccess {
	private EntityManager db;
	private static EntityManagerFactory emf;


	public HibernateDataAccess() {
		// Initialize DB logic if needed
		open();
		initializeDB();
		close();
	}

	/**
	 * This is the data access method that initializes the database with some events and questions.
	 * This method is invoked by the business logic (constructor of BLFacadeImplementation) when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	public void initializeDB() {
		db.getTransaction().begin();
		try {
			// Check if database is already initialized
			User existingDriver = db.find(User.class, "driver1@gmail.com");
			if (existingDriver != null) {
				db.getTransaction().commit();
				System.out.println("Db already initialized");
				return;
			}
			
			Calendar today = Calendar.getInstance();
			int month = today.get(Calendar.MONTH);
			int year = today.get(Calendar.YEAR);
			if (month == 12) {
				month = 1;
				year += 1;
			}			

			// Create drivers
			Driver driver1 = new Driver("driver1@gmail.com", "Aitor Fernandez", "123");
			Driver driver2 = new Driver("driver2@gmail.com", "Ane Gaztañaga", "123");
			Driver driver3 = new Driver("driver3@gmail.com", "Test driver", "123");

			// Create travelers
			Traveler traveler1 = new Traveler("traveler@gmail.com", "Iker Goenaga", "123");
			
			// Create admin
			Admin admin1 = new Admin("admin@gmail.com", "Admin User", "123");

			// Create rides
			driver1.addRide("Donostia", "Bilbo", UtilDate.newDate(year, month, 15), 4, 7);
			driver1.addRide("Donostia", "Gazteiz", UtilDate.newDate(year, month, 6), 4, 8);
			driver1.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 25), 4, 4);
			driver1.addRide("Donostia", "Iruña", UtilDate.newDate(year, month, 7), 4, 8);

			driver2.addRide("Donostia", "Bilbo", UtilDate.newDate(year, month, 15), 3, 3);
			driver2.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 25), 2, 5);
			driver2.addRide("Eibar", "Gasteiz", UtilDate.newDate(year, month, 6), 2, 5);

			driver3.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 14), 1, 3);

			db.persist(driver1);
			db.persist(driver2);
			db.persist(driver3);
			db.persist(traveler1);
			db.persist(admin1);

			db.getTransaction().commit();
			System.out.println("Db initialized");
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	/**
	 * This method returns all the cities where rides depart 
	 * @return collection of cities
	 */
	public List<String> getDepartCities() {
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.from FROM Ride r ORDER BY r.from", String.class);
		List<String> cities = query.getResultList();
		return cities;
	}

	/**
	 * This method returns all the arrival destinations, from all rides that depart from a given city  
	 * 
	 * @param from the depart location of a ride
	 * @return all the arrival destinations
	 */
	public List<String> getArrivalCities(String from) {
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.to FROM Ride r WHERE r.from=:from ORDER BY r.to", String.class);
		query.setParameter("from", from);
		List<String> arrivingCities = query.getResultList();
		return arrivingCities;
	}

	/**
	 * This method creates a ride for a driver
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @param nPlaces available seats
	 * @param driverEmail to which ride is added
	 * 
	 * @return the created ride, or null, or an exception
	 * @throws RideMustBeLaterThanTodayException if the ride date is before today 
 	 * @throws RideAlreadyExistException if the same ride already exists for the driver
	 */
	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail) throws RideAlreadyExistException, RideMustBeLaterThanTodayException {
		System.out.println(">> HibernateDataAccess: createRide=> from= " + from + " to= " + to + " driver=" + driverEmail + " date " + date);
		try {
			if (new Date().compareTo(date) > 0) {
				throw new RideMustBeLaterThanTodayException(ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.ErrorRideMustBeLaterThanToday"));
			}

			db.getTransaction().begin();

			Driver driver = db.find(Driver.class, driverEmail);
			if (driver.doesRideExists(from, to, date)) {
				db.getTransaction().rollback();
				throw new RideAlreadyExistException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.RideAlreadyExist"));
			}

			Ride ride = driver.addRide(from, to, date, nPlaces, price);
			db.persist(driver);
			db.getTransaction().commit();

			return ride;
		} catch (NullPointerException e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			return null;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			throw e;
		}
	}

	/**
	 * This method retrieves the rides from two locations on a given date 
	 * 
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride
	 * @param date the date of the ride 
	 * @return collection of rides
	 */
	public List<Ride> getRides(String from, String to, Date date) {
		System.out.println(">> HibernateDataAccess: getRides=> from= " + from + " to= " + to + " date " + date);
		TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r WHERE r.from=:from AND r.to=:to AND r.date=:date", Ride.class);
		query.setParameter("from", from);
		query.setParameter("to", to);
		query.setParameter("date", date);
		List<Ride> rides = query.getResultList();
		return rides;
	}

	/**
	 * This method retrieves from the database the dates a month for which there are events
	 * @param from the origin location of a ride
	 * @param to the destination location of a ride 
	 * @param date of the month for which days with rides want to be retrieved 
	 * @return collection of rides
	 */
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		System.out.println(">> HibernateDataAccess: getThisMonthDatesWithRides");
		Date firstDayMonthDate = UtilDate.firstDayMonth(date);
		Date lastDayMonthDate = UtilDate.lastDayMonth(date);

		TypedQuery<Date> query = db.createQuery("SELECT DISTINCT r.date FROM Ride r WHERE r.from=:from AND r.to=:to AND r.date BETWEEN :startDate AND :endDate AND r.nPlaces > 0", Date.class);
		query.setParameter("from", from);
		query.setParameter("to", to);
		query.setParameter("startDate", firstDayMonthDate);
		query.setParameter("endDate", lastDayMonthDate);
		List<Date> dates = query.getResultList();
		return dates;
	}

	public User getUser(String email) {
		return db.find(User.class, email);
	}
	
	public void clearBan(String email) {
		db.getTransaction().begin();
		User user = db.find(User.class, email);
		if (user != null) {
			user.setBanExpirationDate(null);
			db.merge(user);
		}
		db.getTransaction().commit();
	}

	public Driver getDriver(String email) {
		return db.find(Driver.class, email);
	}

	public void storeUser(String email, String name, String password, String type) {
		db.getTransaction().begin();
		User user = null;
		if (type.equals("Driver")) {
			user = new Driver(email, name, password);
		} else if (type.equals("Traveler")) {
			user = new Traveler(email, name, password);
		} else if (type.equals("Admin")) {
			user = new Admin(email, name, password);
		}
		
		if (user != null) {
			db.persist(user);
		}
		db.getTransaction().commit();
	}
	
	public List<User> getAllUsers() {
		TypedQuery<User> query = db.createQuery("SELECT u FROM User u", User.class);
		return query.getResultList();
	}
	
	public void banUser(String email, Date date) {
		db.getTransaction().begin();
		User user = db.find(User.class, email);
		if (user != null) {
			user.setBanExpirationDate(date);
		}
		db.getTransaction().commit();
	}
	
	public void unbanUser(String email) {
		db.getTransaction().begin();
		User user = db.find(User.class, email);
		if (user != null) {
			user.setBanExpirationDate(null);
		}
		db.getTransaction().commit();
	}
	
	public List<Ride> getAllRides() {
		TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r", Ride.class);
		return query.getResultList();
	}
	
	public List<Ride> getRidesByDriver(String driverEmail) {
		TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r WHERE r.driver.email = :driverEmail", Ride.class);
		query.setParameter("driverEmail", driverEmail);
		return query.getResultList();
	}
	
	public void deleteRide(Ride ride) {
		db.getTransaction().begin();
		Ride r = db.find(Ride.class, ride.getRideNumber());
		if (r != null) {
			// Find and refund bookings
			TypedQuery<Booking> query = db.createQuery("SELECT b FROM Booking b WHERE b.ride = :ride", Booking.class);
			query.setParameter("ride", r);
			List<Booking> bookings = query.getResultList();
			
			Driver driver = r.getDriver();
			
			for (Booking b : bookings) {
				Traveler traveler = b.getTraveler();
				double refund = b.getPrice();
				
				// Refund money
				traveler.setWallet(traveler.getWallet() + refund);
				driver.setWallet(driver.getWallet() - refund);
				
				db.merge(traveler);
				db.remove(b);
			}
			db.merge(driver);
			
			driver.removeRide(r); 
			db.remove(r);
		}
		db.getTransaction().commit();
	}
	
	public void depositMoney(String email, double amount) {
		db.getTransaction().begin();
		User user = db.find(User.class, email);
		if (user != null) {
			user.setWallet(user.getWallet() + amount);
			db.merge(user);
		}
		db.getTransaction().commit();
	}

	public boolean withdrawMoney(String email, double amount) {
		boolean success = false;
		db.getTransaction().begin();
		User user = db.find(User.class, email);
		if (user != null) {
			if (user.getWallet() >= amount) {
				user.setWallet(user.getWallet() - amount);
				db.merge(user);
				success = true;
			}
		}
		db.getTransaction().commit();
		return success;
	}
	
	public void bookRide(Integer rideNumber, String travelerEmail, int seats) throws NotEnoughSeatsException, NotEnoughMoneyException {
		db.getTransaction().begin();
		Ride ride = db.find(Ride.class, rideNumber);
		Traveler traveler = db.find(Traveler.class, travelerEmail);
		
		if (ride.getnPlaces() < seats) {
			db.getTransaction().rollback();
			throw new NotEnoughSeatsException("Not enough seats available");
		}
		
		double totalPrice = ride.getPrice() * seats;
		
		if (traveler.getWallet() < totalPrice) {
			db.getTransaction().rollback();
			throw new NotEnoughMoneyException("Not enough money in wallet");
		}
		
		// Update wallet
		traveler.setWallet(traveler.getWallet() - totalPrice);
		Driver driver = ride.getDriver();
		driver.setWallet(driver.getWallet() + totalPrice);
		
		// Update ride
		ride.setnPlaces(ride.getnPlaces() - seats);
		
		// Create booking
		Booking booking = new Booking(ride, traveler, seats, totalPrice);
		db.persist(booking);
		
		db.merge(ride);
		db.merge(traveler);
		db.merge(driver);
		
		db.getTransaction().commit();
	}
	
	public List<Booking> getBookingsByTraveler(String email) {
		TypedQuery<Booking> query = db.createQuery("SELECT b FROM Booking b WHERE b.traveler.email = :email", Booking.class);
		query.setParameter("email", email);
		return query.getResultList();
	}
	
	public double cancelBooking(Integer bookingNumber) throws LateCancellationException {
		db.getTransaction().begin();
		Booking booking = db.find(Booking.class, bookingNumber);
		double refund = 0;
		if (booking != null) {
			Ride ride = booking.getRide();
			Traveler traveler = booking.getTraveler();
			Driver driver = ride.getDriver();
			
			long diff = ride.getDate().getTime() - new Date().getTime();
			double hours = diff / (60 * 60 * 1000.0);
			
			if (hours < 48) {
				db.getTransaction().rollback();
				throw new LateCancellationException("Cannot cancel booking less than 48 hours before the ride.");
			}
			
			refund = booking.getPrice();
			
			// Update wallets
			traveler.setWallet(traveler.getWallet() + refund);
			driver.setWallet(driver.getWallet() - refund);
			
			// Update ride seats
			ride.setnPlaces(ride.getnPlaces() + booking.getSeats());
			
			// Remove booking
			db.remove(booking);
			
			db.merge(traveler);
			db.merge(driver);
			db.merge(ride);
		}
		db.getTransaction().commit();
		return refund;
	}

	//getRidesForDate
	 public List<Ride> getRidesForDate(Date date) {
		TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r JOIN FETCH r.driver WHERE r.date=:date", Ride.class);
		query.setParameter("date", date);
		List<Ride> rides = query.getResultList();
		return rides;
	 }
	 
	 public List<Driver> getAllDrivers() {
			TypedQuery<Driver> query = db.createQuery("SELECT d FROM Driver d", Driver.class);
			return query.getResultList();
	}
	 
	 public List<Ride> getRidesByDriver(Driver d) {
		 	// We merge the driver to ensure it's attached, or just query by driver
			TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r WHERE r.driver=:driver", Ride.class);
			query.setParameter("driver", d);
			return query.getResultList();
	}

	public void open() {
		try {
			System.out.println("HibernateDataAccess: Opening...");
			
			if (emf == null) {
				emf = Persistence.createEntityManagerFactory("rides");
			}
			db = emf.createEntityManager();
			
			System.out.println("HibernateDataAccess opened");
		} catch (Exception e) {
			System.err.println("HibernateDataAccess: Error opening database: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public void close() {
		if (db != null) {
			db.close();
		}
		System.out.println("HibernateDataAccess closed");
	}
}
