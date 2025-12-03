package domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import jakarta.persistence.*;


@Entity
@DiscriminatorValue("DRIVER")
public class Driver extends User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, mappedBy="driver")
	private Set<Ride> rides=new HashSet<Ride>();

	public Driver() {
		super();
	}

	public Driver(String email, String name, String password) {
		super(email, name, password);
	}

	
	
	public String toString(){
		return getEmail()+";"+getName()+rides;
	}
	
	/**
	 * This method creates a bet with a question, minimum bet ammount and percentual profit
	 * 
	 * @param question to be added to the event
	 * @param betMinimum of that question
	 * @return Bet
	 */
	public Ride addRide(String from, String to, Date date, int nPlaces, float price)  {
        Ride ride=new Ride(from,to,date,nPlaces,price, this);
        rides.add(ride);
        return ride;
	}

	/**
	 * This method checks if the ride already exists for that driver
	 * 
	 * @param from the origin location 
	 * @param to the destination location 
	 * @param date the date of the ride 
	 * @return true if the ride exists and false in other case
	 */
	public boolean doesRideExists(String from, String to, Date date)  {	
		for (Ride r:rides)
			if ( (java.util.Objects.equals(r.getFrom(),from)) && (java.util.Objects.equals(r.getTo(),to)) && (java.util.Objects.equals(r.getDate(),date)) )
			 return true;
		
		return false;
	}
		
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Driver other = (Driver) obj;
		if (!getEmail().equals(other.getEmail()))
			return false;
		return true;
	}

	public Ride removeRide(String from, String to, Date date) {
		for (Ride r : rides) {
			if ( (java.util.Objects.equals(r.getFrom(),from)) && (java.util.Objects.equals(r.getTo(),to)) && (java.util.Objects.equals(r.getDate(),date)) ) {
				rides.remove(r);
				return r;
			}
		}
		return null;
	}
	
	public void removeRide(Ride r) {
		rides.remove(r);
	}
	
}
