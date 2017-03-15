package Gambler;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import Entities.*;


public class GamblerClient implements Runnable{
	private Socket clientSocket;
	private ObjectOutputStream outputStreamToServer;
	private ObjectInputStream inputStreamFromServer;		
	private Random random;
	private boolean connected = false;
	private MessageGambler message;
	
	private GamblerView gamblerView;
	
	@Override
	public void run() {
		
		message = new MessageGambler(GamblerCommand.GamblerConnect, true);

		try {
			clientSocket = new Socket("127.0.0.1", 8889);
			outputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			inputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
			connected = true;	
			
			//	Send connect message to the server.
			outputStreamToServer.writeObject(message);
			
			//	Listen for server commands.
			initReceiverFromServer();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initReceiverFromServer() {
		new Thread(() -> {			
			while (connected) {
				try {
					MessageGambler message = (MessageGambler) inputStreamFromServer.readObject();		
					processMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void processMessage(MessageGambler message) {
		switch(message.getCommand()){
			case  GamblerConnect:
				///				
				gamblerView = new GamblerView(outputStreamToServer);				
				gamblerView.getGamblerLoginPanel().showMessage("Connected!");
				break;
				
			case  GamblerDisconnect:
				///
				break;
			case  GamblerLogin:
				///
				break;
			case  GamblerBet:
				///
				break;
			default:
				break;
 		}
	}
 	


}
