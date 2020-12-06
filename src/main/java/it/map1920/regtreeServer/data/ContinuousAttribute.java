package it.map1920.regtreeServer.data;

import java.io.Serializable;

/**
 * Modella un attributo continuo.
 *
 */
public class ContinuousAttribute extends Attribute implements Serializable{

	private static final long serialVersionUID = 9L;

	public ContinuousAttribute(String name, int index) {
		super(name, index);
	}
	
	public String toString() {
		
		return this.getName();
	}
	
}
