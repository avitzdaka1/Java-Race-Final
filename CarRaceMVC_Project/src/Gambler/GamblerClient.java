package Gambler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import Entities.*;


public class GamblerClient {
	private Socket clientSocket;
	public ObjectOutputStream outputStreamToServer;
	public ObjectInputStream inputStreamFromServer;		
	private Random random;

	public GamblerClient() {
		
	}

	public void connectToServer() {

		new Thread(() -> {
			try {
				clientSocket = new Socket("127.0.0.1", 8888);				
				outputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
				inputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
				
				initReceiverFromServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void initReceiverFromServer() {

		new Thread(() -> {
			while (!clientSocket.isClosed()) {
				try {
					SERVER_COMMAND command = (SERVER_COMMAND) inputStreamFromServer.readObject();
					Object object = inputStreamFromServer.readObject();
					
					if (command == SERVER_COMMAND.closeConnection) break;
					receiveFromServer(command, object);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	private void receiveFromServer(SERVER_COMMAND command, Object object) {
		switch(command){
			case closeConnection:
				///closeConnection
				break;
 		}
		//
		//controller do something
		//
		//
	}
	
	public void initNewGambler(){
		
	}
 	
 	public void disconnectFromServer(){
		try {
			outputStreamToServer.writeObject(SERVER_COMMAND.closeConnection);
			outputStreamToServer.writeObject(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
