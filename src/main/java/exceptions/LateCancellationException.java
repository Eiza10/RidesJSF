package exceptions;

public class LateCancellationException extends Exception {
	private static final long serialVersionUID = 1L;

	public LateCancellationException() {
		super();
	}

	public LateCancellationException(String message) {
		super(message);
	}
}
