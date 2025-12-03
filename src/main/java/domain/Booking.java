package domain;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
public class Booking implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer bookingNumber;
	
	@ManyToOne
	private Ride ride;
	
	@ManyToOne
	private Traveler traveler;
	
	private int seats;
	private double price;
	
	public Booking() {
		super();
	}
	
	public Booking(Ride ride, Traveler traveler, int seats, double price) {
		this.ride = ride;
		this.traveler = traveler;
		this.seats = seats;
		this.price = price;
	}

	public Integer getBookingNumber() {
		return bookingNumber;
	}

	public void setBookingNumber(Integer bookingNumber) {
		this.bookingNumber = bookingNumber;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public Traveler getTraveler() {
		return traveler;
	}

	public void setTraveler(Traveler traveler) {
		this.traveler = traveler;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
