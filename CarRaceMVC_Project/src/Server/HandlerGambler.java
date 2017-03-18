package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import Entities.*;

class HandlerGambler implements Runnable, MainServerListener {

	private Socket clientSocket;
	private boolean gamblerConnected;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private CarRaceServer mainServer;
	private Database database;
	
	public HandlerGambler(Socket clientSocket, CarRaceServer mainServer,  Database database){
		this.clientSocket = clientSocket;
		this.mainServer = mainServer;
		this.database = database;
	}
	
	@Override
	public void run() {
		try {
			inputStream = new ObjectInputStream(clientSocket.getInputStream());
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

			while (true)
			//	MessageGambler inputMessage = (MessageGambler) inputStream.readObject();
				handleMessage((MessageGambler) inputStream.readObject());
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

	private void handleMessage(MessageGambler inputMessage) throws IOException {
		
			switch (inputMessage.getCommand()) {

			case Disconnect:
				///
				break;
			case Register:
				// TODO: update log on server!
				registerGambler(inputMessage.getUsername(), inputMessage.getPassword());
				break;
			case Login:
				// TODO: update log on server!
				loginGambler(inputMessage.getUsername(), inputMessage.getPassword());
				break;
			case Logout:
				// TODO: update log on server!
				logoutGambler(inputMessage.getUsername(), inputMessage.getPassword());
				break;
			case Bet:
				// TODO: update log on server!
				processBet(inputMessage.getId(),inputMessage.getRaceNumber()[0], 
						inputMessage.getCarName()[0], inputMessage.getBet());
				break;
			case getRaces:
				getCurrentRaces(); ///////////////////////////////////
				break;
			default:
				break;
			}
	}

	private void getCurrentRaces() throws IOException {
		
		ArrayList<Integer> racesList = database.getHoldingRaces();
		int[] races = new int[racesList.size()];
		for (int i = 0; i < racesList.size(); i++) 
			races[i] = racesList.get(i);
		String[] cars = new String[15];
		/*ArrayList<String> carsList = new ArrayList<>();
		for (int i = 0; i < races.length; i++) 
			carsList.addAll(database.getCarsInRace(races[i]));	*/	
		ArrayList<String> carsList = database.getCarsInRace();
		for (int i = 0; i < carsList.size(); i++)
			cars[i] = carsList.get(i);
		////////////////////////////////////////////////////////////////////////////////////////
		outputStream.writeObject(new MessageGambler(GamblerCommand.getRaces,0, races, cars, 0));	
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
			Gambler gambler = new Gambler(database.getLastGamblerId() + 1, username, password);
			database.insertNewGambler(gambler);
			message = new MessageGambler(GamblerCommand.Register, username,
					password, gambler.getBalance(), gambler.getId(), true);
		}
		else {
			message = new MessageGambler(GamblerCommand.Register, false);
		}
		outputStream.writeObject(message);
	}
	
	private void processBet(int gamblerId, int raceNumber, String carName, int bet) throws IOException {
		MessageGambler message = null;
		
		if (database.gamblerBet(gamblerId, raceNumber, carName, bet)){
		//if (database.placeGamblerBet(gamblerId, raceNumber, carName, bet)){
			int races[] = new int[] { raceNumber };
			String cars[] = new String[] {carName};
			message = new MessageGambler(GamblerCommand.Bet, gamblerId, races, cars,
					bet);
			message.setBalance(database.getGamblerDetails(gamblerId).getBalance());
			message.setStatus(true);
		}
		else
			message = new MessageGambler(GamblerCommand.Bet, false);
		outputStream.writeObject(message);
	}
	
	@Override
	public void serverDisconnection() {
		// TODO Auto-generated method stub
		
	}
}