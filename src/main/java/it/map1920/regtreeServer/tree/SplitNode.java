package it.map1920.regtreeServer.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.map1920.regtreeServer.data.*;
import it.map1920.regtreeServer.server.UnknownValueException;

/**
 * Rappresenta un nodo di split, e conserva caratteristiche comuni a nodi
 * discreti e continui.
 */
abstract class SplitNode extends Node implements Comparable<SplitNode>, Serializable {

	private static final long serialVersionUID = 3L;

	/**
	 * Aggrega tutte le informazioni riguardanti un nodo di split
	 */
	protected class SplitInfo implements Serializable {

		private static final long serialVersionUID = 4L;
		Object splitValue;
		int beginIndex;
		int endIndex;
		int numberChild;
		String comparator = "=";

		/**
		 * Costruttore che avvalora gli attributi di classe per split a valori discreti
		 * 
		 * @param splitValue  valore di tipo Object (di un attributo indipendente) che
		 *                    definisce uno split
		 * @param beginIndex
		 * @param endIndex
		 * @param numberChild numero di split (nodi figli) originanti dal nodo corrente
		 */
		protected SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild) {
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
		}

		/**
		 * Costruttore che avvalora gli attributi di classe per generici split
		 * 
		 * @param splitValue  valore di tipo Object (di un attributo indipendente) che
		 *                    definisce uno split
		 * @param beginIndex
		 * @param endIndex
		 * @param numberChild numero di split (nodi figli) originanti dal nodo corrente
		 * @param comparator  operatore matematico che definisce il test nel nodo
		 *                    corrente
		 */
		protected SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
			this.comparator = comparator;
		}

		protected int getBeginIndex() {
			return beginIndex;
		}

		protected int getEndIndex() {
			return endIndex;
		}

		protected Object getSplitValue() {
			return splitValue;
		}

		public String toString() {
			return "Child: " + numberChild + " Split Value" + comparator + splitValue + "[Examples:" + beginIndex + "-"
					+ endIndex + "]";
		}

		protected String getComparator() {
			return comparator;
		}

	}

	private Attribute attribute;

	protected List<SplitInfo> mapSplit = new ArrayList<SplitInfo>();

	private double splitVariance; // il valore di varianza a seguuito del partizionamento indotto dallo split
									// corrente

	public abstract void setSplitInfo(Data trainingSet, int beginExampelIndex, int endExampleIndex,
			Attribute attribute);

	public abstract int testCondition(Object value) throws UnknownValueException;

	/**
	 * Invoca il costruttore della superclasse, ordina i valori di input per gli
	 * esempi beginExampleIndex-endExampleIndex per determinare i possibili split e
	 * popolare mapSplit, computa lo SSE (splitVariance) per l'attributo usato nello
	 * split sulla base del partizionamento indotto dallo split (lo stesso è la
	 * somma degli SSE calcolati su ciascuno SplitInfo collezionato in mapSplit)
	 * 
	 * @param trainingSet
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 * @param attribute
	 */
	public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {

		super(trainingSet, beginExampleIndex, endExampleIndex);
		this.attribute = attribute;
		trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
		setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);

		// compute variance
		splitVariance = 0;
		for (SplitInfo splitinfo : mapSplit) {
			double localVariance = new LeafNode(trainingSet, splitinfo.getBeginIndex(), splitinfo.getEndIndex())
					.getVariance();
			splitVariance += (localVariance);
		}

	}

	public Attribute getAttribute() {
		return attribute;
	}

	public double getVariance() {
		return splitVariance;
	}

	public int getNumberOfChildren() {
		return mapSplit.size();
	}

	public SplitInfo getSplitInfo(int child) {
		return mapSplit.get(child);
	}

	/**
	 * 
	 * @return query contenente i valori di mapSplit per l'attributo dello SplitNode
	 *         corrente
	 */
	public String formulateQuery() {
		String query = "";
		for (int i = 0; i < mapSplit.size(); i++)
			query += (i + ":" + attribute + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue()) + "\n";
		return query;
	}

	@Override
	public String toString() {
		String v = "SPLIT : attribute=" + attribute + " " + super.toString() + " Split Variance: " + getVariance()
				+ "\n";

		for (int i = 0; i < mapSplit.size(); i++) {
			v += "\t" + mapSplit.get(i) + "\n";
		}

		return v;
	}

	@Override
	public int compareTo(SplitNode o) {

		if (o.getVariance() < this.getVariance()) {
			return -1;
		} else if (o.getVariance() > this.getVariance()) {
			return 1;
		} else {
			return 0;
		}
	}
}
