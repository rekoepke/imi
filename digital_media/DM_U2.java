package digitalmedia;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
*/
public class DM_U2_A2 implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	conversionTest();
    	IJ.open("/Users/Rebecca/Documents/Uni/GDMD/orchid.jpg");
    	//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");
		
		DM_U2_A2 pw = new DM_U2_A2();
		pw.imp = IJ.getImage();
		pw.run("");
	}
	
    
    private static void conversionTest() {
    	System.out.println("Color CONVERSIONTEST");
		for (int r =0; r< 256; r++) {
			for (int g = 0; g<256; g++) {
				for  (int b = 0; b<256; b++) {
					double[]YCbCrResult = RGB2YCbCr( r, g, b);
					int backConverted[] = YCbCr2RGB(YCbCrResult[0], YCbCrResult[1], YCbCrResult[2]);
					if ( r != backConverted[0] || g != backConverted[1] || b !=  backConverted[2]){
						System.out.println( "Your Conversion failed: r: "+ r + " g: " +g + " b: "+b + 
								" != " + backConverted[0] + ", " + backConverted[1]+", "+ backConverted[2]);
					}
				}
			}
		}
		System.out.println("done");
	}
    
	public static double[] RGB2YCbCr(int r, int g, int b) {
	    	double y = 0.299 * r + 0.587 * g + 0.114 * b;
	    	double cb = -0.168736 * r - 0.331264 * g + 0.5 * b;
	    	double cr = 0.5 * r - 0.418688 * g - 0.081312 * b;
	    	
	    double[] resultYCbCr = {y, cb, cr};
	    
	    return resultYCbCr;
	}
    /*
	public static int[] RGB2YCbCr(int r, int g, int b) {
	    	double y = (299 * r + 587 * g + 114 * b)/1000;
	    	double cb = (-168736 * r - 331264 * g + 500000 * b)/1000000;
	    	double cr = (500000 * r - 418688 * g)/1000000 - (81312 * b)/100000;
	    	
	    double[] resultYCbCr = {y, cb, cr};
	    
	    return resultYCbCr;
	}
	*/

	public static int[] YCbCr2RGB(double y, double cb, double cr) {
		int r = (int)Math.round(y + 1.402 * cr);
		int g = (int)Math.round(y - 0.3441*cb - 0.7141*cr);
		int b = (int)Math.round(y + 1.772*cb);
		
		//Begrenzung Wertebereich
		if(r>255)	r = 255;
		if(g>255)	g = 255;
		if(b>255)	b = 255;
				
		if(r<0)		r = 0;
		if(g<0)		g = 0;
		if(b<0)		b = 0;
		
		int[] resultRGB = {r, g, b};
		
		return resultRGB;
	}
	/*
	public static int[] YCbCr2RGB(int y, int cb, int cr) {
		int r = (int)Math.round((y * 1000 + 1402 * cr)/1000);
		int g = (int)Math.round((y*10000 - 3441*cb - 7141*cr)/10000);
		int b = (int)Math.round((y*1000 + 1772*cb)/1000);
		int[] result = {r, g, b};
		
		//Begrenzung Wertebereich
		if(r>255)	r = 255;
		if(g>255)	g = 255;
		if(b>255)	b = 255;
				
		if(r<0)		r = 0;
		if(g<0)		g = 0;
		if(b<0)		b = 0;
		
		int[] resultRGB = {r, g, b};
		
		return resultRGB;
		*/
	
	
	
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
    
    
    class CustomWindow extends ImageWindow implements ChangeListener {
         
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JSlider jSliderBrightness;
		private JSlider jSliderContrast;
		private JSlider jSliderSaturation;
		private JSlider jSliderHue;
		private double brightness;
		private double contrast = 1.0;
		private double saturation = 1.0;
		private double hue;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
        	//JPanel panel = new JPanel();
        	Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderBrightness = makeTitledSlider("Helligkeit", -128, 128, 0);
            jSliderContrast = makeTitledSlider("Kontrast", 0, 100, 10);
            jSliderSaturation = makeTitledSlider("Sättigung", 0, 50, 10);
            jSliderHue = makeTitledSlider("Farbton", 0, 360, 0);
            panel.add(jSliderBrightness);
            panel.add(jSliderContrast);
            panel.add(jSliderSaturation);
            panel.add(jSliderHue);
            
            add(panel);
            
            pack();
         }
      
        private JSlider makeTitledSlider(String string, int minVal, int maxVal, int val) {
		
        	JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
        	Dimension preferredSize = new Dimension(width, 50);
        	slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);
			
			return slider;
		}
        
        private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
				str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}

		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();

			if (slider == jSliderBrightness) {
				brightness = slider.getValue();
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderBrightness, str); 
			}
			
			if (slider == jSliderContrast) {
				contrast = slider.getValue()/10.0;
				String str = "Kontrast" + contrast; 
				setSliderTitle(jSliderContrast, str); 
			}
			
			if (slider == jSliderSaturation) {
				saturation = slider.getValue()/10.0;
				String str = "Sättigung " + saturation;
				setSliderTitle(jSliderSaturation, str); 
			}
			
			if (slider == jSliderHue) {
				hue = slider.getValue();
				String str = "Hue " + hue;
				setSliderTitle(jSliderHue, str); 
			}
			
			changePixelValues(imp.getProcessor());
			
			imp.updateAndDraw();
		}



		private void changePixelValues(ImageProcessor ip) {
			
			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			double sin = Math.sin(Math.toRadians(hue));
			double cos = Math.cos(Math.toRadians(hue));
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					
					// anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
					// die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren
					/*int rn = (int) (r + brightness);
					int gn = (int) (g + brightness);
					int bn = (int) (b + brightness);
					*/
					
					double[]YCbCr = RGB2YCbCr(r, g, b);
					
					double cY =  YCbCr[0];
					double cCb = YCbCr[1];
					double cCr = YCbCr[2];
					
					double yN = (cY - 128) * contrast +128 + brightness;
					double cbN = (cCb * cos + cCr * sin) * (contrast) * (saturation);
					double crN = (cCb * -sin + cCr * cos) * (contrast) * (saturation);
				  
					int[] convert = YCbCr2RGB(yN, cbN, crN);
					int rn = convert[0];
					int gn = convert[1];
					int bn = convert[2];
				    
					
					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				
					
                    // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
                     
                    
				}
			}
		}
		
    } // CustomWindow inner class
} 



