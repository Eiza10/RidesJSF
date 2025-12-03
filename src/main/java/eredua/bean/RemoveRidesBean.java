package eredua.bean;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.Ride;
import domain.User;
import domain.Driver;
import domain.Admin;

@Named("removeRidesBean")
@ViewScoped
public class RemoveRidesBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private LoginBean loginBean;
	
	private List<Ride> rides;
	
	@PostConstruct
	public void init() {
		User currentUser = loginBean.getCurrentUser();
		if (currentUser == null) {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("Login.xhtml");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		refreshRides();
	}
	
	public void refreshRides() {
		BLFacade facade = FacadeBean.getBusinessLogic();
		User currentUser = loginBean.getCurrentUser();
		
		if (currentUser instanceof Admin) {
			rides = facade.getAllRides();
		} else if (currentUser instanceof Driver) {
			rides = facade.getRidesByDriver(currentUser.getEmail());
		} else {
			// Travelers shouldn't be here, but just in case
			rides = null;
		}
	}
	
	public void deleteRide(Ride ride) {
		try {
			BLFacade facade = FacadeBean.getBusinessLogic();
			facade.deleteRide(ride);
			refreshRides();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Ride deleted successfully"));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error deleting ride"));
			e.printStackTrace();
		}
	}

	public List<Ride> getRides() {
		return rides;
	}

	public void setRides(List<Ride> rides) {
		this.rides = rides;
	}
}
