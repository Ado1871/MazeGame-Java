# Zork2-Java

## Description
This is a simple Maze adventure game written in Java. The game features a series of rooms the player can navigate through. The objective is to reach a specific room to win the game. Along the way, you can pick up, carry, and drop items based on weight restrictions. The game also has a back command to go to the previous room and a map command to display the layout of rooms and where the current position of the player is.

## Documentation for my Teacher :)

### 1.3  Hauptklasse für Zork

#### *Welche Klasse muss in der main Methode instanziert werden, und welche Methode dieser Klasse muss aufgerufen werden, damit das Programm startet?*

Die Klasse **Game** muss in der main Methode instanziert werden und mit der methode play() startet man das Spiel.

```
public class Zork2 {

	public static void main(String[] args) {
		Game zorkgame = new Game();
		zorkgame.play();
	}

}
```

### 2.1 Eigene Räumlichkeiten definieren.

Der Spieler Startet Oben rechts im Starting Room, bewegt sich nach Osten "east" und landet im Lab (Dort ist eine Machete aufzufinden, welche später vielleicht nützlich sein könnte), im süden "south" liegt Classroom, welches sehr wichtig ist, da sich der Schlüssel zum Sieg dort befindet (Und ein nutzloses Buch). Weiter im Osten "east" ist das Atelier (Wo sich vielleicht Monster "Slimes" befinden), südlich des Ateliers erreicht man den Basement (Dort ist ein Slimeball). Im Osten ist dann das Ziel, dass einen Schlüssel erfordert, falls wir den Schlüssel haben, können wir ans Ziel kommen, ansosten müssen wir deb Schlüssel aufsuchen.

### 2.2 Gewonnen

Um zu gewinnen muss man den Raum "goalRoom" betreten, für welchen man einen Schlüssel braucht.

In der Klasse Room:

Variable "isWinningRoom" mit den Datentyp boolean

```
private boolean isWinningRoom;
```

Getter und Setter Methoden (bei bool schreibt man "is" und nicht "get")

```
public void setWinningRoom() {
 this.isWinningRoom = true;
}

public boolean isWinningRoom() {
 return isWinningRoom;
}
```

In der Klasse Game:

Im Konstruktor machen wir den Raum "goalRoom" zum winning room

```
goalRoom.setWinningRoom();
```

Jedes mal wenn man den Befehl "go" ausführt wird geprüft ob man die Richtung angegeben hat, falls nicht wird "Go where?" ausgegeben, wenn sie doch richtig ist überprüft er ob ein Raum dort vorhanden ist, falls nicht wird "There is no door!" ausgegeben, wenn ja wird der Raum in dem wir sind im Stack getan und der neue raum wird zum "currentRoom" also dort wo wir nun sind, auch wird vom Raum den wir betreten haben die Beschreibung ausgegeben. Zuletzt wird geprüft ob "currentRoom", also der Raum in dem wird nun sind der winning room ist, fall ja wird die Methode "printWin()" ausgedruckt und wenn nicht, dann passiert auch nichts.

```
private void printWin() {
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
		else {
			roomHistory.push(currentRoom); // Stavljamo sobu u History, na takozvani STACK
			currentRoom = nextRoom;
			System.out.println(currentRoom.longDescription());

			if(currentRoom.isWinningRoom()) {
				printWin();
				}
			}
		}
	}

```

```
private Stack<Room> roomHistory;
```

In Dieser Methode wird geprüft ob "roomHistory" nicht leer ist, falls sie nicht leer ist wird vom stack der als letztes eingefügte Raum zum "previousRoom" und dann wird unser Raum "currentRoom" zu dem welchen wir vor Ihm betreten haben (text wird ausgegeben + beschreibung des Raumes in welchem wir nun sind wird ausgegeben). Falls wir stack leer ist (Also wir bis zum letzten raum gelangt sind im stack, oder keinen Raum betreten haben), wird text ausgegeben ("You can't...")

```
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
```

In 2.2 Sieht man die ganze Methode, hier aber das wichtigste nochmals kurz erklärt. Wenn wir einen neuen Raum betreten wird mit der methode **push()** der Raum dem wir sind im Stack getan und erst dann wird unser Raum zum neuen.

```
roomHistory.push(currentRoom); // Stavljamo sobu u History, na takozvani STACK
currentRoom = nextRoom;
```

### 2.4 Gegenstand im Raum & 2.5 Mehrere Gegenstände & 2.6

Variable in der Klasse Room für die items

```
private List<Item> items;
```

In der Klasse Item definiert der Konstruktor den Gegenstand mit 3 Attributen, *Name*, *beschreibung* und *Gewicht*

