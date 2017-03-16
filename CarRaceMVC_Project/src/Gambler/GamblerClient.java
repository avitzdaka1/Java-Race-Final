package Gambler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Entities.Gambler;
import Entities.GamblerCommand;
import Entities.MessageGambler;


public class GamblerClient implements Runnable{
	private Socket clientSocket;
	private ObjectOutputStream outputStreamToServer;
	private ObjectInputStream inputStreamFromServer;		
	private boolean connected = false;
	private GamblerView gamblerView;
	
	@Override
	public void run() {
		
	//	message = new MessageGambler(GamblerCommand.GamblerConnect, true);

		try {
			clientSocket = new Socket("127.0.0.1", 8889);
			outputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			inputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
			connected = true;	
			gamblerView = new GamblerView(this);		
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
					
			case  GamblerLogin:
				if(message.getStatus()){
					Gambler gambler = new Gambler(message.getId(), message.getUsername(), message.getPassword());
					gamblerView.loginSuccessful(gambler);
				}
				else
					gamblerView.loginUnsuccessful();				
				break;
				
			case  GamblerBet:
				///
				break;
				
			case  GamblerDisconnect:
				///
				break;
				
			default:
				break;
 		}
	}
 	

	public void loginGambler(MessageGambler message){
		try {
			outputStreamToServer.writeObject(message);
		} catch (IOException e) {
			//	Update message on LoginPanel
			gamblerView.getGamblerLoginPanel().showMessage("Connection error!");
			e.printStackTrace();
		}
	}

}
