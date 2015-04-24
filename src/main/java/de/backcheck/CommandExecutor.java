package de.backcheck;

public class CommandExecutor {

	private final String commandOrNull;
	
	public CommandExecutor(String commandOrNull) {
		super();
		this.commandOrNull = commandOrNull;
	}

	public void execute() {
		if( commandOrNull == null ) return;
		try {
			Runtime.getRuntime().exec(commandOrNull);
		} catch (Exception e) {
			System.err.println("cannot execute \""+commandOrNull+"\": "+e.getMessage());
		}
	}

}
