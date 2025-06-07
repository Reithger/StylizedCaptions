package control;

import java.util.HashMap;

import core.JavaReceiver;


/**
 * 
 * Lightweight class that implements JavaSocketInterface's JavaReceiver to receive the
 * String data sent from the Vosk python captions software and process it before sending
 * it along to the TextReceiver implementing class to use as it sees fit.
 * 
 * This class can contain multiple TextReceiver references to send the received caption data
 * to multiple destinations.
 * 
 * @author Ada Reithger
 * 
 */

public class Interpreter implements JavaReceiver {

	private HashMap<String, TextReceiver> receivers;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Interpreter() {
		receivers = new HashMap<String, TextReceiver>();
		
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void addTextReceiver(String label, TextReceiver in) {
		receivers.put(label, in);
	}
	
	public void removeTextReceiver(String label) {
		receivers.remove(label);
	}
	
	@Override
	public void receiveSocketData(String arg0) {
		if(arg0.contains("partial") || arg0.contains("text")) {
			String use = cleanInput(arg0);
			System.out.println("Voice: " + use);
			for(TextReceiver tr : receivers.values()) {
				tr.handleText(use);
			}
		}
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private String cleanInput(String in) {
		String use = in.substring(in.indexOf("\"") + 1);
		use = use.substring(use.indexOf("\"") + 1);
		use = use.substring(use.indexOf("\"") + 1, use.length() - 1);
		return use;
	}

}