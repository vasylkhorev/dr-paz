package sk.upjs.drpaz.storage.exceptions;

public class EntityAlreadyReferencedInDatabaseException extends RuntimeException{

	private static final long serialVersionUID = 4439604483093499636L;

	public EntityAlreadyReferencedInDatabaseException(String message) {
		super(message);
	}

}
