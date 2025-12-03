package domain;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
	private static final long serialVersionUID = 1L;

	public Admin() {
		super();
	}

	public Admin(String email, String name, String password) {
		super(email, name, password);
	}
}
