package it.map1920.regtreeServer.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.map1920.regtreeServer.data.*;
import it.map1920.regtreeServer.server.*;

/**
 * Rappresenta un nodo corrispondente ad un attributo continuo.
 */
public class ContinuousNode extends SplitNode {

	private static final long serialVersionUID = 7L;

	public ContinuousNode(Data trainingSet,int beginExampleIndex, int endExampleIndex, ContinuousAttribute attribute) {
		super(trainingSet,beginExampleIndex,endExampleIndex, attribute);
	}
	
	public void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		// Update mapSplit defined in SplitNode -- contiene gli indici del partizionamento
		Double currentSplitValue = (Double) trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
		double bestInfoVariance = 0;
		List<SplitInfo> bestMapSplit = null;

		for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
			Double value = (Double) trainingSet.getExplanatoryValue(i, attribute.getIndex());
			if (value.doubleValue() != currentSplitValue.doubleValue()) {
				// System.out.print(currentSplitValue +" var ");
				double localVariance = new LeafNode(trainingSet, beginExampleIndex, i - 1).getVariance();
				double candidateSplitVariance = localVariance;
				localVariance = new LeafNode(trainingSet, i, endExampleIndex).getVariance();
				candidateSplitVariance += localVariance;
				// System.out.println(candidateSplitVariance);
				if (bestMapSplit == null) {
					bestMapSplit = new ArrayList<SplitInfo>();
					bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
					bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
					bestInfoVariance = candidateSplitVariance;
				} else {

					if (candidateSplitVariance < bestInfoVariance) {
						bestInfoVariance = candidateSplitVariance;
						bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
						bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
					}
				}
				currentSplitValue = value;
			}
		}
		
		if (bestMapSplit != null) {
			mapSplit = bestMapSplit;
		
			// rimuovo split inutili (che includono tutti gli esempi nella stessa partizione)
			Iterator<SplitInfo> it = mapSplit.iterator();
			while(it.hasNext()) {
				SplitInfo split = it.next();
				if(split.getBeginIndex() == split.getEndIndex()) {
					it.remove();
				}
			}
		}
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
	public int testCondition (Object value) throws UnknownValueException {
		
		for( int i = 0; i < mapSplit.size(); i++) {
			if(mapSplit.get(i).getSplitValue().equals((Double)value)) {
				return i;
			}
		}
			
		throw new UnknownValueException("Valore "+value+" non trovato in nessuno degli split.");
	}
	
	public String toString() {
		
		return "CONTINUOUS " + super.toString();
	}

}
