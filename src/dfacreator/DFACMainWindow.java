package dfaCreator;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableModel;

/**
 * <!-- class MainWindow -->
 *
 * This is the main class for the program.  The program creates the window
 * containing a drawing area and allows the user to create a DFA. 
 * 
 * Graphical
 * 		Create a DFA with accept and reject
 * 		directed
 * Generate formal description from state diagram
 * process strings provided
 * 		accepted? 
 * 
 *
 * (Q, E, 6, q0, F)
 * Set of states
 * Set of symbols (alphabet)
 * transition function (6 : Q x E -> Q)
 * start sate
 * set of accept states
 *
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @author Caleb M. Piekstra
 * @version 23 November 2014
 */

public class DFACMainWindow  extends JPanel implements MouseInputListener, ActionListener
{
	int z=0;
        //to satisfy the Serializable interface inherited from JPanel
	private static final long serialVersionUID = 16082013L;

	/** the size of the campus map image */
	private static final int JPEG_WIDTH = 700;
	private static final int JPEG_HEIGHT = 450;
	public static final int TRANS_ON_ZERO = 0;
	public static final int TRANS_ON_ONE = 1;
	public static final int TRANS_ON_BOTH = 2;
	private static final int TEXT_FIELD_LENGTH = 37;

	/** the size of the window (height and width) */
	public static final int WINDOW_SIZE = JPEG_WIDTH;
	private static final Object[] transColHeaders = {"State (q)", "Input (w)", "Next State ( \u03B4 (q, w))"};

	//this is the canvas that will show the map with the token on it
	static JpegCanvas diagramCanvas = new JpegCanvas(DFACMainWindow.class.getResource("resources/blankScreen.jpg"),
			JPEG_WIDTH,
			JPEG_HEIGHT);

	private static JButton createStateButton = new JButton("Create State");
	private static JButton createLinkButton = new JButton("Link Nodes");
	private static JButton linkToSelfButton = new JButton("Link Node to Self");
	private static JButton clearAllButton = new JButton("Clear All");
	private static JButton generateFormalDescriptionButton = new JButton("Generate Formal Description");
	private static JButton runStringButton = new JButton("Run DFA on w");

	private static JTextField inputStringField = new JTextField(TEXT_FIELD_LENGTH);

	private static JTextArea inputStringArea = new JTextArea(2, TEXT_FIELD_LENGTH);
	private static JTextArea runResult = new JTextArea(2, TEXT_FIELD_LENGTH);
	private static JTextArea formalDescription = new JTextArea(2, TEXT_FIELD_LENGTH);

	private static JTable transitionTable = new JTable(new DefaultTableModel(transColHeaders, 0));

	private static JScrollPane inputPaneArea = new JScrollPane(inputStringArea);
	private static JScrollPane inputPaneResult = new JScrollPane(runResult);
	private static JScrollPane inputPaneFormal = new JScrollPane(formalDescription);
	private static JScrollPane tablePane = new JScrollPane(transitionTable);


	private enum actions {
		makeNode, makeLink, linkToSelf, doNothing
	}
	private actions currentAction = actions.doNothing;

	private int nrNodes = 0;

	// Transitions int[] arrays are of size 3, int[0] being the number of the first state, 
	// int[1] being the second, and int[2] being the symbols that cause it to transition
	public static ArrayList<int[]> transitions = new ArrayList<int[]>();	
	public static List<Shape> lines = new ArrayList<Shape>();
	public static ArrayList<State> states = new ArrayList<State>();
	public static List<int[]> arcs = new ArrayList<int[]>();
	public static Shape line = null;
	
	public static int[] followOval = null;
	public static int[] startLine;
	public static int[] startPoly1;
	public static int[] startPoly2;

	//	public static List<Shape> lines = new ArrayList<Shape>();
	private static int currentLineStart = 0;
	//    private static boolean startState = true;

