package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import Entities.*;

class HandlerGambler implements Runnable, MainServerListener {

	private Socket clientSocket;
	private boolean gamblerConnected;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private int id;
	private CarRaceServer mainServer;
	private Database database;
	
	public HandlerGambler(Socket clientSocket, CarRaceServer mainServer, int id, Database database){
		this.clientSocket = clientSocket;
		this.mainServer = mainServer;
		this.id = id;
		this.database = database;
	}
	
	@Override
	public void run() {
		try {
			inputStream = new ObjectInputStream(clientSocket.getInputStream());
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

			while (true) {
				MessageGambler inputMessage = (MessageGambler) inputStream.readObject();
				handleMessage(outputStream, inputMessage);
			}
		} 		
		catch (SocketException e) {
			e.printStackTrace();
		} 					
		catch (IOException e) {
			e.printStackTrace();
		} 		
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				clientSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void handleMessage(ObjectOutputStream outputStream, MessageGambler inputMessage) {
		switch (inputMessage.getCommand()) {
			
		case GamblerDisconnect:
			///
			break;
			
		case GamblerLogin:
			///
			break;
			
		case GamblerBet:
			///
			break;
		default:
			break;
		}
	}

	@Override
	public void serverDisconnection() {
		// TODO Auto-generated method stub
		
	}
}