package Gambler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import Entities.Gambler;
import Entities.MessageGambler;

/**
 * Gambler client class, allows the gambler view to communicate with the corresponding handler at the server.
 * @author Vitaly Ossipenkov
 * @author Omer Yaari
 *
 */
public class GamblerClient implements Runnable {
	private Socket clientSocket;
	private ObjectOutputStream outputStreamToServer;
	private ObjectInputStream inputStreamFromServer;		
	private boolean connectedToServer = false;
	private GamblerView gamblerView;
	private Gambler currentGambler;
	
	@Override
	public void run() {
		try {
			clientSocket = new Socket("localhost", 8889);
			outputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			inputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
			connectedToServer = true;	
			gamblerView = new GamblerView(this);
			//	Listen for server commands.
			initReceiverFromServer();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Starts a new thread to listen for messages from the server (gambler handler).
	 * @exception IOException
	 * @exception SocketException
	 * @exception ClassNotFoundException
	 */
	private void initReceiverFromServer() {
		new Thread(() -> {
			try {
				while (connectedToServer) {
					MessageGambler message = (MessageGambler) inputStreamFromServer.readObject();
					processMessage(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
			finally {
				try {
					clientSocket.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * Closes all connections and attempts to shut down the gambler client upon receiving a Disconnect message from the server.
	 * @throws IOException
	 */
	private void shutdownClient() throws IOException {
		connectedToServer = false;
		outputStreamToServer.close();
		inputStreamFromServer.close();
		clientSocket.close();
	}
	
	/**
	 * Processes messages received from the server (gambler handler).
	 * @param message	The message received from the handler at the server.
	 * @throws IOException
	 */
	private void processMessage(MessageGambler message) throws IOException {
		switch (message.getCommand()) {
		case Register:
				gamblerView.registerSuccess(message.getStatus());
			break;
		
		case Login:
			//	If login was successful, create a new gamble and notify the gambler view.
			if (message.getStatus()) {
				currentGambler = new Gambler(message.getId(), message.getUsername(), message.getPassword(),
						message.getBalance());
				gamblerView.loginSuccess(currentGambler);
			} 
			//	Otherwise, just notify the view with null.
			else
				gamblerView.loginSuccess(null);
			break;

		case Bet:
				currentGambler.setBalance(message.getBalance()); 
				gamblerView.betPlaceSuccess(currentGambler.getBalance(), message.getStatus());
			break;

		case Disconnect:
			shutdownClient();
			break;
		
		case UpdateCars:
			//	TODO: update the cars the gambler can bet on.
			//	either add or remove car (use message.getCarName, and status true / false).
			break;
			
		case UpdateRaces:
			gamblerView.getGamblerMainPanel().updateRacesAndCars(message.getRaceNumber(), message.getCarName());
			break;
		default:
			//	TODO: show an error message like "invalid gambler command"
			break;
		}
	}
 	
	/**
	 * Sends a message to the server (handler).
	 * @param message	The message that will be sent to the handler at the server.
	 * @exception IOEXception
	 */
	public void SendGamblerMessage(MessageGambler message) {
		try {
			outputStreamToServer.writeObject(message);
		} catch (IOException e) {
			gamblerView.showMessageOnCurrentPanel("Connection error!", MessageColor.Red);
			e.printStackTrace();
		}
	}
	
	public Gambler getCurrentGambler(){
		return currentGambler != null ? currentGambler : null;
	}
	

}
