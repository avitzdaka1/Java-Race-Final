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
		try {
			switch (inputMessage.getCommand()) {

			case Disconnect:
				///
				break;
			case Register:
				registerGambler(inputMessage.getUsername(), inputMessage.getPassword());
				break;
			case Login:
				loginGambler(inputMessage.getUsername(), inputMessage.getPassword());
				break;
			case Logout:
				logoutGambler(inputMessage.getUsername(), inputMessage.getPassword());
				break;
			case Bet:
				processBet();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logoutGambler(String username, String password) {
			database.updateGamblerOnline(username, password, false);	
	}

	//	Logins a new gambler to the database.
	private void loginGambler(String username, String password) throws IOException {
		MessageGambler message = null;
		if (database.checkGamblerAuth(username, password)) {
			database.updateGamblerOnline(username, password, true);
			Gambler gambler = database.getGamblerDetails(username);
			message = new MessageGambler(GamblerCommand.Login, username, 
					password, gambler.getBalance(), gambler.getId(), true);

		}
		else {
			message = new MessageGambler(GamblerCommand.Login, false);
		}
		outputStream.writeObject(message);
	}
	
	//	Registers a new gambler to the database.
	private void registerGambler(String username, String password) throws IOException {
		MessageGambler message = null;
		if (!database.gamblerExists(username)) {
			Gambler gambler = new Gambler(database.getLastGamblerId(), username, password);
			database.insertNewGambler(gambler);
			message = new MessageGambler(GamblerCommand.Register, username,
					password, gambler.getBalance(), gambler.getId(), true);
		}
		else {
			message = new MessageGambler(GamblerCommand.Register, false);
		}
		outputStream.writeObject(message);
	}
	
	private void processBet() {
		
	}
	
	@Override
	public void serverDisconnection() {
		// TODO Auto-generated method stub
		
	}
}