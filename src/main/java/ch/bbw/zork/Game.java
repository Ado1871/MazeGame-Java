package ch.bbw.zork;

import ch.bbw.zork.Room.Room;
import ch.bbw.zork.Room.RoomWithALock;
import ch.bbw.zork.Room.RoomWithEnemies;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class Game - the main class of the "Zork" game.
 *
 * Author:  Michael Kolling, 1.1, March 2000
 * refactoring: Rinaldo Lanza, September 2020
 */

public class Game {

	private Parser parser;
	private Room currentRoom;
	private List<Room> allRooms;
	private Room startingRoom, lab, classRoom, basement, boilerRoom;
	private RoomWithALock goalRoom;
	private RoomWithEnemies atelier;
	private Item key, book, machete, slimeball;
	private Stack<Room> roomHistory;
	private ArrayList<Item> playerInventory;
	private double inventorySpaceCapacity;
	private double inventorySpaceAvailable;
	boolean finished;

	public Game() {

		roomHistory = new Stack<>();
		allRooms = new ArrayList<>();
		parser = new Parser(System.in);
		playerInventory = new ArrayList<>();
		inventorySpaceCapacity = 2.0;
		inventorySpaceAvailable = 0;

		key = new Item("Key", "Unlocks Something", 0.4);
		book = new Item("Book", "By Best Author", 2.0);
		machete = new Item("Machete", "Used for apples i guess.", 1.6);
		slimeball = new Item("Slimeball", "Uhh sticky.", 0.01);

		startingRoom = new Room("Starting Room.");
		lab = new Room("Lab");
		classRoom = new Room("Classroom");
		atelier = new RoomWithEnemies("Atelier");
		basement = new Room("Basement");
		boilerRoom = new Room("Boiler room");
		goalRoom = new RoomWithALock("Exit, maybe?");

		allRooms.add(startingRoom);
		allRooms.add(lab);
		allRooms.add(classRoom);
		allRooms.add(atelier);
		allRooms.add(basement);
		allRooms.add(goalRoom);
		allRooms.add(boilerRoom);

		startingRoom.setExits(null, null, null, lab);
		lab.setExits(null, null, classRoom, startingRoom);
		classRoom.setExits(lab, atelier, null, null);
		atelier.setExits(null, null, basement, classRoom);
		basement.setExits(atelier, boilerRoom, null, goalRoom);
		goalRoom.setExits(null, null, null, null);
		boilerRoom.setExits(null, null, null, basement);

		Enemy slimeCreature = new Enemy("Slime Creature",3.0, 1.5 );
		Enemy critter = new Enemy();

		atelier.addEnemy(slimeCreature);
		atelier.addEnemy(critter);

		boilerRoom.addItemToTheRoom(key);
		classRoom.addItemToTheRoom(book);
		lab.addItemToTheRoom(machete);
		basement.addItemToTheRoom(slimeball);


		currentRoom = startingRoom; // start game outside
		goalRoom.setWinningRoom();
	}


	/**
	 *  Main play routine.  Loops until end of play.
	 */
	public void play() {
		printWelcome();

		// Enter the main command loop.  Here we repeatedly read commands and
		// execute them until the game is over.
		finished = false;
		while (!finished) {
			Command command = parser.getCommand();
			finished = processCommand(command);
		}
		System.out.println("Thank you for playing.  Good bye.");
	}

	private void printWelcome() {
		System.out.println();
		System.out.println("Welcome to Zork!");
		System.out.println("Zork is a simple adventure game.");
		System.out.println("Type 'help' if you need help.");
		System.out.println();
		System.out.println(currentRoom.longDescription());
	}

	private boolean processCommand(Command command) {
		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}

		String commandWord = command.getCommandWord();
		if (commandWord.equals("help")) {
			printHelp();

		} else if (commandWord.equals("go")) {
			goRoom(command);

		} else if (commandWord.equals("quit")) {
			if (command.hasSecondWord()) {
				System.out.println("Quit what?");
			} else {
				return true; // signal that we want to quit
			}
		} else if (commandWord.equals("back")) {
			goBack();
		} else if (commandWord.equals("look")) {
			lookAround();
		} else if (commandWord.equals("map")) {
			createAsciiMap(allRooms, currentRoom);
		} else if (commandWord.equals("take") && command.hasSecondWord()) {
			takeItem(command.getSecondWord());
		} else if(commandWord.equals("drop") && command.hasSecondWord()) {
			putItemDown(command.getSecondWord());
		}

		if (finished) {
			System.out.println("Game Over!");
			return true;
		}

