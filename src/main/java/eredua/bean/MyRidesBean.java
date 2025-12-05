package eredua.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import businessLogic.BLFacade;
import domain.Booking;
import exceptions.LateCancellationException;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named("myRidesBean")
@ViewScoped
public class MyRidesBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<Booking> bookings;
	
	public MyRidesBean() {
	}
	
	@PostConstruct
	public void init() {
		FacesContext context = FacesContext.getCurrentInstance();
		LoginBean loginBean = context.getApplication().evaluateExpressionGet(context, "#{loginBean}", LoginBean.class);
		
		if (loginBean != null && loginBean.isLoggedIn() && loginBean.isTraveler()) {
			BLFacade facade = FacadeBean.getBusinessLogic();
			this.bookings = facade.getBookingsByTraveler(loginBean.getCurrentUser().getEmail());
		}
	}
	
	public void cancelBooking(Booking booking) {
		BLFacade facade = FacadeBean.getBusinessLogic();
		try {
			double refund = facade.cancelBooking(booking.getBookingNumber());
			
			// Refresh list
			FacesContext context = FacesContext.getCurrentInstance();
			LoginBean loginBean = context.getApplication().evaluateExpressionGet(context, "#{loginBean}", LoginBean.class);
			this.bookings = facade.getBookingsByTraveler(loginBean.getCurrentUser().getEmail());
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Booking cancelled. Refunded: " + refund + "€"));
		} catch (LateCancellationException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
		}
	}
	
	public boolean isCancellable(Booking booking) {
		if (booking == null || booking.getRide() == null) return false;
		long diff = booking.getRide().getDate().getTime() - new Date().getTime();
		double hours = diff / (60 * 60 * 1000.0);
		return hours >= 48;
	}

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
}
