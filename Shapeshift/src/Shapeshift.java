import java.util.Scanner;

public class Shapeshift {

	public static Scanner inputScanner = new Scanner(System.in);
	
	private static Point inputPoint(String prompt) {
		System.out.print(prompt);
		double x = inputScanner.nextDouble();
		double y = inputScanner.nextDouble();
		return new Point(x, y);
	}
	
	private static Double xShift, yShift;
	
	private static void inputXYShifts() {
		System.out.print("Enter the offset as X Y: ");
		xShift = inputScanner.nextDouble();
		yShift = inputScanner.nextDouble();
	}
	
	public static void main(String[] args) {
		System.out.print("Choose circle (1), triangle (2), rectangle (3): ");
		int shapeChoice = inputScanner.nextInt();
		
		switch (shapeChoice) {
		case 1: //Circle
			Point centre = inputPoint("Enter the centre as X Y: ");
			System.out.print("Enter the Radius: ");
			double radius = inputScanner.nextDouble();
			Circle originalCircle = new Circle(centre, radius);
			inputXYShifts();
			
			Circle shiftedCircle = originalCircle.shift(xShift, yShift);
			System.out.println();
			System.out.println(originalCircle);
			System.out.println(" has area " + originalCircle.area() + ", perimeter "
					+ originalCircle.Perimeter());
			System.out.println("and when shifted by X offset " + xShift + " and Y offset"
					+ yShift + " gives");
			System.out.println(shiftedCircle);
			break;
		case 2: //Triangle
			Point pointA = inputPoint("Enter point A (must be 90 degree angle) as X Y: ");
			Point pointB = inputPoint("Enter point B as X Y: ");
			Point pointC = inputPoint("Enter point C as X Y: ");
			
			Triangle originalTriangle = new Triangle(pointA, pointB, pointC);
			inputXYShifts();
			Triangle shiftedTriangle = originalTriangle.shift(xShift, yShift);
			System.out.println();
			System.out.println(originalTriangle);
			System.out.println(" has area " + originalTriangle.area() + ", perimeter "
					+ originalTriangle.perimeter());
			System.out.println("and when shifted by X offset " + xShift + " and Y offset"
					+ yShift + " gives");
			System.out.println(shiftedTriangle);
			break;
		case 3: //Rectangle
			Point diaglEnd1 = inputPoint("Enter one corner as X Y: ");
			Point diaglEnd2 = inputPoint("Enter opposite corner as X Y: ");

			Rectangle originalRectangle = new Rectangle(diaglEnd1, diaglEnd2);
			inputXYShifts();
			Rectangle shiftedRectangle = originalRectangle.shift(xShift, yShift);
			System.out.println();
			System.out.println(originalRectangle);
			System.out.println(" has area " + originalRectangle.area() + ", perimeter "
					+ originalRectangle.perimeter());
			System.out.println("and when shifted by X offset " + xShift + " and Y offset"
					+ yShift + " gives");
			System.out.println(shiftedRectangle);
			break;
		default: System.out.println("That wasn't 1, 2, or 3");
		}
	}
}