		return false;
	}

	private void takeItem(String itemToTake) {
		AtomicBoolean itemFound = new AtomicBoolean(false); // Todo: proguglaj sta je atomic

		List<Item> roomItems = currentRoom.getItems();
		if(!roomItems.isEmpty()){
			roomItems.forEach(item -> {
				if(item.getItemName().equals(itemToTake) && inventorySpaceAvailable + item.getItemWeight() <= inventorySpaceCapacity) {
					playerInventory.add(item);
					inventorySpaceAvailable+=item.getItemWeight(); // Todo: Solve the floating point problem!
					System.out.println("You picked up: " + item.getItemName());
					checkInventory();
					itemFound.set(true);
				}
			});
		} else {
			System.out.println("No items to pick up.");
		}

		if(!itemFound.get()) {
			System.out.println("No such item or no space.");
			checkInventory();
		} else {
			// Removes the item from the room that Player picked up.
			playerInventory.forEach(item -> {
				currentRoom.getItems().remove(item);
			});
		}
	}

	private void putItemDown(String itemToPutDown) {
		List<Item> playerInventoryTemp = playerInventory;
		AtomicBoolean itemFound = new AtomicBoolean(false);
		AtomicReference<Item> itemDropped = new AtomicReference<>(); // Todo - Ovo je kada je u pitanju neki objekat klase

		playerInventoryTemp.forEach(item -> {
			if(item.getItemName().equals(itemToPutDown)) {
				currentRoom.addItemToTheRoom(item);
				itemFound.set(true);
				itemDropped.set(item);
			}
		});

		if(!itemFound.get()) {
			System.out.println("No such item or no space.");
			checkInventory();
		} else {
			// Removes the item from the Player.
			playerInventory.remove(itemDropped.get());
			inventorySpaceAvailable-=itemDropped.get().getItemWeight();
			System.out.println("You put down " + itemDropped.get().getItemName() + " in " + currentRoom.shortDescription());
			checkInventory();
		}
	}

	public void checkInventory() {
		playerInventory.forEach(item -> {
			System.out.println("Items: ");
			System.out.println(item.getItemName() + " - " + item.getDescription() + "(" + item.getItemWeight() + "kg)");
		});
		System.out.println("Current inventory capacity: " + inventorySpaceAvailable + "/" + inventorySpaceCapacity);
		System.out.println();
	}

	public static void createAsciiMap(List<Room> rooms, Room currentRoom) {
		for (Room room : rooms) {
			System.out.print("+---");
		}
		System.out.println("+");

		for (Room room : rooms) {
			System.out.print("|");
			if (room == currentRoom) {
				System.out.print(" * ");
			} else {
				System.out.print("   ");
			}
		}
		System.out.println("|");

		for (Room room : rooms) {
			System.out.print("+---");
		}
		System.out.println("+");

		System.out.println("Legend:");
		System.out.println("* - Current Room");

		// You can add more details, such as room names and items, to the map as needed.
		for (Room room : rooms) {
			System.out.println(room.shortDescription());

			List<Item> items = room.getItems();
			if (!items.isEmpty()) {
				System.out.print("Items:");
				for (Item item : items) {
					System.out.print(" " + item.getItemName());
				}
				System.out.println();
			}

			System.out.print("Exits:");
			for (String exit : room.getExits().keySet()) {
				System.out.print(" " + exit);
			}
			System.out.println("\n");
		}
	}

	private void lookAround() {
		System.out.println("You are looking around the room. You see the following: ");
		List<Item> itemsInTheRoom = currentRoom.getItems();

		if(itemsInTheRoom.isEmpty()) {
			System.out.println("There are no items in the room.");
		} else {
			itemsInTheRoom.forEach(item -> {
				System.out.println(item.getItemName() + ": " + item.getDescription() + ", " + item.getItemWeight());
			});
		}
	}

	private void goBack() {
		if(!roomHistory.isEmpty()) {
			Room previousRoom = roomHistory.pop();
			currentRoom = previousRoom;
			System.out.println("You have gone back to the previous room.");
			System.out.println(currentRoom.longDescription());
		} else {
			System.out.println("You can't go back any further.");
		}
	}

	private void printHelp() {
		System.out.println("You are lost. You are alone. You wander");
		System.out.println("around at Monash Uni, Peninsula Campus.");
		System.out.println();
		System.out.println("Your command words are:");
		System.out.println(parser.showCommands());
	}

	private void printWin() {
		finished = true;
		System.out.println("Congrats! You have found the way out.");
	}

	private void goRoom(Command command) {
		if (!command.hasSecondWord()) {
			System.out.println("Go where?");
		} else {

			String direction = command.getSecondWord();

			// Try to leave current room.
			Room nextRoom = currentRoom.nextRoom(direction);

			if (nextRoom == null)
				System.out.println("There is no door!");
			else if (nextRoom instanceof RoomWithALock) {
				if(((RoomWithALock) nextRoom).isLocked()) { // PARSE (Parsovanje/Parsing)
					boolean hasKey = playerInventory.stream().anyMatch(item -> item.getItemName().equalsIgnoreCase("Key"));
					if(hasKey) {
						((RoomWithALock) nextRoom).unlockRoom();
						currentRoom = nextRoom;
						System.out.println("You unlocked the room.");
					} else {
						System.out.println("Room is locked. You need a key to open it.");
					}
				}
			} else {
				roomHistory.push(currentRoom); // Stavljamo sobu u History, na takozvani STACK
				currentRoom = nextRoom;
				System.out.println(currentRoom.longDescription());
				if (currentRoom instanceof RoomWithEnemies) { // Kao "==" samo za klase
					RoomWithEnemies roomWithEnemies = (RoomWithEnemies) currentRoom;
					List<Enemy> enemies = roomWithEnemies.getEnemies();

					// Check if the player has a machete in their inventory.
					boolean hasMachete = playerInventory.stream()
							.anyMatch(item -> item.getItemName().equalsIgnoreCase("Machete"));

					if (!hasMachete && !enemies.isEmpty()) {
						System.out.println("You entered a room with enemies, but you don't have a machete.");
						finished = true; // End the game
					} else if (hasMachete && !enemies.isEmpty()) {
						System.out.println("You entered a room with enemies, and you have a machete.");
						System.out.println("You use the machete to defeat the enemies!");
						// Remove the enemies from the room.
						enemies.clear();
					}
				}
			}
			// Check if player is in the winning room
			if(currentRoom.isWinningRoom()) {
				printWin();
			}
		}
	}
}
