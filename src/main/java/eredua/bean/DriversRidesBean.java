package eredua.bean;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Ride;

@Named("driversRidesBean")
@SessionScoped
public class DriversRidesBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Driver> drivers;
	private Driver selectedDriver;
	private List<Ride> driverRides;

	@PostConstruct // Bean-a ondo kargatu dela bermatzeko
	public void init() {
		try {
			BLFacade facade = FacadeBean.getBusinessLogic();
			if (facade != null) {
				drivers = facade.getAllDrivers();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Driver> getDrivers() {
		return drivers;
	}

	public void setDrivers(List<Driver> drivers) {
		this.drivers = drivers;
	}

	public Driver getSelectedDriver() {
		return selectedDriver;
	}

	public void setSelectedDriver(Driver selectedDriver) {
		this.selectedDriver = selectedDriver;
	}

	public List<Ride> getDriverRides() {
		return driverRides;
	}

	public void setDriverRides(List<Ride> driverRides) {
		this.driverRides = driverRides;
	}
	
	public String viewRides() {
		if (selectedDriver != null) {
			BLFacade facade = FacadeBean.getBusinessLogic();
			if (facade != null) {
				driverRides = facade.getRidesByDriver(selectedDriver.getEmail());
				return "DriverRides?faces-redirect=true";
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_WARN, "Abisua", "Mesedez, aukeratu gidari bat lehenengo."));
		}
		return null;
	}
}