	/**
	 * paint
	 *
	 * draws any additional images needed
	 */
	@Override
	public void paint(Graphics g) {}//paint

	/**
	 * setupLayout
	 *
	 * create a layout of the widgets on the window
	 *
	 * @arg frame  the window frame to put the layout into
	 */
	public void setupLayout(JFrame frame)
	{
		//Setup a main Box to contain our layout
		Box mainBox = Box.createVerticalBox();
		frame.setContentPane(mainBox);		
		mainBox.add(diagramCanvas);
		mainBox.add(Box.createRigidArea(new Dimension(20,20)));
		mainBox.add(Box.createVerticalGlue());

		//Swing won't play nice with sizes so we force the map size here
		diagramCanvas.setSize(JPEG_WIDTH, JPEG_HEIGHT);
		diagramCanvas.setMinimumSize(diagramCanvas.getSize());
		diagramCanvas.setMaximumSize(diagramCanvas.getSize());
		diagramCanvas.setPreferredSize(diagramCanvas.getSize());

		//Create a Box containing our interface
		Box interfaceRegion = Box.createVerticalBox();
		mainBox.add(interfaceRegion);		

		Box topButtons = Box.createHorizontalBox();
		Box bottomButtons = Box.createHorizontalBox();
		Box inputRegion = Box.createHorizontalBox();
		Box textLabels = Box.createVerticalBox();
		Box textInput = Box.createVerticalBox();
		Box transitionRegion = Box.createHorizontalBox();

		interfaceRegion.add(topButtons);
		interfaceRegion.add(bottomButtons);
		interfaceRegion.add(inputRegion);
		interfaceRegion.add(transitionRegion);

		inputRegion.add(textLabels);
		inputRegion.add(textInput);

		topButtons.add(createStateButton);
		topButtons.add(createLinkButton);
		topButtons.add(linkToSelfButton);
		topButtons.add(clearAllButton);

		bottomButtons.add(generateFormalDescriptionButton);
		bottomButtons.add(runStringButton);

		JTextArea inputPrompt = new JTextArea("Type the input string here:");		
		JTextArea input = new JTextArea("Input w:");
		JTextArea runLabel = new JTextArea("Running DFA on w result:");
		JTextArea formalLabel = new JTextArea("Formal Description (Q, \u03A3, \u03B4, q0, F):");
		JTextArea lowerDeltaLabel = new JTextArea("Transition Function ( \u03B4 ):");

		inputPrompt.setEditable(false);
		inputPrompt.setOpaque(false);

		input.setEditable(false);
		input.setOpaque(false);

		formalLabel.setEditable(false);
		formalLabel.setOpaque(false);

		runLabel.setEditable(false);
		runLabel.setOpaque(false);

		lowerDeltaLabel.setEditable(false);
		lowerDeltaLabel.setOpaque(false);

		textLabels.add(inputPrompt);		
		textLabels.add(input);		
		textLabels.add(formalLabel);
		textLabels.add(runLabel);
		transitionRegion.add(lowerDeltaLabel);

		textInput.add(inputStringField);
		textInput.add(inputPaneArea);
		textInput.add(inputPaneResult);
		textInput.add(inputPaneFormal);
		transitionRegion.add(tablePane);

		//Tell the operating system to send me mouse events when the user clicks
		//on the diagram or one of the buttons or enters text in the text field
		diagramCanvas.addMouseListener(this);
		diagramCanvas.addMouseMotionListener(this);
		createStateButton.addActionListener(this);
		createLinkButton.addActionListener(this);
		linkToSelfButton.addActionListener(this);
		clearAllButton.addActionListener(this);
		generateFormalDescriptionButton.addActionListener(this);
		runStringButton.addActionListener(this);
		inputStringField.addActionListener(this);

	}//setupLayout

