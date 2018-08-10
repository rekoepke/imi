package digitalmedia;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class DM_U4 implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = {"Wischen", "Weiche Blende", "Overlay (A, B)", "Overlay (B, A)", 
			"Schieb-Blende", "Chroma Key", "Extra"};

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB+STACK_REQUIRED;
	}
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		IJ.open("/Users/Rebecca/Documents/Uni/GDM/StackB.zip");
		
		DM_U4 sd = new DM_U4();
		sd.imp = IJ.getImage();
		ImageProcessor B_ip = sd.imp.getProcessor();
		sd.run(B_ip);
	}

	public void run(ImageProcessor B_ip) {
		// Film B wird uebergeben
		ImageStack stack_B = imp.getStack();
		
		int length = stack_B.getSize();
		int width  = B_ip.getWidth();
		int height = B_ip.getHeight();
		
		// ermoeglicht das Laden eines Bildes / Films
		Opener o = new Opener();
		OpenDialog od_A = new OpenDialog("Auswählen des 2. Filmes ...",  "");
				
		// Film A wird dazugeladen
		String dateiA = od_A.getFileName();
		if (dateiA == null) return; // Abbruch
		String pfadA = od_A.getDirectory();
		ImagePlus A = o.openImage(pfadA,dateiA);
		if (A == null) return; // Abbruch

		ImageProcessor A_ip = A.getProcessor();
		ImageStack stack_A  = A.getStack();

		if (A_ip.getWidth() != width || A_ip.getHeight() != height)
		{
			IJ.showMessage("Fehler", "Bildgrößen passen nicht zusammen");
			return;
		}
		
		// Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
		length = Math.min(length,stack_A.getSize());

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
		ImageStack stack_Erg  = Erg.getStack();

		// Dialog fuer Auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("Überlagerung");
		gd.addChoice("Methode",choices,"");
		gd.showDialog();

		int methode = 0;		
		String s = gd.getNextChoice();
		if (s.equals("Wischen")) methode = 1;
		if (s.equals("Weiche Blende")) methode = 2;
		if (s.equals("Overlay (A, B)")) methode = 3;
		if (s.equals("Overlay (B, A)")) methode = 4;
		if (s.equals("Schieb-Blende")) methode = 5;
		if (s.equals("Chroma Key")) methode = 6;
		if (s.equals("Extra")) methode = 7;

		// Arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_A;
		int[] pixels_Erg;

		// Schleife ueber alle Bilder
		for (int z=1; z<=length; z++)
		{
			pixels_B   = (int[]) stack_B.getPixels(z);
			pixels_A   = (int[]) stack_A.getPixels(z);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			int pos = 0;
			for (int y=0; y<height; y++)
				for (int x=0; x<width; x++, pos++)
				{
					int cA = pixels_A[pos];
					int rA = (cA & 0xff0000) >> 16;
					int gA = (cA & 0x00ff00) >> 8;
					int bA = (cA & 0x0000ff);

					int cB = pixels_B[pos];
					int rB = (cB & 0xff0000) >> 16;
					int gB = (cB & 0x00ff00) >> 8;
					int bB = (cB & 0x0000ff);

					if (methode == 1) {
						/*if (x+1 > (z-1)*(double)width/(length-1)) {
							pixels_Erg[pos] = pixels_B[pos];
						}*/
						if(y+1 > (z-1)*(double)height/(length-1)) {
							pixels_Erg[pos] = pixels_B[pos];
						}
						else {
							pixels_Erg[pos] = pixels_A[pos];
						}
					}
					if (methode == 2) {
						double f = (double)(z-1)/(length-1);
						int r = (int)((rA*f)+(rB*(1-f)));
						int g = (int)((gA*f)+(gB*(1-f)));
						int b = (int)((bA*f)+(bB*(1-f)));

						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					if(methode == 3) {
						//code methode 3
						int r = rB <= 128 ? rA*rB/128 : 255-((255-rA)*(255-rB)/128);
						int g = gB <= 128 ? gA*gB/128 : 255-((255-gA)*(255-gB)/128);
						int b = bB <= 128 ? bA*bB/128 : 255-((255-bA)*(255-bB)/128);
						
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					if(methode == 4) {
						//code methode 4
						int r = rA <= 128 ? rB*rA/128 : 255-((255-rB)*(255-rA)/128);
						int g = gA <= 128 ? gB*gA/128 : 255-((255-gB)*(255-gA)/128);
						int b = bA <= 128 ? bB*bA/128 : 255-((255-bB)*(255-bA)/128);
						
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					
					if(methode == 5) {
						//code methode 5
						int d = width * (z-1)/(length - 1);
						
						if(x < d) {
							pixels_Erg[pos] = pixels_A[width - (d - x) + width * y];
						}else{
							pixels_Erg[pos] = pixels_B[(x - d) + width * y];
						}
					}
					if(methode == 6){
						double rD1 = 222 - rA;
                        	double gD1 = 174 - gA;
                        	double bD1 = 63 - bA;
                        /*	
                        	double rD2 = 119 - rA;
                        	double gD2 = 116 - gA;
                        	double bD2 = 91 - bA;
                        	//double d2 = Math.sqrt(rD2 * rD2 + gD2 * gD2 + bD2 * bD2);
                         */
                        	double d = Math.sqrt(rD1 * rD1 + gD1 * gD1 + bD1 * bD1);
                        	/*
                        	double a = 3 * (d/255 - .32);
                        	
                        	if(a > 1) a = 1;
                        	if(a < 0) a = 0;
                        	
                         int rE = (int)(a * rA + (1 - a) * rB);
                         int gE = (int)(a * gA + (1 - a) * gB);
                         int bE = (int)(a * bA + (1 - a) * bB);

                            pixels_Erg[pos] = bE | gE << 8 | rE << 16;
                            */
                        	
                        	if(d < 100){
                        		pixels_Erg[pos] = pixels_B[pos];
                        	}else{
                        		pixels_Erg[pos] = pixels_A[pos];
                        }
					}
					if(methode == 7){
						int maxrad = (int) Math.sqrt((height*height) + (width*width));
						
								if(Math.sqrt(y*y + x*x) < maxrad * (z/length)) {
								rA = rB;
								gA = gB;
								bA = bB;
								}
								pixels_Erg[pos] = bA | gA << 8 | rA << 16;
							}
						
						}
					}
				



		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();

	}

}

