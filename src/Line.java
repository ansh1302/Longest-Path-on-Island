public class Line {
	
	//instance variables of the class
	private Node nodeStart;
	private Node nodeEnd;
	private double slope, bValue, length; 
	
	//Line Constructor 
	Line(Node node1, Node node2) {
		this.nodeStart = node1;
		this.nodeEnd = node2;
	}
	
	//All getter and setter methods
	public Node getNodeStart() {
		return this.nodeStart;
	}
	
	public Node getNodeEnd() {
		return this.nodeEnd;
	}
	
	public double getSlope() {
		return this.slope;
	}
	
	public double getB() {
		return this.bValue;
	}
	
	public double getLength() {
		return this.length;
	}
	
	public void setLength(double length) {
		this.length = length;
	}
	
	//Method to compute the values of the line variables 
	public void genEquation() {
		
		//Calculate the slope of the line
		if ((this.getNodeEnd().getX() - this.getNodeStart().getX()) == 0) {
			this.slope = Double.POSITIVE_INFINITY;
		} else {
			this.slope = ((double)this.getNodeEnd().getY() - (double)this.getNodeStart().getY())/
					((double)this.getNodeEnd().getX() - (double)this.getNodeStart().getX());
		}
		
		//Calculate the 'b' value of the line
		this.bValue = this.getNodeStart().getY() - this.slope*this.getNodeStart().getX();
	}
}