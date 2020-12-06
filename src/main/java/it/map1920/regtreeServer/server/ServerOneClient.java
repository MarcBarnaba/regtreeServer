package it.map1920.regtreeServer.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import it.map1920.regtreeServer.data.Data;
import it.map1920.regtreeServer.data.TrainingDataException;
import it.map1920.regtreeServer.tree.RegressionTree;

public class ServerOneClient extends Thread {

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	/**
	 * Inizializza gli attributi socket, in e out. Avvia il thread.
	 */
	public ServerOneClient(Socket s) throws IOException {

		socket = s;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		start();

	}

	/**
	 * Riscrive il metodo run della superclasse Thread al fine di gestire le
	 * richieste del client e in modo da rispondere alle richieste.
	 */
	@Override
	public void run() {

		String answer = "";
		String tableName;
		RegressionTree tree = null;

		try {
			Object clientQuery = in.readObject();

			if (clientQuery instanceof Integer) {
				clientQuery = (Integer) clientQuery;

				if (((Integer) clientQuery).intValue() == 0) {
					tableName = (String) in.readObject();
					Data trainingSet = null;

					trainingSet = new Data(tableName);

					out.writeObject("OK");

					if (((Integer) in.readObject()).intValue() == 1) {
						tree = new RegressionTree(trainingSet);
						out.writeObject("OK");

						tree.salva(tableName + ".dmp");

					}

				} else if (((Integer) clientQuery).intValue() == 2) {
					tableName = (String) in.readObject();

					tree = RegressionTree.carica(tableName + ".dmp");
					out.writeObject("OK");

				}

				do {
					clientQuery = in.readObject();

					if (((Integer) clientQuery).intValue() == 3) {
						out.writeObject("QUERY");
						Double prediction;

						try {
							prediction = tree.predictClass(in, out);
							out.writeObject("OK");
							out.writeObject(prediction);
						} catch (UnknownValueException e) {
							answer = e.getMessage();
							out.writeObject(answer);
						}
					}
				} while (((Integer) clientQuery).intValue() == 3);

			}
		} catch (ClassNotFoundException | IOException e) {
			answer = "ERR";
			try {
				out.writeObject(answer);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (TrainingDataException e) {
			answer = e.getMessage();
			try {
				out.writeObject(answer);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			System.out.println("closing...");
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
