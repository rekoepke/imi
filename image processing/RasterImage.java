// BV Ue1 SS2019 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ss19;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RasterImage {
	
	private static final int gray  = 0xffa0a0a0;

	public int[] argb;	// pixels represented as ARGB values in scanline order
	public int width;	// image width in pixels
	public int height;	// image height in pixels
	
	public RasterImage(int width, int height) {
		// creates an empty RasterImage of given size
		this.width = width;
		this.height = height;
		argb = new int[width * height];
		Arrays.fill(argb, gray);
	}
	
	public RasterImage(File file) {
		// creates an RasterImage by reading the given file
		Image image = null;
		if(file != null && file.exists()) {
			image = new Image(file.toURI().toString());
		}
		if(image != null && image.getPixelReader() != null) {
			width = (int)image.getWidth();
			height = (int)image.getHeight();
			argb = new int[width * height];
			image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
		} else {
			// file reading failed: create an empty RasterImage
			this.width = 256;
			this.height = 256;
			argb = new int[width * height];
			Arrays.fill(argb, gray);
		}
	}
	
	public RasterImage(ImageView imageView) {
		// creates a RasterImage from that what is shown in the given ImageView
		Image image = imageView.getImage();
		width = (int)image.getWidth();
		height = (int)image.getHeight();
		argb = new int[width * height];
		image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
	}
	
	public void setToView(ImageView imageView) {
		// sets the current argb pixels to be shown in the given ImageView
		if(argb != null) {
			WritableImage wr = new WritableImage(width, height);
			PixelWriter pw = wr.getPixelWriter();
			pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
			imageView.setImage(wr);
		}
	}
	
	
	// image point operations to be added here
	
	public void convertToGray() {
		// TODO: convert the image to grayscale
		for(int pos = 0; pos < argb.length; pos++) {
			int value = argb[pos];
					
			int r = (value >> 16) & 0xff;
			int g = (value >>  8) & 0xff;
			int b =  value        & 0xff;
			
			int q = (r+g+b)/3;
			
			int rn = q;
			int gn = q;
			int bn = q;
			
			argb[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
		}
	}
	
	/**
	 * @param quantity The fraction of pixels that need to be modified
	 * @param strength The brightness to be added or subtracted from a pixel's gray level
	 */
	public void addNoise(double quantity, int strength) {
		// TODO: add noise with the given quantity and strength
		int rn = 0; 
		int gn = 0; 
		int bn = 0;
		
		//Um 0-60% der gesamten Anzahl der Pixel zu erhalten teilt man die Gesamtanzahl der Pixel(argb.length) durch 
		//den quantity Faktor(ist nen double zwischen 0.0-0.6) Somit läuft die for-Schleife von 0 bis maximal 
		//argb.length*0.6(also über 60% aller Pixel im Array) und würfelt im folgenden so viele zufällige Positionen
		for(int i = 0; i < argb.length*quantity; i++) {
			Random rand = new Random();
			int randomNum = rand.nextInt(argb.length);
			
			//man nimmt sich diesen Wert der an der gewürfelten Index-Position steht. Ist ein langer code der
			//noch zusammenhängenden argb-Werte
			int curr = argb[randomNum];

			//curr shiften und Farbwerte in Variablen speichern
			int r = (curr >> 16) & 0xff;
			int g = (curr >>  8) & 0xff;
			int b =  curr        & 0xff;
			
			//Graustufenbild erzeugen
			int q = (r+g+b)/3;

			//Zufallsgenerator, ob heller oder dunkler
			Random random = new Random();
			int binary = random.nextInt(2);
			
			//Fallunterscheidung und entweder den Helligkeitswert draufrechnen oder abziehen. Clamp wichtig,
			//da es sonst zu nem Overflow kommt
			switch(binary) {
			case 0: rn = gn = bn = clamp(q + strength);
			break;
			case 1: rn = gn = bn = clamp(q - strength);
			break;
			}
			//Werte zurückschreiben
			argb[randomNum] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
		}
	}	
	
	private int clamp(int val) {
		
		if(val<=0)		val = 0;
		if(val>=255) 	val = 255;
		
		return val;
	}
}
