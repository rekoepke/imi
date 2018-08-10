package digitalmedia;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import gdmvalidation.ScaleValidate;

public class DM_U6_B2 implements PlugInFilter {
    protected ImagePlus imp;

    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);

        //TODO open your image here
        //IJ.open("/home/...../component.jpg");
        IJ.open("/Users/Rebecca/Documents/Uni/GDM/component.jpg");

        DM_U6_B2 sd = new DM_U6_B2();
        sd.imp = IJ.getImage();
        ImageProcessor ip = sd.imp.getProcessor();
        sd.run(ip);
    }

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about"))
        {showAbout(); return DONE;}
        return DOES_RGB+NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }
    
	
	
    void test() {
    	System.out.println("If you see \"you solved it\", this only means that your solution works correct\n" +
    			"only for this scaling factor!\n" +
    			"Therefore, please try with many different values for width and height, to check if it will work for \"all\" possible values" 
    			);
    	
    	
        //pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
        int testImWidth = 4;
        int testImHeight= 4;
        int[] testIm = {  0,   0,  0, 0,
        		          0, 100,  3, 0,
        		          0,   0,  0, 0,
        		         30,  30, 30, 0
        };
        
        Utils.makeRGB(testIm);

        int outTestImWidth = 7;
        int outTestImHeight= 9;
        int [] outTestIm = new int[outTestImWidth*outTestImHeight];
        Utils.reset(outTestIm);
        
        

        
  // blank new image      
        System.out.println();
        System.out.println("orig image:");
        Utils.printGreyValues(testIm, testImWidth, testImHeight);
        
        System.out.println();
        System.out.println("new image:");
        Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
   // copy image
        System.out.println();
        copyImage(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
 		ScaleValidate.validateCopy(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
		System.out.println("Copy Image:");
		Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
		Utils.reset(outTestIm);
   // nearest neighbor
        System.out.println();
        nearestNeighbor(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
        ScaleValidate.validateNearestNeighbor(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
        System.out.println("nearest neighbour:");
        Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
        Utils.reset(outTestIm);
    // bilinear    
        System.out.println();
        bilinearInterpolation(testIm, testImWidth, testImHeight, outTestIm, outTestImWidth, outTestImHeight);
   		ScaleValidate.validateBilinear(testIm,testImWidth,testImHeight,outTestIm, outTestImWidth, outTestImHeight);
   	 
		System.out.println("bilinear image:");
		Utils.printGreyValues(outTestIm, outTestImWidth, outTestImHeight);
      	System.out.println("test finished\n");
      	Utils.reset(outTestIm);

    }


    public void run(ImageProcessor ip) {

        String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear", "Test"};
        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode",dropdownmenue,dropdownmenue[0]);
        gd.addNumericField("Breite:",800,0);
        gd.addNumericField("Hoehe:",700,0);

        gd.showDialog();

        int newWidth =  (int)gd.getNextNumber();
        int newHight = (int)gd.getNextNumber(); // _n fuer das neue skalierte Bild
        String choice = gd.getNextChoice();

        int width  = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen
        
        
        ImagePlus scaledImage = NewImage.createRGBImage("Skaliertes Bild",
                newWidth, newHight, 1, NewImage.FILL_BLACK);

        ImageProcessor ip_n = scaledImage.getProcessor();

        int[] pix = (int[])ip.getPixels();
        int[] pix_n = (int[])ip_n.getPixels();
        boolean test = false;
        if (choice == "Test") {
        	test();
        	test = true;
        } else if (choice == "Kopie") {
            copyImage(pix, width, height, pix_n, newWidth, newHight);
    		ScaleValidate.validateCopy(pix, width, height, pix_n, newWidth, newHight);
        } else if (choice == "Pixelwiederholung") {
            nearestNeighbor(pix, width, height, pix_n, newWidth, newHight);
    		ScaleValidate.validateNearestNeighbor(pix, width, height, pix_n, newWidth, newHight);
        } else if (choice == "Bilinear") {
        	bilinearInterpolation(pix, width, height, pix_n, newWidth, newHight);
    		ScaleValidate.validateBilinear(pix,width,height,pix_n, newWidth, newHight);
        }
        // neues Bild anzeigen
        if (!test) {
        	scaledImage.show();
        	scaledImage.updateAndDraw();
        }
    }

	private void bilinearInterpolation(int[] origPix, int origWidth,
			int origHeight, int[] newPix, int newWidth, int newHeight) {
		//TODO set your ratio here:
				
		double ratioX = (double)(origWidth - 1) / (newWidth - 1);
		double ratioY = (double)(origHeight - 1) / (newHeight - 1);

		// Schleife ueber das neue Bild
		for (int yNew=0; yNew<newHeight; yNew++) {
			for (int xNew=0; xNew<newWidth; xNew++) {
				//TODO add your code here
				
				int xOrig = (int) (ratioX * xNew);
				int yOrig = (int) (ratioY * yNew);
				
				int posNew = yNew* newWidth + xNew;
				
				double h = xNew * ratioX - xOrig;
				double v = yNew * ratioY - yOrig;
				
				// A
				int A = origPix[yOrig * origWidth + xOrig];
				int rA = (A >> 16) & 0xff;
				int gA = (A >> 8) & 0xff;
				int bA = A & 0xff;
				
				// B
				int B = origPix[yOrig * origWidth + clampInt(0, origWidth-1, xOrig+1)];
				
				int rB = (B >> 16) & 0xff;
				int gB = (B >> 8) & 0xff;
				int bB = B & 0xff;
				
				// C
				int C = origPix[clampInt(0, origHeight-1, yOrig + 1) * origWidth + xOrig];
				
				int rC = (C >> 16) & 0xff;
				int gC = (C >> 8) & 0xff;
				int bC = C & 0xff;
				
				// D
				int D = origPix[clampInt(0, origHeight-1, yOrig + 1) * origWidth + clampInt(0, origWidth-1, xOrig+1)];
				
				int rD = (D >> 16) & 0xff;
				int gD = (D >> 8) & 0xff;
				int bD = D & 0xff;
				
				int r, g, b;
				r = (int)(rA * (1-h) * (1-v) + rB * h * (1-v) + rC * (1-h) * v + rD * h * v+.5); 
				g = (int)(gA * (1-h) * (1-v) + gB * h * (1-v) + gC * (1-h) * v + gD * h * v+.5); 
				b = (int)(bA * (1-h) * (1-v) + bB * h * (1-v) + bC * (1-h) * v + bD * h * v+.5); 
				
				
				
				newPix[posNew] = (0xFF << 24) | (r << 16) | (g << 8) | b;
				}
			}
		}
	
	public int clampInt(int lower, int upper, int value) {
		if(value<=lower) return lower;
		else if(value>=upper) return upper;
		else return value;
	}

	private void nearestNeighbor(int[] origPix, int origWidth, int origHeight,
			int[] newPix, int newWidth, int newHeight) {
		//TODO set the values of ratioX and ratioY
		
		double ratioX = (double) (origWidth - 1) / (newWidth - 1);
		double ratioY = (double) (origHeight - 1) / (newHeight - 1); 

		// Schleife ueber das neue Bild
		for (int yNew=0; yNew<newHeight; yNew++) {
		    for (int xNew=0; xNew<newWidth; xNew++) {
		    	//TODO  put your code here
		    		int xOrig = (int) Math.round(ratioX * xNew);
		    		int yOrig = (int) Math.round(ratioY * yNew);
		    	
		    		int posNew = yNew * newWidth + xNew;
				int posOrig = yOrig * origWidth + xOrig;

				newPix[posNew] = origPix[posOrig];
		    		}
		    }
		}
		
	private void copyImage(int[] origPix, int origWidth, int origHeight,
			int[] newPix, int newWidth, int newHeight) {
		for (int yNew=0; yNew<newHeight; yNew++) {
		    for (int xNew=0; xNew<newWidth; xNew++) {
		        int y = yNew;
		        int x = xNew;

		        if (y < origHeight && x < origWidth) {
		            int posNew = yNew*newWidth + xNew;
		            int pos  =  y  *origWidth   + x;

		            newPix[posNew] = origPix[pos];
		        }
		     }
	        }
	}

    void showAbout() {
        IJ.showMessage("");
    }
}
