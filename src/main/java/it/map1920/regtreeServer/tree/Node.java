package it.map1920.regtreeServer.tree;

import java.io.Serializable;
import it.map1920.regtreeServer.data.*;

/**
 * Modella un nodo (foglia o intermedio) 
 * dell'albero di decisione.
 * Tiene conto del numero di nodi generati nell'albero.
 */
public abstract class Node implements Serializable{
	
	private static final long serialVersionUID = 2L;
	private static int idNodeCount = 0; //Contatore nodi generati nell'albero
	private int idNode; //Identificativo numerico del nodo
	private int beginExampleIndex;
	private int endExampleIndex;
	private double variance; //SSE calcolato rispetto all'attributo di classe, nel sotto-insieme di training del nodo

	/**
	 * Crea un nuovo nodo 
	 * @param trainingSet
	 * @param beginExampleIndex Indice del primo esempio esaminato dal nodo corrente
	 * @param endExampleIndex Indice dell'ultimo esempio coperto dal nodo corrente
	 */
	public Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		
		idNode = idNodeCount++;
		this.beginExampleIndex = beginExampleIndex;
		this.endExampleIndex = endExampleIndex;
		
		//Calcolo media dell'attributo di classe
		double meanClassValue = 0;
		for(int i = beginExampleIndex; i <= endExampleIndex; i++) {
			meanClassValue += trainingSet.getClassValue(i);
		}
		meanClassValue /= (endExampleIndex - beginExampleIndex + 1);
		
		
		variance = 0;
		for(int j = beginExampleIndex; j <= endExampleIndex; j++) {
			variance += Math.pow(trainingSet.getClassValue(j) - meanClassValue, 2);
		}
	}
	
	public int getIdNode() {
		
		return idNode;
	}
	
	public int getBeginExampleIndex() {
		
		return beginExampleIndex;
	}

	public int getEndExampleIndex() {
		
		return endExampleIndex;
	}
	
	public double getVariance() {
		
		return variance;
	}
	
	public abstract int getNumberOfChildren();
	
	public String toString() {
		
		String state = " ";
		state += "[Examples:" + beginExampleIndex + "-" + endExampleIndex + "]\n";
		state += "Variance: " + variance;
		
		return state;
	}

}
