package Gambler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Entities.Gambler;
import Entities.GamblerCommand;
import Entities.MessageGambler;
import javafx.application.Application;
import javafx.stage.Stage;


public class GamblerClient implements Runnable {
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

		} catch (Exception e) {
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
			
		case  Register:
			///
			break;
			
			case  Login:
				if(message.getStatus()) {
					Gambler gambler = new Gambler(message.getId(), message.getUsername(), message.getPassword(),
							message.getBalance());
					gamblerView.loginSuccess(gambler);
				}
				else
					gamblerView.loginSuccess(null);				
				break;
				
			case  Bet:
				///
				break;
				
			case  Disconnect:
				///
				break;
				
			default:
				break;
 		}
	}
 	

	public void SendGamblerMessage(MessageGambler message){
		try {
			outputStreamToServer.writeObject(message);
		} catch (IOException e) {
			//	Update message on LoginPanel
			gamblerView.getGamblerLoginPanel().showMessage("Connection error!");
			e.printStackTrace();
		}
	}

}
