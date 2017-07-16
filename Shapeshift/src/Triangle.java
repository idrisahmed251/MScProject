
public class Triangle {
	private Point pointA, pointB, pointC;
	public Triangle(Point pointA, Point pointB, Point pointC) {
		this.pointA = pointA;
		this.pointB = pointB;
		this.pointC = pointC;
	}
	
	public Triangle  shift(Double xShift, Double yShift) {
		Point newA = new Point(pointA.x + xShift, pointA.y + yShift);
		Point newB = new Point(pointB.x + xShift, pointB.y + yShift);
		Point newC = new Point(pointC.x + xShift, pointC.y + yShift);
		return new Triangle(newA, newB, newC);
	}
	
	public Double area() { 
		Double xLength = pointB.x - pointA.x;
		Double yLength = pointC.y - pointA.y;
		return (xLength * yLength) / 2;
	}
	
	public Double perimeter() { 
		Double aToB = pointB.x - pointA.x;
		Double bToC = aToB + (pointC.y - pointB.y);
		Double cToA = pointC.y - pointA.y;
		return new Double(aToB + bToC + cToA);
	}
	
	public String toString() {
		String output = "Point A: " + pointA + ", Point B: " + pointB + ", pointC: "
				+ pointC;
		return output;
	}
	
}
