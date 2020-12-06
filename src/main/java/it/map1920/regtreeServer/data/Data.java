package it.map1920.regtreeServer.data;


import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import it.map1920.regtreeServer.database.*;;

/**
 * Conserva il training set e le informazioni ad esso relative come
 * la sua cardinalità, la lista degli attributi indipendenti e l'attributo target.
 * Fornisce metodi per accedere a queste informazioni e per ordinare il training set
 * rispetto ad un attributo.
 *
 */
public class Data {

	private List<Example> data = new LinkedList<Example>(); // contiene il training set
	private int numberOfExamples; // Cardinalità del training set
	private List<Attribute> explanatorySet = new LinkedList<Attribute>(); // Contiene gli attributi indipendenti
	private ContinuousAttribute classAttribute; // (target attribute), è numerico

	/**
	 * Stabilisce la connessione al database e avvalora gli attributi d'istanza.
	 * @param tableName Nome della tabella da cui estrarre i dati
	 * @throws TrainingDataException In caso di problemi di connessione al db, 
	 * tabella inesistente o vuota, tabella con meno di due colonne o priva di classAttribute
	 */
	public Data(String tableName) throws TrainingDataException {
		System.out.println("Starting data acquisition phase!");

		DbAccess db = null;
		try {
			db = new DbAccess();
		} catch (DatabaseConnectionException e) {
			String msg = "\nImpossibile stabilire connessione al database";
			throw new TrainingDataException(msg);
		}
		
		try {
			db.initConnection();
		} catch (DatabaseConnectionException e) {
			String msg = "\nImpossibile stabilire connessione al database";
			throw new TrainingDataException(msg);
		}
		
		
		TableData td = null;
		try {
			td = new TableData(db);
			data = td.getTransazioni(tableName);
		} catch (SQLException e) {
			String msg = "\nLa tabella non esiste ";
			throw new TrainingDataException(msg +"["+ e.getMessage()+"]");
		} catch (EmptySetException e) {
			String msg = "\nTraining Set vuoto";
			throw new TrainingDataException(msg);
		}
		
		numberOfExamples = data.size();
		
		TableSchema ts = null;
		try {
			ts = new TableSchema(db,tableName);
		} catch (SQLException e) {
			String msg = "\n";
			throw new TrainingDataException(msg + e.getMessage());
		}
		
		if (ts.getNumberOfAttributes() < 2) {
			throw new TrainingDataException("La tabella ha meno di due colonne");
		}
		
		Set<String> values = new TreeSet<String>();
		
		Iterator<Column> it = ts.iterator();
		int iAttribute = 0;
		Column col = null;
		while (it.hasNext()){
			col = it.next();
			if(col.isNumber()) {
				explanatorySet.add(new ContinuousAttribute(col.getColumnName(), iAttribute));
			} else {
				try {
					for( Object o : td.getDistinctColumnValues(tableName, col)) {
						values.add((String) o);
					}
				} catch (SQLException e) {
					String msg = "\n";
					throw new TrainingDataException(msg + e.getMessage());
				}
				explanatorySet.add(new DiscreteAttribute(col.getColumnName(), iAttribute, values));
			}
			iAttribute++;
		}
		if (col.isNumber()) {
			classAttribute = new ContinuousAttribute(col.getColumnName(), iAttribute);
		} else {
			throw new TrainingDataException("L'ultimo attributo non è numerico");
		}
	}
	
	public int getNumberOfExamples() {

		return numberOfExamples;
	}

	public int getNumberOfExplanatoryAttributes() {

		return explanatorySet.size();
	}

	public Double getClassValue(int exampleIndex) {
		return (Double) data.get(exampleIndex).get(classAttribute.getIndex() - 1);
	}

	public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {

		return data.get(exampleIndex).get(attributeIndex);
	}

	public Attribute getExplanatoryAttribute(int index) {

		return explanatorySet.get(index);
	}

	public ContinuousAttribute getClassAttribute() {

		return classAttribute;
	}

	public String toString() {
		String value = "";
		for (int i = 0; i < numberOfExamples; i++) {
			for (int j = 0; j < explanatorySet.size(); j++)
				value += data.get(i).get(j) + ",";

			value += data.get(i).get(explanatorySet.size()) + "\n";
		}
		return value;

	}

	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {

		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}

	// scambio esempio i con esempio j
	private void swap(int i, int j) {
		
		Collections.swap(data, i, j);
	}

	/**
	 * Partiziona il vettore rispetto al DiscreteAttribute e restiutisce il punto di
	 * separazione
	 */
	private int partition(DiscreteAttribute attribute, int inf, int sup) {
		int i, j;

		i = inf;
		j = sup;
		int med = (inf + sup) / 2;
		String x = (String) getExplanatoryValue(med, attribute.getIndex());
		swap(inf, med);

		while (true) {

			while (i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
				i++;

			}

			while (((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
				j--;

			}

			if (i < j) {
				swap(i, j);
			} else
				break;
		}
		swap(inf, j);
		return j;

	}

	/**
	 * Partiziona il vettore rispetto al ContinuousAttribute e restiutisce il punto di
	 * separazione
	 */
	private int partition(ContinuousAttribute attribute, int inf, int sup) {
		int i, j;

		i = inf;
		j = sup;
		int med = (inf + sup) / 2;
		Double x = (Double) getExplanatoryValue(med, attribute.getIndex());
		swap(inf, med);

		while (true) {

			while (i <= sup && ((Double) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
				i++;

			}

			while (((Double) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
				j--;

			}

			if (i < j) {
				swap(i, j);
			} else
				break;
		}
		swap(inf, j);
		return j;

	}

	/**
	 * Algoritmo quicksort per l'ordinamento di un array usando come
	 * relazione d'ordine totale "<="
	 * 
	 */
	private void quicksort(Attribute attribute, int inf, int sup) {

		if (sup >= inf) {

			int pos;
			if (attribute instanceof DiscreteAttribute)
				pos = partition((DiscreteAttribute) attribute, inf, sup);
			else
				pos = partition((ContinuousAttribute) attribute, inf, sup);

			if ((pos - inf) < (sup - pos + 1)) {
				quicksort(attribute, inf, pos - 1);
				quicksort(attribute, pos + 1, sup);
			} else {
				quicksort(attribute, pos + 1, sup);
				quicksort(attribute, inf, pos - 1);
			}

		}

	}

}