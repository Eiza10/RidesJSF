package eredua.bean;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import businessLogic.BLFacade;

@Named("registerBean")
@ViewScoped
public class RegisterBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String email;
	private String name;
	private String password;
	private String type = "Driver"; // Default value
	private String secretAnswer;
	
	private boolean questPassed = false;
	private String questMessage;
	private String questMessageStyle;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSecretAnswer() {
		return secretAnswer;
	}

	public void setSecretAnswer(String secretAnswer) {
		this.secretAnswer = secretAnswer;
	}
	
	public void checkQuest() {
		if ("admin".equalsIgnoreCase(secretAnswer != null ? secretAnswer.trim() : "")) {
			questPassed = true;
			questMessage = "Correct! You may proceed.";
			questMessageStyle = "color: green; font-weight: bold;";
		} else {
			questPassed = false;
			questMessage = "Incorrect code.";
			questMessageStyle = "color: red; font-weight: bold;";
		}
	}

	public String getQuestMessage() {
		return questMessage;
	}

	public String getQuestMessageStyle() {
		return questMessageStyle;
	}

	public String register() {
		if ("Admin".equals(type)) {
			if (!questPassed && (secretAnswer == null || !"admin".equalsIgnoreCase(secretAnswer.trim()))) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Access Denied", "Incorrect admin code!"));
				return null;
			}
		}
		
		BLFacade facade = FacadeBean.getBusinessLogic();
		boolean success = facade.register(email, name, password, type);
		if (success) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Registration successful"));
			return "Login?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User already exists"));
			return null;
		}
	}
}
