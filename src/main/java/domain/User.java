package domain;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name="User")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="user_type", discriminatorType=DiscriminatorType.STRING)
public abstract class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id 
	private String email;
	private String name; 
	private String password;
	
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date banExpirationDate;
	
	private double wallet = 0.0;

	public User(String email, String name, String password) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.wallet = 0.0;
	}

	public User() {
		super();
	}

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
	
	public java.util.Date getBanExpirationDate() {
		return banExpirationDate;
	}

	public void setBanExpirationDate(java.util.Date banExpirationDate) {
		this.banExpirationDate = banExpirationDate;
	}
	
	public double getWallet() {
		return wallet;
	}

	public void setWallet(double wallet) {
		this.wallet = wallet;
	}
	
	public boolean isBanned() {
		return banExpirationDate != null && banExpirationDate.after(new java.util.Date());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}
}
