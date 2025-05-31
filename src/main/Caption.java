package main;

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
 * Isn't working right now for some reason, will investigate
 * 
 */

public class Caption {

	public static void main(String[] args) {
		Interpreter interp = new Interpreter(600, 125);
		PythonFileValidation.verifyPythonFileNear("./captions", "voice-to-text.py", "../assets/voice-to-text.py", "/main/assets/voice-to-text.py");
		JavaPythonSocket a = new JavaPythonSocket("./captions/voice-to-text.py", 3500, interp);
	}

	
}


