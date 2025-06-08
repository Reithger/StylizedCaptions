package control;

import java.util.Arrays;

import core.SocketControl;

import view.Display;
import core.JavaTeardown;

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
 * 
 * TODO: Allow user to select Font customization (list of fonts to pick from, size, color, etc.)
 * 
 */

public class Caption implements JavaTeardown{
	
	private ExportHandler export;
	private Interpreter interp;
	private Display display;
	private SocketControl socket;

	public Caption(String[] args) {
		boolean headless = args.length > 1;
		
		interp = new Interpreter(headless);
		socket = new SocketControl();
		display = new Display(600, 125);

		setupInterpreter();
		
		interp.addTextReceiver("captions", display);
		
		System.out.println(args.length + " " + Arrays.toString(args));
		
		if(headless) {
			setupExportText(args[0], args[1]);
		}
	}
	
	public void teardownProgram() {
		display.terminateDisplay();
		socket.endSocketInstance("text");
		socket.endSocketInstance("send");
	}
	
	private void setupExportText(String argOne, String argTwo) {
		try {
			int lPort = Integer.parseInt(argOne);
			int sPort = Integer.parseInt(argTwo);
			System.out.println("Port established at: " + lPort + " and: " + sPort);
			
			
			socket.createSocketInstance("send");
			
			socket.setInstanceListenPort("send", sPort);
			socket.setInstanceSendPort("send", lPort);
			
			export = new ExportHandler(socket, "send");
			interp.addTextReceiver("export", export);
			
			socket.attachJavaSender("send", export);
			socket.attachJavaReceiver("send", export);
			
			socket.setInstanceKeepAlive("send", 2000);
			socket.setInstanceTimeout("send", 10000);
			
			socket.attachJavaTeardown("send", this);
			
			socket.setInstanceQuiet("send", true);
			
			socket.runSocketInstance("send");
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Argument was provided to the running of StylizedCaptions but it could not be interpreted as a port number for setting up caption data export");
		}
	}

	private void setupInterpreter() {
		socket.createSocketInstance("text");
		
		socket.verifySubprogramReady("./captions", "voice-to-text.py", "../assets/voice-to-text.py", "/control/assets/voice-to-text.py");
		
		socket.setInstanceListenPort("text", 3500);

		socket.attachJavaReceiver("text", interp);
		
		socket.setInstanceSubprogramPython("text", "./captions/voice-to-text.py");

		socket.runSocketInstance("text");	
	}

	
}