	public static void drawArrow(int size, Point from, Point to, Point offset) {
		double dx = to.x - from.x, dy = to.y - from.y;
		int len = (int) Math.sqrt(dx*dx + dy*dy);

		int xOffset = from.x + offset.x;
		int yOffset = from.y + offset.y;
		startLine = new int[] {0 + xOffset, 0 + yOffset, len + xOffset, 0 + yOffset};
		startPoly1 = new int[] {len + xOffset, len-size + xOffset, len-size + xOffset, len + xOffset};
		startPoly2 = new int[] {0 + yOffset, -size + yOffset, size + yOffset, 0 + yOffset};
	}

	/**
	 * mousePressed
	 *
	 * is called whenever the user clicks on the canvas.  It creates a
	 * new ornament at that position and repaints the canvas to show the
	 * user.
	 */
	public void mousePressed(MouseEvent event)
	{
		if (currentAction == actions.makeNode) {   
			followOval = null;
			boolean accepts = false;
			int dialogResult = JOptionPane.showConfirmDialog (null, "Is this an accept state?","Warning",JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				accepts = true;
			}     	
			State s = new State(nrNodes++, event.getX(), event.getY(), accepts);

			states.add(s);

			if (nrNodes == 1) {
				drawArrow(10,
						new Point(event.getX() - 30 - State.radius/2, event.getY()), 
						new Point(event.getX() - State.radius/2, event.getY()), 
						new Point(0, 0));
			}
			currentAction = actions.doNothing;
			diagramCanvas.repaint();
		} else if (currentAction == actions.makeLink) {
			line = new Line2D.Double(event.getPoint(), event.getPoint());
			lines.add(line);	     
			currentLineStart = lines.size() - 1;

			diagramCanvas.repaint();
		} else if (currentAction == actions.linkToSelf) {
			for(State node : states) {
				// Check if the user clicked within a state's circle
				double xDist = node.x - event.getX();
				double yDist = node.y - event.getY();				
				if (Math.sqrt(xDist * xDist + yDist * yDist) <= State.radius) {
					String s = (String)JOptionPane.showInputDialog(
							null,
							"What symbols cause\n"
									+ "this transition?",
									"Transition",
									JOptionPane.PLAIN_MESSAGE,
									null,
									null,
							"0, 1");

					int symbols = -1;
					//If a string was returned, say so.
					if ((s != null) && (s.length() > 0)) {
						if (s.contains("0")) {
							if (s.contains("1")) {
								symbols = TRANS_ON_BOTH;											
							} else {
								symbols = TRANS_ON_ZERO;	
							}
						} else if (s.contains("1")) {
							symbols = TRANS_ON_ONE;											
						}
					}
					arcs.add(new int[]{node.x - 5, node.y - State.radius + 6, State.radius*2/3, State.radius*2/3, -45, 225});	// x y w h startAngle endAngle
					int[] arc = arcs.get(arcs.size() - 1);
					
					int stringX = arc[0];
					int stringY = arc[1] - 3;
					double angle = 2;	// straight down

					int arrowHeight = 9;                 // change as seen fit
					int halfArrowWidth = 7;              // this too
					Point2D end = new Point(arc[0] - 1, arc[1] + 13);
					Point2D aroBase = new Point2D.Double(
							end.getX() - arrowHeight*Math.cos(angle),
							end.getY() - arrowHeight*Math.sin(angle)); 

					Point2D end1 = new Point2D.Double(
							aroBase.getX()-halfArrowWidth*Math.cos(angle-Math.PI/2),
							aroBase.getY()-halfArrowWidth*Math.sin(angle-Math.PI/2));

					//locate one of the points, use angle-pi/2 to get the
					//angle perpendicular to the original line(which was 'angle')
					Point2D end2 = new Point2D.Double(
							aroBase.getX()+halfArrowWidth*Math.cos(angle-Math.PI/2),
							aroBase.getY()+halfArrowWidth*Math.sin(angle-Math.PI/2));

					lines.add(new Line2D.Double(end2,end));
					lines.add(new Line2D.Double(end1,end));

					node.paths.add(new int[]{node.nodeNum, symbols});	
					transitions.add(new int[]{stringX, stringY, symbols});

					diagramCanvas.repaint();
					currentAction = actions.doNothing;
					break;
				}
			}
		}
	}//mousePressed

