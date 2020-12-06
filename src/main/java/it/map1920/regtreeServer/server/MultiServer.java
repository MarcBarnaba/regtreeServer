package it.map1920.regtreeServer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServer {

	private int PORT;
	
	/**
	 * Inizializza la porta ed invoca run() 
	 * @param port
	 */
	public MultiServer(int port) {
		PORT = port;
		run();
	}
	
	/**
	 * Istanzia un oggetto istanza della classe ServerSocket 
	 * che si pone in attesa di richiesta di 
	 * connessioni da parte del client.
	 * Ad ogni nuova richiesta connessione si istanzia ServerOneClient.
	 */
	private void run() {

		try {
			ServerSocket s = new ServerSocket(PORT);
			System.out.println("Started: " + s);
			try {
				
				while (true) {
					// attende una connessione
					Socket socket = s.accept();
					// connessione accettata
				
					System.out.println("Connessione accettata: " + socket);
					ServerOneClient serverClient = new ServerOneClient(socket);
	
					}
				
			} finally {
				s.close();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
