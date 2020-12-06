package it.map1920.regtreeServer.server;

public class UnknownValueException extends Exception {

	private static final long serialVersionUID = 14L;

	public UnknownValueException() {}
	
	public UnknownValueException(String msg) {
		super(msg);
	}
}
