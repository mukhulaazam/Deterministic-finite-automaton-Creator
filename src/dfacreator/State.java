package dfaCreator;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * <!-- class State -->
 *
 * This class represents a state that can be drawn on a canvas
 *
 * @author Andrew M. Nuxoll
 * @author Steven R. Vegdahl
 * @author Caleb M. Piekstra
 * @version 28 August 2014
 */
/*
 * This class is the framework upon which the other tokens are based and is
 * used to draw a circle
 */
public class State extends JPanel
{
	//satisfy Serializable interface
	private static final long serialVersionUID = 160820131543L;
	
	// instance variables
	protected int x; // my x coordinate
    protected int y; // my y coordinate
    public static final int radius = 40; //my radius
    protected static final Color color = new Color(0x00, 0x00, 0x00);  //black    
    protected static final int outerRadiusFactor = 4; 
    
    
    public int nodeNum;
	public boolean acceptState;
	public ArrayList<int[]> paths;
	

    /**
     * When a State is created, the creator must specify where it
     * is located.
     */
    public State(int num, int initX, int initY, boolean accepts)
    {
    	this.nodeNum = num;
        this.x = initX;
        this.y = initY;
        this.acceptState = accepts;
        paths = new ArrayList<int[]>();
    }

    /**
     * this state can paint itself on a given canvas
     */
    public void paint(Graphics canvas)
    {
        int x = this.x - State.radius/2;
        int y = this.y - State.radius/2;
        canvas.setColor(color);
        canvas.drawOval(x, y, radius, radius);
    	if (acceptState) {
            canvas.drawOval(x + radius/outerRadiusFactor/2, y + radius/outerRadiusFactor/2, radius - radius/outerRadiusFactor, radius - radius/outerRadiusFactor);    		
    	}
    	canvas.drawString("q"+this.nodeNum, this.x - 10, this.y + 4);
    }//paint
    
}//class State

