package sk.upjs.drpaz.storage;

public class UniqueAlreadyInDatabaseException extends RuntimeException {

	private static final long serialVersionUID = 6963352834065662554L;

	public UniqueAlreadyInDatabaseException(String message) {
		super(message);
	}

}
