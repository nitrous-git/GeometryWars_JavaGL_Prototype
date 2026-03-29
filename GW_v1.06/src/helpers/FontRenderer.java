package helpers;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public class FontRenderer {

	private Font awt_Font;
	private TrueTypeFont tt_font;
	
	public FontRenderer(String font_name, int style, int size) {
		awt_Font = new Font(font_name, Font.BOLD, 24);
	}

	public void setUpFont() {
		tt_font = new TrueTypeFont(awt_Font, true);	
	}
	
	public void drawString(int x, int y, String text, Color color) {
		glEnable(GL_BLEND);
		tt_font.drawString(x, y, text, color);
		
		
		//glEnable(GL_BLEND);
		//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		//tt_font.drawString(x, y, text, color);
		//glDisable(GL_BLEND);
	}
	
}
