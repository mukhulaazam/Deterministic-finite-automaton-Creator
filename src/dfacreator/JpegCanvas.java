package dfaCreator;

/**
 * <!-- class JpegCanvas -->
 *
 * This is the class that displays a JPEG image. 
 *
 * Students:  DO NOT MODIFY THIS FILE UNLESS YOU DO THE ENHANCEMENT!
 *            (But you're encouraged to look at the code)
 *
 * @author Steven R. Vegdahl
 * @version 20 August 2014
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class JpegCanvas extends JPanel {

	// to satisfy Serializable interface
	private static final long serialVersionUID = -3602904541546623419L;

	// The object that contains JPEG image
	private Image image;

	/**
	 * constructor: creates a canvas that contains an image read in from a JPEG file.
	 * A blank image is used if there the file could not be read, or if were not properly
	 * formatted as a JPEG image.y
	 * 
	 * @param file
	 * 		the File object denoting the file that contains the image to be displayed
	 * @param width
	 * 		the width, in pixels, to which the image will be scaled
	 * @param height
	 * 		the height, in pixels, to which the image will be scaled
	 */
	public JpegCanvas(URL file, int width, int height) {
		// variable into which the image will be read
		BufferedImage img = null; 

		// read the image, if possible
		try {
			img = ImageIO.read(file); 
			img.flush(); 
		} catch (IOException ex) { 
		}

		// at this point, 'img' is an actual (non-null) object unless there were
		// a problem reading the image from the file

		if (img != null) {
			// have an image: update the instance variable; this our size
			image = img.getScaledInstance((int)width, (int)height, Image.SCALE_SMOOTH);
			this.setSize(image.getWidth(null), image.getHeight(null));
		}
		else {
			// just set our size, since there is no image
			this.setSize(width, height);
		}
	}

	/**
	 * paint callback-method; called whenever the widget needs to be repainted
	 * 
	 * @param g
	 * 		the graphics object, used to paint the widget
	 */
	public void paint(Graphics g) {
		// do superclass painting behavior
//		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;		
		g2d.setPaint(Color.BLACK);

		// if the image is non-null, display it on our widget
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
		if (DFACMainWindow.followOval != null) {
			g.drawOval(DFACMainWindow.followOval[0], DFACMainWindow.followOval[1], DFACMainWindow.followOval[2], DFACMainWindow.followOval[3]);
		}
		// Redraw all links to nodes
		for(Shape line: DFACMainWindow.lines) {
			g2d.draw(line); 			
		}
		// Redraw all links to self
		for(int[] arc: DFACMainWindow.arcs) {
			g.drawArc(arc[0], arc[1], arc[2], arc[3], arc[4], arc[5]);	
		}
		// Redraw all nodes
		for (State state: DFACMainWindow.states) {
			state.paint(g);
		}
		// Redraw all the symbols above the links
		for (int[] transition : DFACMainWindow.transitions) {
			String s = "";
			switch (transition[2]) {
				case DFACMainWindow.TRANS_ON_ZERO:	s = "0";	break;
				case DFACMainWindow.TRANS_ON_ONE:	s = "1"; 	break;					
				case DFACMainWindow.TRANS_ON_BOTH:	s = "0, 1";	break;
				default: s = "error"; break;
			}
			g.drawString(s, transition[0], transition[1]);
		}
		// Redraw the start state's arrow
		if (DFACMainWindow.startLine != null && DFACMainWindow.startPoly1 != null && DFACMainWindow.startPoly2 != null) {
			g2d.drawLine(DFACMainWindow.startLine[0], DFACMainWindow.startLine[1], DFACMainWindow.startLine[2], DFACMainWindow.startLine[3]);
			g2d.fillPolygon(DFACMainWindow.startPoly1, DFACMainWindow.startPoly2, 4);
		}
	}
	
	public void saveCanvasAsImage(JpegCanvas mCanvas)
	{
		if (image != null) {
			mCanvas.getGraphics();
		}
	}
}
