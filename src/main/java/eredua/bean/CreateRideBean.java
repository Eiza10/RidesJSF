package eredua.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import configuration.UtilDate;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

@Named("createRideBean")
@ViewScoped
public class CreateRideBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private LoginBean loginBean;
	
	private BLFacade facadeBL;
	
	private String origin;
	private String destination;
	private Date rideDate;
	private int seats;
	private float price;
	
	public CreateRideBean() {
	    System.out.println("=== DEBUG: CreateRideBean eraikitzailean sartzen ===");
	    try {
	        this.facadeBL = FacadeBean.getBusinessLogic();
	        
	        if (this.facadeBL == null) {
	            System.out.println("=== ERROR: FacadeBean.getBusinessLogic() is NULL ===");
	        } else {
	            System.out.println("=== SUCCESS: FacadeBean ondo kargatu da ===");
	        }
	        
	        this.rideDate = new Date();
	        System.out.println("=== DEBUG: Eraikitzailea ondo burutu da ===");
	        
	    } catch (Exception e) {
	        System.out.println("=== EXCEPTION ERAIKITZAILEAN ===");
	        e.printStackTrace(); 
	    }
	}
	
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Date getRideDate() {
		return rideDate;
	}

	public void setRideDate(Date rideDate) {
		this.rideDate = rideDate;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String createRide() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (loginBean == null || loginBean.getCurrentUser() == null) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
				"Error", "You must be logged in to create a ride"));
			return "Login?faces-redirect=true";
		}
		
		// Balidazioak
		if (origin == null || origin.trim().isEmpty() || 
		    destination == null || destination.trim().isEmpty()) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
				"Error", "Origin and destination are required"));
			return null;
		}
		
		if (seats <= 0) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
				"Error", "Seats must be greater than 0"));
			return null;
		}
		
		if (price <= 0) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
				"Error", "Price must be greater than 0"));
			return null;
		}
		
		try {
			facadeBL.createRide(origin, destination, UtilDate.trim(rideDate), 
				seats, price, loginBean.getCurrentUser().getEmail());
			
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
				"Success", "Ride created successfully!"));
			
			// Eremuak garbitu
			origin = null;
			destination = null;
			rideDate = new Date();
			seats = 0;
			price = 0;
			
			return null;
			
		} catch (RideMustBeLaterThanTodayException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
				"Error", "Ride must be later than today"));
			return null;
			
		} catch (RideAlreadyExistException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
				"Error", "Ride already exists"));
			return null;
		}
	}

	public String close() {
		return "Menua";
	}
}
