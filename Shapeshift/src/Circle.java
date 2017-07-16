
public class Circle {
	
	private Point centre;
	private Double radius, diameter, circumference;
	
	public Circle(Point centre, Double radius) {
		this.centre = centre;
		this.radius = radius;
		setDiameter();
		setCircumference();
	}
	
	public Circle shift(Double xShift, Double yShift) {
		Point newCentre = new Point(centre.x + xShift, centre.y + yShift);
		Circle shiftedCircle = new Circle(newCentre, radius);
		return shiftedCircle;
	}
	
	public Double area() { return Math.PI * (radius * radius); }
	
	public Double Perimeter() { return circumference; }
	
	private void setDiameter() { this.diameter = this.radius / 2; }
	
	private void setCircumference() { this.circumference = Math.PI * diameter; }
	
	public String toString() {
		String output = "Centre is: " + centre;
		return output;
	}
	
}
