package domain;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("TRAVELER")
public class Traveler extends User {
	private static final long serialVersionUID = 1L;

	public Traveler() {
		super();
	}

	public Traveler(String email, String name, String password) {
		super(email, name, password);
	}
}
