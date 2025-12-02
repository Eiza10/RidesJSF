package eredua.bean;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.Driver;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String email;
	private String password;
	private Driver currentUser;

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

	public Driver getCurrentUser() {
		return currentUser;
	}
	
	public boolean isLoggedIn() {
		return currentUser != null;
	}

	public String login() {
		BLFacade facade = FacadeBean.getBusinessLogic();
		Driver d = facade.login(email, password);
		if (d != null) {
			currentUser = d;
			return "Menua?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid email or password"));
			return null;
		}
	}
	
	public String logout() {
		currentUser = null;
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "Login?faces-redirect=true";
	}
}
