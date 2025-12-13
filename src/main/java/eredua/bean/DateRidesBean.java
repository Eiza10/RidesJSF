package eredua.bean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

import businessLogic.BLFacade;
import domain.Ride;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped; 
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Date;

@Named("dateRidesBean")
@SessionScoped
public class DateRidesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date selectedDate;
    private List<Ride> rides;

    public DateRidesBean() {
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public void onDateSelect() {
        BLFacade facadeBL = FacadeBean.getBusinessLogic();
        this.rides = facadeBL.getRidesForDate(this.selectedDate);
    }
    
    public String goToRidesForDate() {
        // rides are already loaded by onDateSelect, just navigate
        return "RidesForDate?faces-redirect=true";
    }
}

    