```
public class Item {

    private String itemName;
    private String description;
    private double itemWeight;

    public Item(String name, String desc, double weight) {
        itemName = name;
        description = desc;
        itemWeight = weight;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public double getItemWeight() {
        return itemWeight;
    }
}
```

Die Items werden in der Klasse Game instanziert

```
key = new Item("Key", "Unlocks Something", 0.4);
book = new Item("Book", "By Best Author", 2.0);
machete = new Item("Machete", "Used for apples i guess.", 1.6);
slimeball = new Item("Slimeball", "Uhh sticky.", 0.01);
```

Die Methode **processCommand(Command command)** in der Klasse Game verarbeitet den gegebenen Befehl und führt dementsprechen die Aktion aus (gibt es den Befehlt oder nicht), in unserem fall wäre es z.B. "take slimeball"

Diese Methode **processCommand(Command command)** überprüft das gewicht des slimeballs ob wir über verfügbaren inventarplatz haben und dann fügt es den slimeball hinzu oder nicht.

Diese Methode **putItemDown(String itemToPutDown)** behandelt das ablegen der Items


```
private void putItemDown(String itemToPutDown) {
    List<Item> playerInventoryTemp = playerInventory;
    AtomicBoolean itemFound = new AtomicBoolean(false);
    AtomicReference<Item> itemDropped = new AtomicReference<>();

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
        playerInventory.remove(itemDropped.get());
        inventorySpaceAvailable -= itemDropped.get().getItemWeight();
        System.out.println("You put down " + itemDropped.get().getItemName() + " in " + currentRoom.shortDescription());
        checkInventory();
    }
}
```

1. `List<Item> playerInventoryTemp = playerInventory;`: Eine temporäre Liste des Spielerinventars wird erstellt.
2. `AtomicBoolean itemFound = new AtomicBoolean(false);`: Ein `AtomicBoolean` wird initialisiert, um zu prüfen, ob ein Item gefunden wurde.
3. `AtomicReference<Item> itemDropped = new AtomicReference<>();`: Ein `AtomicReference` wird verwendet, um das abgelegte Item zu speichern.
4. `playerInventoryTemp.forEach()`: Durchläuft alle Items im Inventar des Spielers, um das gesuchte Item zu finden.
5. `currentRoom.addItemToTheRoom(item);`: Fügt das Item dem aktuellen Raum hinzu, wenn es gefunden wird.
6. `playerInventory.remove(itemDropped.get());`: Entfernt das Item aus dem Spielerinventar.
7. `inventorySpaceAvailable -= itemDropped.get().getItemWeight();`: Aktualisiert den verfügbaren Inventarraum des Spielers.

###2.7 Befehl "map"

Der `map` Befehl wird durch die Methode `createAsciiMap` implementiert. Diese Methode erzeugt eine ASCII-basierte Darstellung der Spielwelt. Sie nimmt die Liste aller `Room`objekte und das `currentRoom` Objekt. Innerhalb der Methode werden mehrere Schlaufen verwendet um es darzustellen. Sie zeigt die aktuelle position des Spielers und man sieht auch die anderen Räume

```
public static void createAsciiMap(List<Room> rooms, Room currentRoom) {
    // ... (Code für die Erzeugung der ASCII-Karte)
}
```

Dieser Array speichert alle `room` Objekte 

```
private List<Room> allRooms;
```

## Features

- Text-based navigation between rooms
- A "back" command to return to the previous room
- Item interaction: pick up, drop, and inventory management
- Weight-based inventory system
- Map display to see the layout of rooms and player location
- Win condition upon reaching a specific room which has to be unlocked with a key

## Requirements

- Java SE Development Kit (JDK)
- Any IDE that supports Java, or a text editor along with a command-line terminal

## Setup

1. Clone the repository to your local machine.
2. Open the project using your IDE or text editor.
3. Compile the Java files.
4. Run the `Zork2` class to start the game.

If you're using a terminal, you can compile and run the code as follows:

```
javac Zork2.java
java Zork2
```

## How to Play

1. After running the game, you will see a text prompt (`>`).
2. You can type various commands and press enter.

### Commands

- "go [direction]": Moves you to a room in the specified direction.
- "quit": Exits the game.
- "help": Shows list of all commands.
- "look": Describes your current surroundings.
- "back": Takes you back to the last room you were in.
- "take [item]": Picks up an item if present in the room and you have enough carrying capacity.
- "drop [item]": Drops an item from your inventory.
- "inventory": Lists the items you're carrying and their total weight.
- "map": Displays the layout of the rooms and player location.

## License

This project is open-source. Feel free to modify and distribute as you see fit.
