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
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("queryRidesBean")
@ViewScoped
public class QueryRidesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private BLFacade facadeBL;

    private List<String> departCities;
    private List<String> destinationCities;
    private String from;
    private String to;
    private Date date;
    private List<Ride> rides;
    private List<Date> datesWithRides;

    public QueryRidesBean() {
        this.facadeBL = FacadeBean.getBusinessLogic();
    }

    @PostConstruct
    public void init() {
    	departCities = facadeBL.getDepartCities();
    	destinationCities = new ArrayList<String>();
        this.date = new Date();
    }

    public List<String> getDepartCities() {
        return departCities;
    }

    public void setDepartCities(List<String> departCities) {
        this.departCities = departCities;
    }
    
    public List<String> getDestinationCities() {
        return facadeBL.getDestinationCities(from);
    }

    public void setDestinationCities(List<String> destinationCities) {
        this.departCities = destinationCities;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }
    
    public List<Date> getDatesWithRides() {
        return datesWithRides;
    }

    public void setDatesWithRides(List<Date> datesWithRides) {
        this.datesWithRides = datesWithRides;
    }

    public void onDateSelect() {
        searchRides();
    }

    public void searchRides() {
        if (from != null && to != null && date != null) {
            if (from.equals(to)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Origin and destination cannot be the same."));
                this.rides = new Vector<>(); 
            } else {
                this.rides = facadeBL.getRides(from, to, date);
                this.datesWithRides = facadeBL.getThisMonthDatesWithRides(from, to, date);
            }
        }
    }
    
    public String getDatesWithRidesAsJson() {
        if (from == null || from.isEmpty() || to == null || to.isEmpty()) {
            return "[]";
        }
        List<Date> datesWithRides = facadeBL.getThisMonthDatesWithRides(from, to, date);
        System.out.println("Dates with rides: " + datesWithRides);
        if (datesWithRides == null || datesWithRides.isEmpty()) {
            return "[]";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String json = datesWithRides.stream()
                                    .map(formatter::format)
                                    .collect(Collectors.joining("','", "['", "']"));
        return json;
    }

    public String getDatesWithRidesJson() {
        if (datesWithRides == null || datesWithRides.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < datesWithRides.size(); i++) {
            sb.append(datesWithRides.get(i).getTime());
            if (i < datesWithRides.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public String close() {
		return "Menua";
	}
}
