package eredua.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.User;
import domain.Admin;

@Named("adminBean")
@ViewScoped
public class AdminBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private LoginBean loginBean;
	
	private List<User> users;
	private User selectedUser;
	private Map<String, Object> banDurationMap = new HashMap<>();
	private Map<String, String> banUnitMap = new HashMap<>();
	
	@PostConstruct
	public void init() {
		// Security check
		if (loginBean == null || loginBean.getCurrentUser() == null || !(loginBean.getCurrentUser() instanceof Admin)) {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("Login.xhtml");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		refreshUsers();
	}
	
	public void refreshUsers() {
		BLFacade facade = FacadeBean.getBusinessLogic();
		users = facade.getAllUsers();
	}
	
	public void banUser(User user) {
		Object durationObj = banDurationMap.get(user.getEmail());
		Integer duration = null;
		
		if (durationObj instanceof Integer) {
			duration = (Integer) durationObj;
		} else if (durationObj instanceof String) {
			try {
				duration = Integer.parseInt((String) durationObj);
			} catch (NumberFormatException e) {
				// ignore
			}
		} else if (durationObj instanceof Number) {
			duration = ((Number) durationObj).intValue();
		}
		
		if (duration == null || duration <= 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Duration must be greater than 0"));
			return;
		}
		
		String unit = banUnitMap.get(user.getEmail());
		if (unit == null || unit.isEmpty()) {
			unit = "Days"; // Default
		}
		
		try {
			BLFacade facade = FacadeBean.getBusinessLogic();
			facade.banUser(user.getEmail(), duration, unit);
			refreshUsers();
			banDurationMap.remove(user.getEmail()); // Clear input
			banUnitMap.remove(user.getEmail());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User banned successfully"));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error banning user"));
		}
	}
	
	public void unbanUser(User user) {
		try {
			BLFacade facade = FacadeBean.getBusinessLogic();
			facade.unbanUser(user.getEmail());
			refreshUsers();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User unbanned successfully"));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error unbanning user"));
		}
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

	public Map<String, Object> getBanDurationMap() {
		return banDurationMap;
	}

	public void setBanDurationMap(Map<String, Object> banDurationMap) {
		this.banDurationMap = banDurationMap;
	}

	public Map<String, String> getBanUnitMap() {
		return banUnitMap;
	}

	public void setBanUnitMap(Map<String, String> banUnitMap) {
		this.banUnitMap = banUnitMap;
	}
	
	public boolean isAdmin(User user) {
		return user instanceof Admin;
	}
}
