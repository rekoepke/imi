package digitalmedia;

import java.util.Random;

import gdmvalidation.Ueb1Validation;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)
// TODO refactor the class name
public class DM_U1_A2 implements PlugIn {

	final static String[] choices = { "Schwarzes Bild", "Gelbes Bild", "Schwarz/Weiss Verlauf",
			"Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf", "Italienische Fahne", "Kegelschnitt", "Japanische Fahne",
			"Japanische Fahne mit weichen Kanten", "Streifenmuster", "Streifenmuster vertikal&horizontal" };

	private String choice;

	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
		ij.exitWhenQuitting(true);

		DM_U1_A2 imageGeneration = new DM_U1_A2();
		imageGeneration.run("");
	}

	public void run(String arg) {

		int width = 566; // Breite
		int height = 400; // Hoehe

		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("DM_U1_A2", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();

		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[]) ip.getPixels();

		dialog();

		// //////////////////////////////////////////////////////////////
		if (choice.equals("Schwarzes Bild")) {
			int r, g, b;
			r = g = b = 0;
			setImageToColor(pixels, width, height, r, g, b);
		} else if (choice.equals("Gelbes Bild")) {
			int r, g, b;
			r = g = 255;
			b = 0;
			// TODO set the color to yellow

			setImageToColor(pixels, width, height, r, g, b);
		} else if (choice.equals("Schwarz/Weiss Verlauf")) {
			blackToWhite(pixels, width, height);
			Ueb1Validation.validateBlackToWhite(pixels, width, height);
		} else if (choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf")) {
			black2RedAndBlack2Blue(pixels, width, height);
			Ueb1Validation.validateBlack2RedAndBlack2Blue(pixels, width, height);
		} else if (choice.equals("Italienische Fahne")) {
			flagItalian(pixels, width, height);
			Ueb1Validation.validateFlagItalian(pixels, width, height);
		} else if (choice.equals("Kegelschnitt")) {
			conicSection(pixels, width, height);
		} else if (choice.equals("Japanische Fahne")) {
			flagOfJapan(pixels, width, height);
		} else if (choice.equals("Japanische Fahne mit weichen Kanten")) {
			flagOfJapanSmooth(pixels, width, height);
		} else if (choice.equals("Streifenmuster")) {
			stripes(pixels, width, height);
		} else if (choice.equals("Streifenmuster vertikal&horizontal")) {
			stripesVertiHoriz(pixels, width, height);
		}

		// //////////////////////////////////////////////////////////////////

		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void blackToWhite(int[] pixels, int width, int height) {
		// TODO set some values here

				// Schleife ueber die y-Werte
				for (int y = 0; y < height; y++) {
					// Schleife ueber die x-Werte
					for (int x = 0; x < width; x++) {
						int pos = y * width + x; // Arrayposition bestimmen
						int r, g, b;
						// TODO code for the image.
						r = g = b = 255 * x / (width - 1);
						
						pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
					}
				}
			/*
			 * Gelb-Blau-Verlauf
			 
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					// TODO i)
					
					
					int bn = 255 * x/(width - 1);
					int rn = 255 - bn;
					int gn = 255 - bn;
					
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		
		*/
		
	}

	private void black2RedAndBlack2Blue(int[] pixels, int width, int height) {
		// TODO set some values here

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the image.
				r = 255 * x / (width - 1);
				g = 0;
				b = 255 * y / (height - 1);
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void flagItalian(int[] pixels, int width, int height) {
		// TODO set some values here
		int segments = width / 3;

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				r = g = b = 255;
				// TODO code for the flag.

				if (x < segments) {
					r = b = 0;
				}
				
				else if (x >= segments*2) {
					g = b = 0;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void conicSection(int[] pixels, int width, int height) {
		// TODO set some values here

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the flag.
				r = g = b = 0;
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void flagOfJapan(int[] pixels, int width, int height) {
		// TODO set some values here
		int mX = width / 2;
		int mY = height / 2;

		int rC = (height * 3 / 10) * (height * 3 / 10);

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				// TODO code for the flag.
				int dX = (x - mX);
				int dY = (y - mY);

				double rP = ((dX * dX) + (dY * dY));

				if (rP < rC) {
					r = 255;
					g = b = 0;
				} else {
					r = g = b = 255;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void flagOfJapanSmooth(int[] pixels, int width, int height) {
		// TODO set some values here
		int mX = width / 2;
		int mY = height / 2;

		int rC = (height * 3 / 10) * (height * 3 / 10);
		int rS = rC+50;

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				r = g = b = 255;
				// TODO code for the flag.
				int dX = (x - mX);
				int dY = (y - mY);

				double rP = Math.sqrt((dX * dX) + (dY * dY));
				
				if (rP < rC) {
					r = 255;
					g = b = 0;
				} else if (rP < rS) {
					r = 255;
					g = b = (int) (255*(rP-rC)/(rS-rC));
				} 
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void stripes(int[] pixels, int width, int height) {
		// TODO set some values here
		double d = width / 12.0;

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				r = g = b = 255;
				// TODO code for the flag.

				int stripe = (int) (x/d);
				
				if(stripe % 2 == 0) {
					r = g = b = 0;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void stripesVertiHoriz(int[] pixels, int width, int height) {
		// TODO set some values here
		double dV = width / 12.0;
		double dH = height / 12.0;

		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
				r = g = b = 255;
				// TODO code for the flag.

				int stripeV = (int) (x/dV);
				int stripeH = (int) (y/dH);
				
				if(stripeV % 2 == 0) {
					r = g = b = 0; 
				}
				if(stripeV % 2 == 0 && stripeH % 2 == 0) {
					r = 255;
					g = b = 0;
				}

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void setImageToColor(int[] pixels, int width, int height, int r, int g, int b) {
		int color = 0xFF000000 | (r << 16) | (g << 8) | b;
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				// Werte zurueckschreiben
				pixels[pos] = color;
			}
		}
	}

	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");
		gd.addChoice("Bildtyp", choices, choices[0]);
		gd.showDialog(); // generiere Eingabefenster
		choice = gd.getNextChoice(); // Auswahl uebernehmen

		if (gd.wasCanceled())
			System.exit(0);
	}
}
