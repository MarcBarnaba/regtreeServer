package it.map1920.regtreeServer.data;

import java.io.Serializable;

/**
 * Classe astratta, modella le caratteristiche di un attributo,
 * sia esso continuo o discreto
 *
 */
public abstract class Attribute implements Serializable {
	
	private static final long serialVersionUID = 8L;
	private String name;
	private int index;
	
	public Attribute(String name, int index) {
		
		this.name = name;
		this.index = index;
	}
	
	public String getName() {
		
		return name;
	}
	
	public int getIndex() {
		
		return index;
	}
	
	public abstract String toString();
	
}
