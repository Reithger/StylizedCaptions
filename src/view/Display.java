package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.ArrayList;

import control.Interpreter;
import control.TextReceiver;
import visual.composite.HandlePanel;
import visual.frame.WindowFrame;

/**
 * Class primarily serves to manage the graphical components of displaying the interpreted
 * lingual text using the Software Visual Interface library, as well as providing a means
 * to import novel font designs for use in the captions.
 * 
 *
 * TODO: Need to overhaul SVI DrawnText to allow for a series of strings and Fonts to be
 * provided such that we can have alternate font stylings mid-string (so the most recent
 * addition to the caption can be colored differently).

 * TODO: Native means of transparent background so we don't rely on chroma key options?
 * 
 * 
 */

public class Display implements TextReceiver{
	
//---  Constant Values   ----------------------------------------------------------------------

	private static final int DISPLAY_WORD_COUNT = 14;
	private static final int OVERWRITE_DELAY = 5;
	private static final Font DEFAULT_FONT = new Font("Sans Serif", Font.BOLD, 28);
	private static final Color DEFAULT_CHROMA = new Color(0, 255, 0);
	
//---  Instance Variables   -------------------------------------------------------------------

	private int width;
	private int height;
	
	private WindowFrame wf;
	private HandlePanel hp;
	
	private boolean resetOnNext;
	private int counter;
	private int prestige;
	
	private Color chroma;
	
	private volatile ArrayList<String> text;
	private volatile String lastText;
	
	private volatile Font usedFont;
		
//---  Constructors   -------------------------------------------------------------------------
	
	public Display(int wid, int hei) {
		initiate(wid, hei, DEFAULT_CHROMA);
	}
	
	public Display(int wid, int hei, Color chrom) {
		initiate(wid, hei, chrom);
	}
	
	private void initiate(int wid, int hei, Color chromaChoice) { 
		width = wid;
		height = hei;
		usedFont = DEFAULT_FONT;
		wf = new WindowFrame(width, height);
		wf.setName("Ada Captions");
		chroma = chromaChoice;
		wf.setBackgroundColor(chroma);
		hp = new HandlePanel(0, 0, width, height) {
			private int counter;
			@Override
			public void clickReleaseEvent(int code, int x, int y, int type) {
				usedFont = counter++ % 2 == 0 ? new Font("Sans Serif", Font.BOLD, 28) : new Font("RuneScape UF", Font.BOLD, 36);
			}
		};
		wf.addPanel("panel", hp);
		hp.addRectangle("rect", 3, "default", 0, 0, width, height, false, chroma, chroma);
		counter = -1;
		
		loadFont("../control/assets/runescape_uf.ttf", "/control/assets/runescape_uf.ttf");
	}

//---  Operations   ---------------------------------------------------------------------------

	public void terminateDisplay() {
		wf.disposeFrame();
	}
	
	@Override
	public void handleText(String in) {
		// The commented out code below was used to improve performance by stopping redundant interpretations
		if(lastText != null && lastText.equals(in) && !lastText.equals("")) {
			return;
		}
		lastText = in;
		// On first instance of text coming in, assign whatever we got to instantiate the text ArrayList
		if(text == null) {
			resetText(in);
		}
		// Otherwise we need to detect when a new sentence has started and appropriately append new text as it comes in
		else {
			String[] other = in.split(" ");
			// If the input we receive is empty, that marks the end of a sentence
			if(other[0].equals("")) {
				resetOnNext = true;
			}
			// If the last sentence ended and new text is arrived, we need to reset the text ArrayList
			if((!other[0].equals("") && resetOnNext) || text.size() == 0) {
				resetText(in);
				resetOnNext = false;
				prestige = 0;
			}
			// If we're mid-sentence, figure out what the new text is to append to the text ArrayList
			else if(!other[0].equals("")){
				resetText(in);
			}		
			// Go over the newest version of the input and replace the text ArrayList contents with any words that have been changed
			if(!other[0].equals("")) {
				for(int i = other.length - 1; i >= 0 && text.size() - 1 - i >= 0; i--) {
					text.set(text.size() - 1 - i, other[other.length - 1 - i]);
				}
			}
			
		}

		if(counter == -1) {
			StringBuilder sb = new StringBuilder();
			
			for(int i = prestige * DISPLAY_WORD_COUNT; i < text.size(); i++) {
				sb.append(text.get(i).substring(0, 1).toUpperCase() + text.get(i).substring(1) + " ");
			}
			String display = sb.toString(); //.toUpperCase(Locale.CANADA);
			hp.removeAllElements();
			hp.addRectangle("rect", 3, "default", 0, 0, width, height, false, chroma, chroma);
			//hp.handleText("text", "default", 5, width / 2, height / 2, width, height, usedFont, sb.toString());
			hp.addText("text", 5, "default", width / 2, height / 2, width, height, display, usedFont, false, false, true);
		}
		if(text.size() - (prestige * DISPLAY_WORD_COUNT) > DISPLAY_WORD_COUNT || counter != -1) {
			counter = counter == -1 ? 0 : counter + 1;
			if(counter >= OVERWRITE_DELAY) {
				counter = -1;
				prestige++;
			}
		}
	}
	
	private void loadFont(String localPath, String jarPath) {
		InputStream is = null;
		try {
			is = Interpreter.class.getResourceAsStream(localPath);
			if(is == null) {
				is = Interpreter.class.getResourceAsStream(jarPath);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try {
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, is));
			usedFont = new Font("RuneScape UF", Font.BOLD, 36);
			is.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			usedFont = DEFAULT_FONT;
		}
	}

//---  Support Methods   ----------------------------------------------------------------------
	
	private void resetText(String in) {
		text = new ArrayList<String>();
		for(String s : in.split(" ")) {
			if(!s.equals(""))
				text.add(s);
		}
	}

}
