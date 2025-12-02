package dataAccess;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Driver;
import domain.Ride;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

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
			Driver d = db.find(Driver.class, "driver1@gmail.com");
			if (d != null) {
				// Fix for existing drivers with null passwords
				if (d.getPassword() == null) {
					System.out.println("Updating null passwords for existing drivers...");
					d.setPassword("123");
					
					Driver d2 = db.find(Driver.class, "driver2@gmail.com");
					if (d2 != null && d2.getPassword() == null) d2.setPassword("123");
					
					Driver d3 = db.find(Driver.class, "driver3@gmail.com");
					if (d3 != null && d3.getPassword() == null) d3.setPassword("123");
					
					db.getTransaction().commit();
					System.out.println("Passwords updated.");
				} else {
					db.getTransaction().commit();
					System.out.println("Db already initialized");
				}
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

		TypedQuery<Date> query = db.createQuery("SELECT DISTINCT r.date FROM Ride r WHERE r.from=:from AND r.to=:to AND r.date BETWEEN :startDate AND :endDate", Date.class);
		query.setParameter("from", from);
		query.setParameter("to", to);
		query.setParameter("startDate", firstDayMonthDate);
		query.setParameter("endDate", lastDayMonthDate);
		List<Date> dates = query.getResultList();
		return dates;
	}

	public Driver getDriver(String email) {
		return db.find(Driver.class, email);
	}

	public void storeDriver(String email, String name, String password) {
		db.getTransaction().begin();
		Driver driver = new Driver(email, name, password);
		db.persist(driver);
		db.getTransaction().commit();
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
