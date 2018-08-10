package digitalmedia;


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import java.util.Arrays;
import java.util.Random;

/**
     Opens an image window and adds a panel below the image
 */
public class DM_U3_S0561443 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = { "Original", "Rot-Kanal", "Negativ", "Graustufen",
			"Binärbild", "5 Graustufen", "10 Graustufen", "32 Graustufen",
			"Random Dithering", "Fehlerdiffusion", "Sepia", "6 Farben",
			"Blau-Rot-Verlauf","Floyd-Steinberg" };

	public static void main(String args[]) {

		ImageJ ij = new ImageJ();
		//TODO 
		IJ.open("/Users/Rebecca/Documents/Uni/GDM/Bear.jpg");//bear.jpg

		DM_U3_S0561443 pw = new DM_U3_S0561443();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	@Override
	public void run(String arg) {
		if (imp==null) 
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);
		storePixelValues(imp.getProcessor());
		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}
	class CustomCanvas extends ImageCanvas {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}
	} // CustomCanvas inner class

	class CustomWindow extends ImageWindow implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String method;
		
		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();
			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);
			add(panel);
			pack();
		}

		
		@Override
		public void itemStateChanged(ItemEvent evt) {
			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			} 
		}

		private void changePixelValues(ImageProcessor ip) {

			// Array zum Zurückschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			if (method.equals("Original")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						pixels[pos] = origPixels[pos];
					}
				}
			}
			
			if (method.equals("Rot-Kanal")) {
				redChanel(origPixels, pixels, width, height);
			}
			if (method.equals("Negativ")) {
				negativeImage(origPixels, pixels, width, height);
				//gdmvalidation.Ueb3Validation.validateNegativeImage(origPixels, pixels, width, height);
			}
			if (method.equals("Graustufen")) {
				greyValueImage(origPixels, pixels, width, height);
			}
			if (method.equals("Binärbild")) {
				binaryImage(origPixels, pixels, width, height);
			}
			if (method.equals("5 Graustufen")) {
				greyValueImage5Values(origPixels, pixels, width, height);
			}
			if (method.equals("10 Graustufen")) {
				greyValueImage10Values(origPixels, pixels, width, height);
			}
			if (method.equals("32 Graustufen")) {
				greyValueImage32Values(origPixels, pixels, width, height);
			}
			if (method.equals("Random Dithering")) {
				randomDithering(origPixels, pixels, width, height);
			}
			if (method.equals("Fehlerdiffusion")) {
				errorDiffusion(origPixels, pixels, width, height);
			}
			if (method.equals("Sepia")) {
				sepiaImage(origPixels, pixels, width, height);
			}
			if (method.equals("6 Farben")) {
				mapImageTo6Colors(origPixels, pixels, width, height);
			}
			if (method.equals("Blau-Rot-Verlauf")) {
				blueToRed( pixels, width, height);
			}
			if (method.equals("Floyd-Steinberg")) {
				floydSteinberg(origPixels, pixels, width, height);
			}
			
		}

		private void redChanel(int[] origPixels, int[] pixels, int width, int height) {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					//int g = (argb >>  8) & 0xff;
					//int b =  argb        & 0xff;
		
					int rn = r;
					int gn = 0;
					int bn = 0;
		
					// Hier muessen ggfs. die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
		
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void negativeImage(int[] origPixels, int[] pixels, int width, int height) 
		{
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO b)
					
					int rn = 255 - r;
					int gn = 255 - g;
					int bn = 255 - b;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void greyValueImage(int[] origPixels, int[] pixels, int width,
				int height) 
		{

			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO c)
					int q = (r+g+b)/3;
					int rn = q;
					int gn = q;
					int bn = q;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void binaryImage(int[] origPixels, int[] pixels, int width, int height) 
		{
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					int q = (r+g+b)/3;
					int rn;
					int gn;
					int bn;
					
					if(q > 90) {
						rn = 255;
						gn = 255;
						bn = 255;
					}
					else {
						rn = 0;
						gn = 0;
						bn = 0;
					}
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}	
		}

		private void greyValueImage5Values(int[] origPixels, int[] pixels, int width, int height) 
		{
			reduceGrey(origPixels, pixels, width, height, 5);		
		}

		private void reduceGrey(int[] origPixels, int[] pixels, int width, int height, int anzahl) {
			float v = 256/anzahl;
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO d2)!!!
					
					
					float q = (r+g+b)/3;
					int w = (int)((q/v)*(255.0/(anzahl - 1)));
					
					if(w > 255) w = 255;
					
					int rn = w;
					int gn = w;
					int bn = w;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void greyValueImage10Values(int[] origPixels, int[] pixels, int width, int height) 
		{
			reduceGrey(origPixels, pixels, width, height, 10);	
		}

		private void greyValueImage32Values(int[] origPixels2, int[] pixels,
				int width2, int height2) {
			reduceGrey(origPixels, pixels, width, height, 32);		
		}

		private void randomDithering(int[] origPixels, int[] pixels, int width,
				int height) {
			int error = 0;
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO e)
					int q = (r+g+b)/3;
					Random rand = new Random();
					error = rand.nextInt(150);
					
					int rn;
					int gn;
					int bn;
					
					if(255 - (q + error) > q) {
						rn = 0;
						gn = 0;
						bn = 0;
					}
					else {
						rn = 255;
						gn = 255;
						bn = 255;
					}
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
				error = 0;
			}		
		}

		private void errorDiffusion(int[] origPixels, int[] pixels, int width, int height) 
		{
			int error = 0;
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO f)
					int q = (r+g+b)/3;
					int rn;
					int gn;
					int bn;
					
					if(q + error >= 128) {
						error += q - 255;
						rn = 255;
						gn = 255;
						bn = 255;
					} else {
						error += q;
						rn = 0;
						gn = 0;
						bn = 0;
					}
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
				error = 0;
			}
		}

		private void sepiaImage(int[] origPixels, int[] pixels, int width, int height)
		{
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
		
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO g)
					int q = (r+g+b)/3;
					
					//Graustufenbild
					int rn = q;
					int gn = q;
					int bn = q;
					
					//Sepiabild
					rn = rn + 40;
					gn = gn + 20;
					bn = bn - 30;
					
					if(rn > 255) {
						rn = 255;
					}
					if(gn > 255) {
						gn = 255;
					}
					if(bn < 0) {
						bn = 0;
					}
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}		
		}

		private void mapImageTo6Colors(int[] origPixels, int[] pixels, int width, int height) 
		{
			
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO h)
					//2D Array der 6 Farben und ihrer Werte
					int colors[][] = new int[6][3];
										  //r:  //g: //b:
					colors[0] = new int[] {184, 177, 175}; 	//white
					colors[1] = new int[] {53, 104, 140};	//blue
					colors[2] = new int[] {82, 87, 88};		//grey
					colors[3] = new int[] {62, 56, 51};		//brown
					colors[4] = new int[] {25, 32, 33};		//darkgrey
					colors[5] = new int[] {11, 16, 16};		//black
					
					//Variable für aktuellen r-, g- oder b- Wert
					int current = 0;
					
					//Bild auf Graustufe bringen
					int q = (r+g+b)/3;
					int rn = q;
					int gn = q;
					int bn = q;
					
					//Array der aktuellen rgb-Werte
					int rgb[] = new int [] {r, g, b};
					
					//Array für Werte der mittleren quadratischen Abweichung
					int mse[] = new int[colors.length];
					
					int errors[][] = new int[6][3];
					
					//For-loop über einzelne Reihen
					for(int i = 0; i < colors.length; i++) {
						//For-loop über einzelne Spalten
						for(int j = 0; j < 3; j++) {
							//Aktueller rgb-Wert
							current = rgb[j];
							
							//Subtrahieren des rgb-Wertes vom Farbwert
							errors[i][j] = Math.abs(colors[i][j] - current);
						}
						//Die 3 neuen Werte jeder Reihe in neuer Variable
						int val1 = errors[i][0];
						int val2 = errors[i][1];
						int val3 = errors[i][2];
						
						//Mittlere quadratische Abweichung (quadrieren um negative Zahlen zu vermeiden)
						int error = ((val1*val1) + (val2*val2) + (val3*val3));
						mse[i] = error;
					}
					//Variable für kleinste mittlere quadratische Abweichung der 6 Farben
					int min = 0;
					//wird auf Wert des ersten Index gesetzt
					min = mse[0];
					
					//Index der Reihe mit der kleinsten mittleren qudratischen Abweichung
					//(mit -1 deklariert um unerkannte Fehler zu vermeiden)
					int index = -1;
					
					//Suche des kleinsten MQF
					for(int i = 0; i < mse.length; i++) {
						//Wenn neuer Wert kleiner als aktuell kleinster Wert, 
						//dann ersetzen und Index ändern
						if(mse[i] <= min) {
							min = mse[i];
							index = i;
						}
						rn = colors[index][0];
						gn = colors[index][1];
						bn = colors[index][2];
					}
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}

		private void blueToRed( int[] pixels, int width, int height) 
		{
			Random rand = new Random();
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					// TODO i)
					
					int rn = 255 * x/(width - 1);
					int gn = 0;
					int bn = 255 - rn;
					
					if(y > height/2){
						float ratio = (float) x/(width - 1);
						if (ratio > rand.nextFloat()){
							rn = 255;
							gn = 0;
							bn = 0;
						} else{
							rn = 0;
							gn = 0;
							bn = 255;
						}						
					}
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		
		//not required
		private void floydSteinberg(int[] origPixels, int[] pixels, int width, int height) 
		{	 
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					// TODO j)
					
					int rn = r;
					int gn = g;
					int bn = b;
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
	} // CustomWindow inner class
} 