	// MouseListener Events
	public void mouseClicked(MouseEvent e)   {}
	public void mouseEntered(MouseEvent e)   {}
	public void mouseExited(MouseEvent e)   {}

	public void mouseReleased(MouseEvent e)   {
		if (currentAction == actions.makeLink) {			
			Line2D shape =(Line2D)line;
			double angle = Math.atan2(        //find angle of line
					shape.getY2()-shape.getY1(),
					shape.getX2()-shape.getX1());

			int arrowHeight = 9;                 // change as seen fit
			int halfArrowWidth = 5;              // this too
			Point2D end = shape.getP2();
			Point2D aroBase = new Point2D.Double(
					shape.getX2() - arrowHeight*Math.cos(angle),
					shape.getY2() - arrowHeight*Math.sin(angle)); 

			//determine the location of middle of
			//the base of the arrow - basically move arrowHeight
			//distance back towards the starting point
			Point2D end1 = new Point2D.Double(
					aroBase.getX()-halfArrowWidth*Math.cos(angle-Math.PI/2),
					aroBase.getY()-halfArrowWidth*Math.sin(angle-Math.PI/2));

			//locate one of the points, use angle-pi/2 to get the
			//angle perpendicular to the original line(which was 'angle')
			Point2D end2 = new Point2D.Double(
					aroBase.getX()+halfArrowWidth*Math.cos(angle-Math.PI/2),
					aroBase.getY()+halfArrowWidth*Math.sin(angle-Math.PI/2));

			lines.add(new Line2D.Double(end2,end));
			lines.add(new Line2D.Double(end1,end));

			Line2D.Double lineStart = (Line2D.Double)lines.get(currentLineStart);
			Line2D.Double lineEnd = (Line2D.Double)lines.get(lines.size() - 1);		
			boolean connectedNodes = false;
			node1loop:
				for(State node1 : states) {
					// Check if the user clicked within a state's circle
					double xDist = node1.x - lineStart.getX1();
					double yDist = node1.y - lineStart.getY1();				
					if (Math.sqrt(xDist * xDist + yDist * yDist) <= State.radius) {
						for(State node2 : states) {
							if (node2 == node1) {
								continue;
							}
							// Check if the user clicked within a state's circle
							xDist = node2.x - lineEnd.getX2();
							yDist = node2.y - lineEnd.getY2();				
							if (Math.sqrt(xDist * xDist + yDist * yDist) <= State.radius) {
								String s = (String)JOptionPane.showInputDialog(
										null,
										"What symbols cause\n"
												+ "this transition?",
												"Transition",
												JOptionPane.PLAIN_MESSAGE,
												null,
												null,
										"0, 1");

								int symbols = -1;
								//If a string was returned, say so.
								if ((s != null) && (s.length() > 0)) {
									if (s.contains("0")) {
										if (s.contains("1")) {
											symbols = TRANS_ON_BOTH;											
										} else {
											symbols = TRANS_ON_ZERO;	
										}
									} else if (s.contains("1")) {
										symbols = TRANS_ON_ONE;											
									}
								}
								int stringX = (int)(lineEnd.x2 - (lineEnd.x2 - lineStart.x1)/2);
								int stringY = (int)(lineEnd.y2 - (lineEnd.y2 - lineStart.y1)/2) - 7;

								node1.paths.add(new int[]{node2.nodeNum, symbols});	
								transitions.add(new int[]{stringX, stringY, symbols});
								connectedNodes = true;
								break node1loop;
							}
						}
					}
				}
			// If we did not connect two nodes, remove this line
			if (!connectedNodes) {
				lines.subList(currentLineStart, lines.size()).clear();	
			}
			diagramCanvas.repaint();
			line = null;	// Resets the line
			currentAction = actions.doNothing;	// Reset the active action
		}
	}
	// MouseInputListener Events
	public void mouseDragged(MouseEvent e) {
		if (currentAction == actions.makeLink) {
			Line2D shape =(Line2D)line;
			shape.setLine(shape.getP1(), e.getPoint());
			diagramCanvas.repaint();
		}
	}

