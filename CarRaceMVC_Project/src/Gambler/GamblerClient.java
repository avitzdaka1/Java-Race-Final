package Gambler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import Entities.*;


public class GamblerClient implements Runnable{
	private Socket clientSocket;
	public ObjectOutputStream outputStreamToServer;
	public ObjectInputStream inputStreamFromServer;		
	private Random random;
	
	@Override
	public void run() {
		
		try {
			clientSocket = new Socket("127.0.0.1", 8889);				
			outputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			inputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
			
			initReceiverFromServer();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void initReceiverFromServer() {

		new Thread(() -> {
			while (!clientSocket.isClosed()) {
				try {
					MessageGambler message = (MessageGambler) inputStreamFromServer.readObject();		
					receiveFromServer(MessageGambler);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	private void receiveFromServer(MessageGambler message) {
		switch(message.){
			case  :
				///closeConnection
				break;
			default:
				break;
 		}
	}
	
	public void initNewGambler(){
		
	}
 	
 	public void disconnectFromServer(){
		try {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
