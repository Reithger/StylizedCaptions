package main;

import core.SocketControl;

/**
 * 
 * This is a runner class for the StylizedCaptions project.
 * 
 * You will need to install the vosk Python package for this to work
 * 
 * Please run "pip3 install vosk" in your terminal, you may need to
 * install pip as well? The Python sub-program that does the voice to
 * text relies on vosk.
 * 
 * Font style in this version (will add options later) is the OSRS Runescape
 * font as found here: https://www.dafont.com/runescape-uf.font
 * 
 */

public class Caption {

	public static void main(String[] args) {
		Interpreter interp = new Interpreter(600, 125);
		
		SocketControl socket = new SocketControl();
		
		socket.createSocketInstance("text");
		
		socket.verifySubprogramReady("./captions", "voice-to-text.py", "../assets/voice-to-text.py", "/main/assets/voice-to-text.py");
		
		socket.setInstancePort("text", 3500);

		socket.attachJavaReceiver("text", interp);
		
		socket.setInstanceSubprogramPython("text", "./captions/voice-to-text.py");
		
		socket.runSocketInstance("text");		
	}

	
}


