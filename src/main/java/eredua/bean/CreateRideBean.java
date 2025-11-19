package eredua.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import configuration.UtilDate;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

@Named("createRideBean")
@ViewScoped
public class CreateRideBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BLFacade facadeBL;
	
	private String origin;
	private String destination;
	private Date rideDate;
	private int seats;
	private float price;
	private String driverEmail = "driver1@gmail.com"; // Por ahora hardcodeado, luego se puede obtener de sesión
	
	public CreateRideBean() {
	    System.out.println("=== DEBUG: Entrando al constructor de CreateRideBean ===");
	    try {
	        // 1. Intentamos obtener la lógica
	        this.facadeBL = FacadeBean.getBusinessLogic();
	        
	        // 2. Verificamos si llegó null
	        if (this.facadeBL == null) {
	            System.out.println("=== ERROR CRÍTICO: FacadeBean.getBusinessLogic() devolvió NULL ===");
	        } else {
	            System.out.println("=== ÉXITO: FacadeBean cargado correctamente ===");
	        }
	        
	        this.rideDate = new Date();
	        System.out.println("=== DEBUG: Constructor finalizado con éxito ===");
	        
	    } catch (Exception e) {
	        System.out.println("=== EXCEPCIÓN EN CONSTRUCTOR ===");
	        e.printStackTrace(); // ¡Esto nos dirá el error real!
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

	public String getDriverEmail() {
		return driverEmail;
	}

	public void setDriverEmail(String driverEmail) {
		this.driverEmail = driverEmail;
	}

	public String createRide() {
		FacesContext context = FacesContext.getCurrentInstance();
		
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
				seats, price, driverEmail);
			
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
				"Error", e.getMessage()));
			return null;
			
		} catch (RideAlreadyExistException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
				"Error", e.getMessage()));
			return null;
		}
	}

	public String close() {
		return "Menua";
	}
}
