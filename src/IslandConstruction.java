//All libraries to be used are imported
import java.util.Scanner;
import java.lang.Math;
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.data.xy.XYDataset; 
import org.jfree.data.xy.XYSeries; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RefineryUtilities; 
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.plot.PlotOrientation; 
import org.jfree.data.xy.XYSeriesCollection; 

public class IslandConstruction extends ApplicationFrame{
	
	//Object arrays are declared
	static Node[] nodes;
	static Line[] lines;
	Line[][] insides = new Line[nodes.length][nodes.length];
	Line longestLine = insides[0][0];
	double longestDist = 0.0;
	
	//Island object constructor 
	public IslandConstruction(String applicationTitle, String chartTitle) {
		super(applicationTitle);
		JFreeChart xylineChart = ChartFactory.createXYLineChart(
				chartTitle,
				"X",
				"Y",
				createDataset(), //compute and draw the polygon
				PlotOrientation.VERTICAL,
				true, true, false);
         
		//screen panel is created
		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 800));
      
		setContentPane(chartPanel); 
	}
	
	//Method to generate the entire polygon and indicate its longest distance
	private XYDataset createDataset() {     
	     
		//Graph details are declared and initialized 
		final XYSeriesCollection dataset = new XYSeriesCollection();   
		final XYSeries polygon = new XYSeries("Polygon", false, true);   
		final XYSeries longestPath = new XYSeries( "Longest Path", false, true);
		
		//formulate the polygon itself
		for (int i = 0; i < nodes.length; i++) {
			polygon.add(nodes[i].getX(), nodes[i].getY());
		}
		polygon.add(nodes[0].getX(), nodes[0].getY());
		
		dataset.addSeries(polygon); //graph the polygon
		
		//main loop to compute longest line 
		for (int i = 0; i < nodes.length; i++) {
			int j;
			
			//define the length of current edge of polygon 
			lines[i].setLength(calcDistance(lines[i].getNodeEnd(), lines[i].getNodeStart())); //define the length of each inside line
			
			//check if the i is at second last node
			if (i == nodes.length-1) {
				j = 0;
			} else {
				j = i+1;
			}
			
			//loop through n^2 pairs of points and compute the longest line segment contained within the polygon
			while (j != i) {
				insides[i][j] = new Line(nodes[i], nodes[j]); //generate inside line to check
				insides[i][j].setLength(calcDistance(nodes[j], nodes[i])); //set the length of each inside line
				
				insides[i][j].genEquation(); //generate the current line's equation 
				
				//check if current line is valid and longer than longest line
				if (validate(insides[i][j], i)) {
					if ((insides[i][j].getLength() > longestDist)) {
						
						//assign the length of new longest line to longest distance 
						longestDist = insides[i][j].getLength();
						longestLine = insides[i][j]; //assign new longest line as longest line
					}
				}
				
				if (j == nodes.length-1) {
					j = 0;
				} else {
					j++;
				}
			}
		}

		//check if any of the edges of the polygon are longer than the longest line segment contained within 
		for (int k = 0; k < lines.length; k++) {
			if (lines[k].getLength() > longestDist) {
				longestLine = lines[k];
				longestDist = lines[k].getLength();
			}
		}
		
		//all the longest line to a data series
		longestPath.add(longestLine.getNodeStart().getX(), longestLine.getNodeStart().getY());
		longestPath.add(longestLine.getNodeEnd().getX(), longestLine.getNodeEnd().getY());
		
		//plot the longest line segment
		dataset.addSeries(longestPath);
		
		System.out.println("The coordinates of the longest line segment are A(" + longestLine.getNodeStart().getX() + ", " 
		+ longestLine.getNodeStart().getY() + ") and B(" + longestLine.getNodeEnd().getX() + ", " + longestLine.getNodeEnd().getY() + ").");
		System.out.println("The longest distance is " + longestDist + ".");
		return dataset;
	}
	
	//Method that checks to make sure the current line segment is not breaking any rules of the assignment 
	public boolean validate(Line currentLine, int current) {
		
		//declare and initialize the instances needed for checking 
		Node crossNode = new Node(0,0); Node crossNode2 = new Node(1,1);
		double crossX = 0; double crossY = 0;
		int numCrosses = 0;
		
		//check to see if current line illegally intersects any other line segments inside the polygon 
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].getSlope() != currentLine.getSlope()) {
				
				//check if edge being compared to in a vertical line and get intersection point
				if (lines[i].getSlope() == Double.POSITIVE_INFINITY) {
					crossX = lines[i].getNodeStart().getX();
					crossY = (currentLine.getSlope()*crossX) + currentLine.getB();
				} else {
					crossX = (lines[i].getB()-currentLine.getB()) / (currentLine.getSlope()-lines[i].getSlope());
					crossY = currentLine.getSlope() * crossX + currentLine.getB();
				}
				
				//check how many intersections are occurring in the vicinity of the polygon 
				if (lines[i].getSlope() == Double.POSITIVE_INFINITY ) { //if current edge being checked is a vertical line
					if ((crossY > lines[i].getNodeStart().getY()) && (crossY < lines[i].getNodeEnd().getY())) {
						if (numCrosses == 0) {
							crossNode = new Node(crossX, crossY); //create intersection node
						} else if (numCrosses == 1) {
							crossNode2 = new Node(crossX, crossY); //create second intersection node
						}
						
						numCrosses++; //increment number of intersections
					} else if ((crossY < lines[i].getNodeStart().getY()) && (crossY > lines[i].getNodeEnd().getY())) {
						if (numCrosses == 0) {
							crossNode = new Node(crossX, crossY);
						} else if (numCrosses == 1) {
							crossNode2 = new Node(crossX, crossY);
						}
						
						numCrosses++;
					}
				} else {//if current edge being checked with is no a vertical line
					if ((crossX > lines[i].getNodeStart().getX()) && (crossX < lines[i].getNodeEnd().getX())) {
						if (numCrosses == 0) {
							crossNode = new Node(crossX, crossY);
						} else if (numCrosses == 1) {
							crossNode2 = new Node(crossX, crossY);
						}
						
						numCrosses++;
					} else if ((crossX < lines[i].getNodeStart().getX()) && (crossX > lines[i].getNodeEnd().getX())) {
						if (numCrosses == 0) {
							crossNode = new Node(crossX, crossY);
						} else if (numCrosses == 1) {
							crossNode2 = new Node(crossX, crossY);
						}
						
						numCrosses++;
					}
				}
			}
		}

		//final check to see if line is valid
		if (numCrosses == 0) {
			if (validateAngle(currentLine, current)) {
				return true;
			} else {
				return false;
			}
		} else if (numCrosses > 2) {
			return false;
		} else if (numCrosses == 1) {
			
			//check if intersection is admissible or not 
			//by checking if this intersection is the last intersection on the polygon
			if ((calcDistance(crossNode, currentLine.getNodeStart())) < currentLine.getLength()) {
				return false;
			} else {
				
				//if it is admissible, check if this path is longer than the longest path
				if (calcDistance(crossNode, currentLine.getNodeStart()) > longestDist) { 
					longestLine = new Line(currentLine.getNodeStart(), crossNode);
					longestDist = calcDistance(crossNode, currentLine.getNodeStart());
				}
				return true;
			}
			
		} else if (numCrosses == 2) {

			//check if both intersections are admissible or not
			//by checking if they are both on different boundaries of the polygon 
			if (calcDistance(crossNode2, crossNode) > currentLine.getLength()) {
				
				if (calcDistance(crossNode2, crossNode) > longestDist) {
					longestLine = new Line(crossNode, crossNode2);
					longestDist = calcDistance(crossNode2, crossNode);
				}
				
				return true;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	//Method to calculate distance between two points 
	public static double calcDistance(Node next, Node current) {
		return Math.sqrt((Math.pow((next.getX()-current.getX()), 2) + Math.pow((next.getY()-current.getY()), 2)));
	}
	
	//Method to get the angle between two specified lines that meet at the same origin
	public static double getAngle(double point1X, double point1Y, 
	    double point2X, double point2Y, 
	    double fixedX, double fixedY) {

	    double angle1 = Math.atan2(point1Y - fixedY, point1X - fixedX);
	    double angle2 = Math.atan2(point2Y - fixedY, point2X - fixedX);

	    return angle1 - angle2; 
	}
	
	//Method to check whether the current line being checked is fully enclosed by its surrounding adjacent edges
	public boolean validateAngle(Line currentLine, int i) {
		int j;
		
		if (i == 0) {
			j = lines.length-1;
		} else {
			j = i-1;
		}
		
		//check if current line is fully enclosed and return true or false
		if (Math.abs(getAngle(lines[i].getNodeEnd().getX(), lines[i].getNodeEnd().getY(), lines[j].getNodeStart().getX(), lines[j].getNodeStart().getY(), lines[i].getNodeStart().getX(), lines[i].getNodeStart().getY())) 
				> Math.abs(getAngle(lines[i].getNodeEnd().getX(), lines[i].getNodeEnd().getY(), currentLine.getNodeStart().getX(), currentLine.getNodeStart().getY(), lines[i].getNodeStart().getX(), lines[i].getNodeStart().getY()))) {
			return true;
		} else {
			return false;
		}
	}
	
	//Method to populate polygon with given points connected by lines 
	public static void popPolygon() {
		lines = new Line[nodes.length];
		
		for(int i = 0; i < nodes.length; i++) {
			if (i == (nodes.length-1)) {
				lines[i] = new Line(nodes[i], nodes[0]);
			} else {
				lines[i] = new Line(nodes[i], nodes[i+1]);
			}
			
			lines[i].genEquation(); //generate the equation variables of the line 
		}
	}
	
	//main method 
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		System.out.println("Please enter your number of coordinates followed by your coordinates");
		
		int n = input.nextInt();
		nodes = new Node[n];
		
		//get all points from the user
		for (int i = 0; i < n; i++) {
			int x = input.nextInt();
			int y = input.nextInt();
			nodes[i] = new Node(x,y);
		}
		
		input.close(); //close the scanner
		popPolygon(); //populate the polygon
		
		double startTime = System.currentTimeMillis(); //computational start time
		
		IslandConstruction chart = new IslandConstruction("Island Airport Construction",
				"Longest Path");
		chart.pack();          
		RefineryUtilities.centerFrameOnScreen(chart);          
		chart.setVisible(true);
		
		double endTime   = System.currentTimeMillis(); //computational end time
		double totalTime = endTime - startTime;
		System.out.println("Computational time = " + totalTime/1000 + " seconds."); //print total computational time 
	}

}