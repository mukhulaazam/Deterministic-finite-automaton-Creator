package dfaCreator;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * <!-- class TokenCanvas -->
 *
 * @author Andrew Nuxoll
 * @author Caleb Piekstra
 * @version 23 November 2014
 */
public class TokenCanvas extends JPanel
{
	//satisfy the Serializable interface
	private static final long serialVersionUID = 160820131411L;

	/** a pale gray background color for the token  */
    public static final Color BACKGROUND_COLOR = new Color(200,200,200);

    //the token displays this map token on itself
    private State myToken;

    /**
     * ctor initializes myToken and selects background color
     */
    public TokenCanvas(State initMyToken)
    {
        myToken = initMyToken;

        this.setBackground(BACKGROUND_COLOR);
    }

	/**
     * paint
     *
     * draws the token on itself
     */
    @Override
    public void paint(Graphics canvas)
    {
        myToken.paint(canvas);
    }//paint

    
}
