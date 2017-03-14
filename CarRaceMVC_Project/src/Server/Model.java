package Server;
import javafx.scene.paint.Color;

public class Model {
	private CarLog log;
	private int raceCounter;
	private ServerCar c1;
	private ServerCar c2;
	private ServerCar c3;

	public Model(int raceCounter) {
		this.raceCounter = raceCounter;
		this.log = new CarLog(this.raceCounter);
		c1 = new ServerCar(0, raceCounter, log);
		c2 = new ServerCar(1, raceCounter, log);
		c3 = new ServerCar(2, raceCounter, log);
	}

	public void changeColor(int id, Color color) {
		getCarById(id).setColor(color);
	}

	public void changeRadius(int id, int radius) {
		getCarById(id).setRadius(radius);
	}

	public void changeSpeed(int id, double speed) {
		getCarById(id).setSpeed(speed);
	}

	public ServerCar getCarById(int id) {
		if (id == 0)
			return c1;
		else if (id == 1)
			return c2;
		else
			return c3;
	}

	public int getRaceCounter() {
		return raceCounter;
	}
}