	public void mouseMoved(MouseEvent me) {
		if (currentAction == actions.makeNode) {
			int x = me.getX() - State.radius/2;
			int y = me.getY() - State.radius/2;
			followOval = new int[]{x, y, State.radius, State.radius};
			diagramCanvas.repaint();
		}
	}

	/** when the user selects a radio button, change the token type */
	public void actionPerformed(ActionEvent event)
	{
		currentAction = actions.doNothing;	// The default action
		if (event.getSource() == createStateButton) {
			currentAction = actions.makeNode;
		} else if (event.getSource() == createLinkButton) {
			currentAction = actions.makeLink;
		} else if (event.getSource() == linkToSelfButton) {
			currentAction = actions.linkToSelf;
		} else if (event.getSource() == generateFormalDescriptionButton) {
			if (nrNodes < 1) {
				JOptionPane.showMessageDialog(null, "Not enough states");
				return;
			}
			String setOfStates = "{q0";
			String setOfSymbols = "{0, 1}";
			String transitions = "\u03B4";
			String startState = "q0";
			String acceptStates = "{";
			for (int i = 1; i < states.size(); i++) {
				setOfStates += ", q" + i;
			}
			setOfStates += "}";
			boolean gotFirstAcceptState = false;
			for (State state : states) {
				if (state.acceptState) {
					if (gotFirstAcceptState) {
						acceptStates += ", ";
					}
					gotFirstAcceptState = true;
					acceptStates += "q" + state.nodeNum;
				}
			}
			acceptStates += "}";
			formalDescription.setText("(" + setOfStates + ", " + setOfSymbols + ", " + transitions + ", " + startState + ", " + acceptStates + ")");

			ArrayList<Object[]> rowData = new ArrayList<Object[]>();
			for (State state: states) {
				for (int[] path : state.paths) {
					String s = "";
					switch (path[1]) {
					case DFACMainWindow.TRANS_ON_ZERO:	s = "0";	break;
					case DFACMainWindow.TRANS_ON_ONE:	s = "1"; 	break;					
					case DFACMainWindow.TRANS_ON_BOTH:	s = "0, 1";	break;
					default: s = "error"; break;
					}
					if (s.length() < 2) {	// If the transition is on just 0 or 1 make one row
						z=-1;
                                                rowData.add(new Object[]{new Integer(state.nodeNum), s, new Integer(path[0])});
						//						tableData
					}else if(s.length()==2) {				// if the transition is on both 0 and 1, make two rows
						rowData.add(new Object[]{state.nodeNum, "0", path[0]});
						rowData.add(new Object[]{state.nodeNum, "1", path[0]});
					}
				}
			}
			DefaultTableModel transModel = (DefaultTableModel) transitionTable.getModel();
			transModel.setNumRows(0);
			for (Object[] row : rowData) {
				transModel.addRow(row);
			}

		} else if (event.getSource() == runStringButton) {
			if (nrNodes < 1) {
				JOptionPane.showMessageDialog(null, "Not enough states");
				return;
			}else if(z<0){JOptionPane.showMessageDialog(null, "Error1");
				return;}

			boolean extraSymbolsOrNoPath = false;
			boolean transitioned = false;
			State currentState = states.get(0);	// Get the first state		
			symbolLoop:
				for (char currentSymbol : inputStringArea.getText().toCharArray()) {	
					for (int[] path : currentState.paths) {
						switch (path[1]) {
						case TRANS_ON_ZERO: 
							if (currentSymbol == '0') {
								transitioned = true;
							}
							break;
						case TRANS_ON_ONE:
							if (currentSymbol == '1') {
								transitioned = true;		
							}
							break;
						case TRANS_ON_BOTH: 
							if (currentSymbol == '0' || currentSymbol == '1') {
								transitioned = true;
							}
							break;
						}
						if (transitioned) {
							transitioned = false;
							currentState = states.get(path[0]);
							continue symbolLoop;						
						}
					}
					extraSymbolsOrNoPath = true;	// We continue if we transition, otherwise we have extra symbols or there is no path for the given character
				}
			if (currentState.acceptState && !extraSymbolsOrNoPath) {
				JOptionPane.showMessageDialog(null, "The string was accepted");
				runResult.setText(inputStringArea.getText() + " was accepted.");				
			} else if(extraSymbolsOrNoPath) {
				JOptionPane.showMessageDialog(null, "Error");
				//runResult.setText(inputStringArea.getText() + " was not accepted.");	
			}
                        else {
				JOptionPane.showMessageDialog(null, "The string was not accepted");
				runResult.setText(inputStringArea.getText() + " was not accepted.");	
			}

		} else if (event.getSource() == clearAllButton) {
			states.clear();
			lines.clear();
			transitions.clear();
			arcs.clear();
			nrNodes = 0;
			startLine = null;
			startPoly1 = null;
			startPoly2 = null;
			resetTextAreas();
			transitionTable.setModel(new DefaultTableModel(transColHeaders, 0));	// Clear the table of all rows
			diagramCanvas.repaint();
		} else if (event.getSource() == inputStringField) {
			inputStringArea.setText("");
			inputStringArea.setText(inputStringField.getText().toString());
		}
	}

