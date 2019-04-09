// BV Ue1 SS2019 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ss19;

public class Filter {
	
	public enum FilterType { 
		COPY("Copy Image"), 
		BOX("Box Filter"), 
		MEDIAN("Median Filter");
		
		private final String name;       
	    private FilterType(String s) { name = s; }
	    public String toString() { return this.name; }
	};

	public enum BorderProcessing { 
		CONTINUE("Border: Constant Continuation"), 
		WHITE("Border: White");
		
		private final String name;       
	    private BorderProcessing(String s) { name = s; }
	    public String toString() { return this.name; }
	};

	// filter implementations go here:
	
	public void copy(RasterImage src, RasterImage dst) {
		// TODO: just copy the image
		for(int pos = 0; pos < src.argb.length; pos++) {
			dst.argb[pos] = src.argb[pos];
		}
	}
	
	public void box(RasterImage src, RasterImage dst, int kernelSize, BorderProcessing borderProcessing) {
		// TODO: implement a box filter with given kernel size and border processing
		
		switch(borderProcessing) {
		case CONTINUE:
			break;
		case WHITE:
			break;
		}
	}
	
	public void median(RasterImage src, RasterImage dst, int kernelSize, BorderProcessing borderProcessing) {
		// TODO: implement a median filter with given kernel size and border processing
		
	}
}
