package eredua.bean;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.User;

@Named("walletBean")
@ViewScoped
public class WalletBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private double amount;
	private double balance;
	private User user;
	
	public WalletBean() {
	}
	
	@PostConstruct
	public void init() {
		FacesContext context = FacesContext.getCurrentInstance();
		LoginBean loginBean = context.getApplication().evaluateExpressionGet(context, "#{loginBean}", LoginBean.class);
		
		if (loginBean != null && loginBean.isLoggedIn()) {
			String email = loginBean.getCurrentUser().getEmail();
			BLFacade facade = FacadeBean.getBusinessLogic();
			this.user = facade.getUser(email);
			if (this.user != null) {
				this.balance = this.user.getWallet();
			}
		}
	}
	
	public void deposit() {
		if (amount <= 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Amount must be positive."));
			return;
		}
		
		BLFacade facade = FacadeBean.getBusinessLogic();
		facade.depositMoney(user.getEmail(), amount);
		
		this.user = facade.getUser(user.getEmail());
		this.balance = this.user.getWallet();
		this.amount = 0; 
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Money deposited successfully."));
	}
	
	public void withdraw() {
		if (amount <= 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Amount must be positive."));
			return;
		}
		
		BLFacade facade = FacadeBean.getBusinessLogic();
		boolean success = facade.withdrawMoney(user.getEmail(), amount);
		
		if (success) {
			this.user = facade.getUser(user.getEmail());
			this.balance = this.user.getWallet();
			this.amount = 0; 
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Money withdrawn successfully."));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Insufficient funds."));
		}
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
}
