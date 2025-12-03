package eredua.bean;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.User;
import domain.Admin;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String email;
	private String password;
	private User currentUser;

	public LoginBean() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User getCurrentUser() {
		return currentUser;
	}
	
	public boolean isLoggedIn() {
		return currentUser != null;
	}
	
	public boolean isAdmin() {
		return currentUser != null && currentUser instanceof Admin;
	}
	
	public boolean isDriver() {
		return currentUser != null && currentUser instanceof domain.Driver;
	}
	
	public boolean isTraveler() {
		return currentUser != null && currentUser instanceof domain.Traveler;
	}

	public String login() {
		BLFacade facade = FacadeBean.getBusinessLogic();
		try {
			User u = facade.login(email, password);
			if (u != null) {
				currentUser = u;
				return "Menua?faces-redirect=true";
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid email or password"));
				return null;
			}
		} catch (RuntimeException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
			return null;
		}
	}
	
	public String logout() {
		currentUser = null;
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "Login?faces-redirect=true";
	}
}
