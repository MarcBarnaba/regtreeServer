package it.map1920.regtreeServer.tree;
import it.map1920.regtreeServer.data.*;

/**
 * Modella un nodo foglia dell'albero di regressione.
 *
 */
public class LeafNode extends Node {
	
	private static final long serialVersionUID = 5L;
	private Double predictedClassValue; //Valore dell'attributo di classe espresso nella foglia corrente
	
	/**
	 * Crea un nuovo nodo foglia, avvalorando l'attributo predictedClassValue 
	 * come media dei valori dell'attributo
	 * di classe che ricadono nel sottoinsieme di trainingSet (begin,end)
	 * @param trainingSet
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 */
	public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		super(trainingSet, beginExampleIndex, endExampleIndex);
		
		predictedClassValue = 0.0;
		for(int i = beginExampleIndex; i <= endExampleIndex; i++) {
			predictedClassValue += trainingSet.getClassValue(i);
		}
		predictedClassValue /= (endExampleIndex - beginExampleIndex + 1);
	}
	
	public Double getPredictedClassValue() {
		
		return predictedClassValue;
	}
	
	public int getNumberOfChildren() {
		return 0;
	}
	
	public String toString() {
		
		return "LEAF : class-"  + predictedClassValue + super.toString();
	}
}
