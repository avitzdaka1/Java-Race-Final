package Race;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import Entities.*;

public class RaceController implements Runnable {

	private final int totalNumOfCars = 5;
	private Socket clientSocket;
	private ObjectOutputStream outputStreamToServer;
	private ObjectInputStream inputStreamFromServer;		
	private Random random;
	private boolean connected;
	private View raceView;
	private ArrayList<Car> cars;

	@Override
	public void run() {
		try {
		clientSocket = new Socket("localhost", 8888);
		outputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
		inputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
		connected = true;
		cars = new ArrayList<>();
		raceView = new View();
		initReceiverFromServer();
		
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Generates cars for this race.
	 */
	private void initCars() {
		
	}

	private void initReceiverFromServer() {

		new Thread(() -> {
			while (connected) {
				try {
					MessageRace message = (MessageRace) inputStreamFromServer.readObject();
					processMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	private void processMessage(MessageRace message) {
		switch(message.getCommand()){
			case Connect:

				break;
			case Disconnect:
				
				break;
			case Start:
				
				break;
			case End:
				
				break;
			case ChangeSpeed:
				
				break;
			case InitSettings:
				
				break;
			default:
				
				break;
 		}
	}

	
 	public void disconnectFromServer(){
		try {
			outputStreamToServer.writeObject(SERVER_COMMAND.closeConnection);
			outputStreamToServer.writeObject(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void changeColor(Car car){
		try {
			outputStreamToServer.writeObject(SERVER_COMMAND.colorChanged);
			outputStreamToServer.writeObject(Serializer.serialize(car.getLog())); // or Car.getLog()
		//	outputStreamToServer.writeObject(5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
