
public class Rectangle {
	private Point diaglEnd1, diaglEnd2;
	
	public Rectangle(Point diaglEnd1, Point diaglEnd2) {
		this.diaglEnd1 = diaglEnd1;
		this.diaglEnd2 = diaglEnd2;
	}
	
	public Rectangle shift(Double xShift, Double yShift) {
		Point newX = new Point(diaglEnd1.x + xShift, diaglEnd2.x + yShift);
		Point newY = new Point(diaglEnd2.y + yShift, diaglEnd2.y + yShift);
		return new Rectangle(newX, newY);
	}
	
	public Double area() { 
		Double xLength = diaglEnd2.x - diaglEnd1.x;
		Double yLength = diaglEnd2.y - diaglEnd1.y;
		return (xLength * yLength);
	}
	
	public Double perimeter() { 
		Double lolX = (diaglEnd2.x - diaglEnd1.x) * 2;
		Double lolY = (diaglEnd2.y - diaglEnd1.y) * 2;
		return lolX + lolY;
	}
	
	public String toString() {
		String output = "Diag Corner 1: " + diaglEnd1 + ", Diag Corner 2: "
				+ diaglEnd2;
		return output;
	}
}
