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
	private CarRaceServer mainServer;
	
	public HandlerGambler(Socket clientSocket, CarRaceServer mainServer){
		this.clientSocket = clientSocket;
		this.mainServer = mainServer;
	}
	
	@Override
	public void run() {
		try {
			inputStream = new ObjectInputStream(clientSocket.getInputStream());
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

			while (gamblerConnected) {
				Message inputMessage = (Message) inputStream.readObject();
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

	private void handleMessage(ObjectOutputStream outputStream, Message inputMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serverDisconnection() {
		// TODO Auto-generated method stub
		
	}
}