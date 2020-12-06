package it.map1920.regtreeServer.tree;

import it.map1920.regtreeServer.data.*;
import it.map1920.regtreeServer.server.UnknownValueException;

/**
 * Modella un nodo di Split per attributi discreti.
 */
public class DiscreteNode extends SplitNode {
	
	private static final long serialVersionUID = 6L;

	public DiscreteNode(Data trainingSet,int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {
		
		super(trainingSet,beginExampleIndex,endExampleIndex, attribute);
	}
	
	/**
	 * Istanzia oggetti SpliInfo (definita come inner class in Splitnode) 
	 * con ciascuno dei valori discreti dell’attributo relativamente al sotto-insieme di training corrente
	 *  (ossia la porzione di trainingSet compresa tra beginExampleIndex e endExampleIndex), quindi
	 *  popola mapSplit con tali oggetti.
	 */
	public void setSplitInfo(Data trainingSet,int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		
		//Istanzio oggetti SplitInfo ogni volta che trovo un "nuovo" valore dell'attributo
		int numberChild = 0;
		int begin = beginExampleIndex;
		String explValue = (String)trainingSet.getExplanatoryValue(begin, attribute.getIndex());
		for(int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
			
			if(!explValue.equals((String)trainingSet.getExplanatoryValue(i, attribute.getIndex()))) {
				mapSplit.add(numberChild, new SplitInfo(explValue,begin,i-1,numberChild));
				explValue = (String)trainingSet.getExplanatoryValue(i, attribute.getIndex());
				numberChild++;
				begin = i;
			}
		}
		mapSplit.add(numberChild, new SplitInfo(explValue, begin, endExampleIndex, numberChild));
	}
	
	/**
	 * Effettua il confronto del valore in input rispetto al valore contenuto 
	 * nell’attributo splitValue di ciascuno degli oggetti SplitInfo
	 *  collezionati in mapSplit e restituisce l'identificativo dello split
	 *   (indice della posizione in mapSplit) con cui
	 * il test è positivo
	 * @param value elemento da cercare
	 * @throws UnknownValueException Quando non trova value in mapSplit
	 */
	public int testCondition(Object value) throws UnknownValueException {
		
		for( int i = 0; i < mapSplit.size(); i++) {
			if(mapSplit.get(i).getSplitValue().equals((String) value)) {
				return i;
			}
		}
			
		throw new UnknownValueException("Valore "+value+" non trovato in nessuno degli split.");
	}
	
	public String toString() {
		
		return "DISCRETE " + super.toString();
		
	}

}
