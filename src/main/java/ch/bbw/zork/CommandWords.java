package ch.bbw.zork;

/*
 * author:  Michael Kolling, Version: 1.0, Date: July 1999
 * refactoring: Rinaldo Lanza, September 2020
 */

import java.util.Arrays;
import java.util.List;

public class CommandWords {

	private List<String> validCommands = Arrays.asList("go", "quit", "help", "back", "look", "map", "take", "drop" );

	public boolean isCommand(String commandWord) {
		return validCommands.stream()
				.filter(c -> c.equals(commandWord)) //TODO For Each petlja, uproscena
				.count()>0;
	}

	public String showAll() {
		return String.join(" ", validCommands);
	}

}





