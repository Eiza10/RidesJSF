package exceptions;

public class NotEnoughSeatsException extends Exception {
	private static final long serialVersionUID = 1L;

	public NotEnoughSeatsException() {
		super();
	}

	public NotEnoughSeatsException(String message) {
		super(message);
	}
}
