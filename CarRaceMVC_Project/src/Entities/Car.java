package Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import Server.CarEvents;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Car implements CarEvents {
	//	The model the car belongs to
	private double speed;
	private int wheelRadius;
	private String name, make, size, color, type;
	private Map<eventType, ArrayList<EventHandler<Event>>> carHashMap;
	
	public Car(String name, String make, String size, String color, String type) {
		this.name = name;
		this.make = make;
		this.size = size;
		this.color = color;
		this.type = type;
		this.speed = 1;
		this.wheelRadius = 5;
		carHashMap = new HashMap<eventType, ArrayList<EventHandler<Event>>>();
		for (eventType et : eventType.values())
			carHashMap.put(et, new ArrayList<EventHandler<Event>>());
	}

	public int getWheelRadius() {
		return wheelRadius;
	}

	public String getMake() {
		return make;
	}

	public String getColor() {
		return color;
	}

	public String getSize() {
		return size;
	}

	public String getType() {
		return type;
	}

	public double getSpeed() {
		return speed;
	}
	
	public String getName() {
		return name;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		processEvent(eventType.SPEED, new ActionEvent());
	}

	public synchronized void addEventHandler(EventHandler<Event> l, eventType et) {
		ArrayList<EventHandler<Event>> al;
		al = carHashMap.get(et);
		if (al == null)
			al = new ArrayList<EventHandler<Event>>();
		al.add(l);
		carHashMap.put(et, al);
	}

	public synchronized void removeEventHandler(EventHandler<Event> l, eventType et) {
		ArrayList<EventHandler<Event>> al;
		al = carHashMap.get(et);
		if (al != null && al.contains(l))
			al.remove(l);
		carHashMap.put(et, al);
	}

	private void processEvent(eventType et, Event e) {
		ArrayList<EventHandler<Event>> al;
		synchronized (this) {
			al = carHashMap.get(et);
			if (al == null)
				return;
		}
		for (int i = 0; i < al.size(); i++) {
			EventHandler<Event> handler = (EventHandler<Event>) al.get(i);
			handler.handle(e);
		}
	}
}