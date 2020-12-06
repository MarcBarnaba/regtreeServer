package it.map1920.regtreeServer.database;

public class EmptySetException extends Exception {

	private static final long serialVersionUID = 13L;

	public EmptySetException() {
		
	}
	
	public EmptySetException(String msg) {
		
		super(msg);
	}
}
