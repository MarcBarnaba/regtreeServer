package it.map1920.regtreeServer.data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Modella un attributo discreto.
 * Conserva i valori che può assumere in ordine crescente.
 *
 */
public class DiscreteAttribute extends Attribute implements Iterable<String>{
	
	private static final long serialVersionUID = 10L;
	private Set<String> values=new TreeSet<>(); // order by asc
	
	public DiscreteAttribute(String name, int index, Set<String> values) {
		super(name,index);
		this.values=values;
	}
	
	public int getNumberOfDistinctValues(){
		return values.size();
	}

	@Override
	public Iterator<String> iterator() {
		return values.iterator();
	}
	
	public String toString() {
		return this.getName();
	}
	
	
}