	private void resetTextAreas() {
		inputStringArea.setText("");
		inputStringField.setText("");
		formalDescription.setText("(Q, \u03A3, \u03B4, q0, F)");
		runResult.setText(inputStringArea.getText() + " is accepted/rejected");
	}

	/**
	 * creates the window frame and kicks off the program
	 */
	static JFrame myFrame = new JFrame("DFA Builder");
	public static void main(String[] args)
	{
                //Create a window for this program
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setSize(WINDOW_SIZE, WINDOW_SIZE*10/8 + 20);
		//		myFrame.setResizable(false);

		//use JpegCanvas to put a map in the window
		DFACMainWindow myself = new DFACMainWindow();
		myself.setupLayout(myFrame);

		inputPaneArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		inputPaneArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		inputPaneResult.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		inputPaneResult.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		inputPaneFormal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		inputPaneFormal.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		inputStringField.setUI(new HintTextFieldUI("Input w", true));
		inputStringArea.setEditable(false);
		inputStringArea.setFocusable(false);
		inputStringArea.setHighlighter(null);
		inputStringArea.setText("011001");
		inputStringArea.setOpaque(false);
		formalDescription.setEditable(false);
		formalDescription.setFocusable(false);
		formalDescription.setHighlighter(null);
		formalDescription.setText("(Q, \u03A3, \u03B4, q0, F)");
		formalDescription.setOpaque(false);
		runResult.setEditable(false);
		runResult.setFocusable(false);
		runResult.setHighlighter(null);
		runResult.setText(inputStringArea.getText() + " is accepted/rejected");
		runResult.setOpaque(false);

		// Subscribe to the window events and listen for the window being minimized or maximized
		myFrame.addWindowListener(
				new WindowAdapter() {
					public void windowIconified(WindowEvent e) {		// if the window was minimized, this will run
						System.out.println("minimized");        				
					}
					public void windowDeiconified(WindowEvent e) {	// if the window was maximized, this will run
						System.out.println("maximized");
					}
				});


		//Ready!
		myFrame.setVisible(true);

	}//main
}