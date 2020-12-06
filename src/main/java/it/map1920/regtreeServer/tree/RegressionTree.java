package it.map1920.regtreeServer.tree;

import java.io.*;
import java.util.TreeSet;

import it.map1920.regtreeServer.data.*;
import it.map1920.regtreeServer.server.UnknownValueException;

/**
 * Modella l'albero di regressione utilizzato per effettuare previsioni sul classValue.
 */
public class RegressionTree implements Serializable{

	//UID al momento da 1 a 14
	private static final long serialVersionUID = 1L;
	private Node root;
	private RegressionTree childTree[];
	
	
	public RegressionTree() {
		
	}

	/**
	 * Istanzia un sotto-albero dell'intero albero e avvia l'induzione esempi di training in input 
	 * @param trainingSet
	 */
	public RegressionTree(Data trainingSet) {
		
		learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, trainingSet.getNumberOfExamples() * 10 / 100);
	}
	
	/**
	 * Verifica se il sottoinsieme corrente può essere coperto da un nodo foglia
	 * controllando che il numero di esempi del training set compresi tra begin ed end
	 * sia minore uguale di numberOfExamplesPerLeaf.
	 */
	public boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
		
		return (end - begin + 1) <= numberOfExamplesPerLeaf;
	}
	
	/**
	 * Per ciascun attributo indipendente istanzia il DiscreteNode/ContinuousNode associato e
	 * seleziona il nodo di split con minore varianza tra quelli istanziati.
	 * Ordina la porzione di trainingSet corrente (tra begin ed end) rispetto
	 * all’attributo indipendente del nodo di split selezionato. Restituisce il nodo
	 * selezionato.
	 */
	private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
		
		SplitNode currentNode;

		TreeSet<SplitNode> ts = new TreeSet<SplitNode>();

		for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
			
			Attribute a = trainingSet.getExplanatoryAttribute(i);
			
			if (a instanceof DiscreteAttribute) {
				
				DiscreteAttribute attribute = (DiscreteAttribute) trainingSet.getExplanatoryAttribute(i);
				currentNode = new DiscreteNode(trainingSet, begin, end, attribute);
				
			} else {

				ContinuousAttribute attribute = (ContinuousAttribute) trainingSet.getExplanatoryAttribute(i);
				currentNode = new ContinuousNode(trainingSet, begin, end, attribute);
				
			}
			ts.add(currentNode);
		}

		trainingSet.sort(ts.last().getAttribute(), begin, end);

		return ts.last();
	}

	/**
	 * Genera un sotto-albero con il sotto-insieme di input istanziando 
	 * un nodo fogliare o un nodo di split. 
	 * In tal caso determina il miglior nodo rispetto al sotto-insieme di input
	 *  (determineBestSplitNode()), ed a tale nodo esso associa un sotto-albero 
	 *  avente radice il nodo medesimo (root) e avente un numero di rami pari
	 *   al numero dei figli determinati dallo split (childTree[]).
	 * Ricorsivamente su ogni oggetto di childTree[] sarà re-invocato il learnTree() 
	 * per l'apprendimento su un insieme ridotto del sotto-insieme attuale (begin..end).
	 * Condizione in cui il nodo di split non origina figli, il nodo diventa fogliare.
	 * @param trainingSet
	 * @param begin
	 * @param end
	 * @param numberOfExamplesPerLeaf è fissato al 10% della dimensione iniziale del training set
	 */
	private void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
		if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
			// determina la classe che compare pi� frequentemente nella partizione corrente
			root = new LeafNode(trainingSet, begin, end);
		} else // split node
		{
			root = determineBestSplitNode(trainingSet, begin, end);

			if (root.getNumberOfChildren() > 1) {
				childTree = new RegressionTree[root.getNumberOfChildren()];
				for (int i = 0; i < root.getNumberOfChildren(); i++) {
					childTree[i] = new RegressionTree();
					childTree[i].learnTree(trainingSet, ((SplitNode) root).getSplitInfo(i).beginIndex,
							((SplitNode) root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
				}
			} else
				root = new LeafNode(trainingSet, begin, end);

		}
	}
	
	/**
	 * Effettua la previsione del classValue.
	 * Scrive nello stream out una stringa "QUERY" e una stringa contenente gli split che l'utente può percorrere.
	 * Legge dallo stream in un Integer che rappresenta il nodo scelto.
	 * @param in Stream di input
	 * @param out Stream di output
	 * @return predicted classValue
	 * @throws UnknownValueException Nel caso in cui la risposta dell'utente non corrisponda a uno split esistente
	 */
	public Double predictClass(ObjectInputStream in, ObjectOutputStream out) throws UnknownValueException, ClassNotFoundException, IOException {
		if (root instanceof LeafNode)
			return ((LeafNode) root).getPredictedClassValue();
		else {
			int risp;
			out.writeObject(((SplitNode) root).formulateQuery());
			
			risp = ((Integer) in.readObject()).intValue();
			if (risp == -1 || risp >= root.getNumberOfChildren()) {
				throw new UnknownValueException(
						"The answer should be an integer between 0 and " + (root.getNumberOfChildren() - 1) + "!");
			} else {
				out.writeObject("QUERY");
				return childTree[risp].predictClass(in, out);
			}
		}
	}

	public String toString() {
		String tree = root.toString() + "\n";

		if (root instanceof LeafNode) {

		} else // split node
		{
			for (int i = 0; i < childTree.length; i++)
				tree += childTree[i];
		}
		return tree;
	}
	
	

	public void printTree() {
		System.out.println("********* TREE **********\n");
		System.out.println(toString());
		System.out.println("*************************\n");
	}
	
	public void printRules() {
		System.out.println("********* RULES **********\n");
		
		String rules = "";
		SplitNode splitRoot = (SplitNode) root;
		
		for (int i = 0; i < splitRoot.getNumberOfChildren(); i++) {
			rules = splitRoot.getAttribute().toString() + 
					splitRoot.getSplitInfo(i).getComparator() + 
					splitRoot.getSplitInfo(i).getSplitValue();
			childTree[i].printRules(rules);
		}
		
		System.out.println("*************************\n");
	}
	
	private void printRules(String current) {
		
		if (root instanceof LeafNode) {
			current += " -> Class = " + ((LeafNode) root).getPredictedClassValue();
			System.out.println(current);
		}
		else if(root instanceof SplitNode){
			SplitNode splitRoot = (SplitNode) root;
			for(int i = 0; i < splitRoot.getNumberOfChildren(); i++) {
				childTree[i].printRules(current + " AND " + 
										splitRoot.getAttribute().toString() + 
										splitRoot.getSplitInfo(i).getComparator() + 
										splitRoot.getSplitInfo(i).getSplitValue());
			}
		}
	}
	
	/**
	 * Serializza l'albero in un file.
	 * @param nomeFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void salva(String nomeFile) throws FileNotFoundException, IOException{
		
		FileOutputStream outFile = new FileOutputStream(nomeFile);
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(this);
		outStream.close();
		outFile.close();
	}
	
	/**
	 * Carica un albero di regressione salvato in un file.
	 * @param nomeFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static RegressionTree carica(String nomeFile) throws FileNotFoundException,IOException,ClassNotFoundException{
		
		FileInputStream inFile = new FileInputStream(nomeFile);
		ObjectInputStream inStream = new ObjectInputStream(inFile);
		RegressionTree loadedTree = (RegressionTree) inStream.readObject();
		inStream.close();
		inFile.close();
		return loadedTree;
	}
	
		
}
		
