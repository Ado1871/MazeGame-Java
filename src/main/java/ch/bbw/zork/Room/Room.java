package ch.bbw.zork.Room;
/**
 * Author:  Michael Kolling, Version: 1.1, Date: August 2000
 * refactoring: Rinaldo Lanza, September 2020
 */

import ch.bbw.zork.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Room {
	
	private String description;
	private HashMap<String, Room> exits;
	private boolean isWinningRoom;
	private List<Item> items;

	public Room(String description) {
		this.description = description;
		this.exits = new HashMap<>();
		this.isWinningRoom = false;
		this.items = new ArrayList<>();
	}

	public void setExits(Room north, Room east, Room south, Room west) {
		exits.put("north", north);
		exits.put("east", east);
		exits.put("south", south);
		exits.put("west", west);
	}

	public void addItemToTheRoom(Item newItem) {
		items.add(newItem);
	}

	public List<Item> getItems() {
		return items;
	}

	public void setWinningRoom() {
		this.isWinningRoom = true;
	}

	public boolean isWinningRoom() {
		return isWinningRoom;
	}

	public String shortDescription() {
		return description;
	}

	public HashMap<String, Room> getExits() {
		return exits;
	}

	public String longDescription() {
		StringBuilder stringBuilder = new StringBuilder("You are in " + description + ".\n");
		stringBuilder.append(exitString());
		return stringBuilder.toString();
	}

	private String exitString() {
		return "Exits:" + String.join(" ", exits.keySet());
	}

	public Room nextRoom(String direction) {
		return exits.get(direction);
	}

}




