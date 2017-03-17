package Race;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import Entities.*;

public class RaceController {

	private Socket clientSocket;
	private ObjectOutputStream outputStreamToServer;
	private ObjectInputStream inputStreamFromServer;		
	private Random random;

	public RaceController() {
		//
	}

	public void connectToServer() {

		new Thread(() -> {
			try {
				clientSocket = new Socket("127.0.0.1", 8888);				
				outputStreamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
				inputStreamFromServer = new ObjectInputStream(clientSocket.getInputStream());
				
				initReceiverFromServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void initReceiverFromServer() {

		new Thread(() -> {
			while (!clientSocket.isClosed()) {
				try {
					MessageRace message = (MessageRace) inputStreamFromServer.readObject();

					receiveFromServer(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	private void receiveFromServer(MessageRace message) {
		switch(message){
			case closeConnection:
				///closeConnection
				break;
 		}
		//
		//controller do something
		//
		//
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
