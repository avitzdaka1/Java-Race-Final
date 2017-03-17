package Race;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import Entities.*;

public class RaceController implements Runnable {

	private Socket clientSocket;
	private ObjectOutputStream outputStreamToServer;
	private ObjectInputStream inputStreamFromServer;		
	private Random random = new Random(System.currentTimeMillis());
	private boolean connectedToServer = false;
	private RaceView raceView;
	private String[] carNames;
	private int raceNumber;

	@Override
	public void run() {
		try {
		clientSocket = new Socket("localhost", 8888);
		outputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
		inputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
		connectedToServer = true;
		raceView = new RaceView(raceNumber);
		initReceiverFromServer();
		
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Selects cars for this race.
	 */
	private void selectCars(MessageRace message) throws IOException {
		raceNumber = message.getRaceNumber();
		carNames = new String[RaceCommand.TotalNumOfCars.ordinal()];
		for(int i = 0; i < carNames.length; i++) {
			int j = random.nextInt(message.getCarNames().length);
			carNames[i] = new String(message.getCarNames()[j]);
		}
		message.setCarNames(carNames);
		outputStreamToServer.writeObject(message);
	}

	/**
	 * Initialises the cars at the view after receiving their properties from the server (handler).
	 * @param message the race message.
	 */
	private void initCars(MessageRace message) {
		Car[] cars = new Car[RaceCommand.TotalNumOfCars.ordinal()];
		for(int i = 0; i < cars.length; i++) {
			cars[i] = new Car(message.getCarNames()[i], message.getCarMakes()[i], 
					message.getCarSizes()[i], message.getCarColors()[i], message.getCarTypes()[i]);
		}
		raceView.setCarsProps(cars);
	}
	
	private void initReceiverFromServer() {

		new Thread(() -> {
			while (connectedToServer) {
				try {
					MessageRace message = (MessageRace) inputStreamFromServer.readObject();
					processMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	private void processMessage(MessageRace message) throws IOException {
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
				selectCars(message);
				break;
			case CarSettings:
				initCars(message);
				break;
			default:
				
				break;
 		}
	}


}
