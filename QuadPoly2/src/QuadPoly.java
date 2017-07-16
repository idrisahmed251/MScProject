
public class QuadPoly {
	
	int one, two, three;
	
	public QuadPoly(int one, int two, int three) {
		this.one = one;
		this.two = two;
		this.three = three;
	}
	
	public QuadPoly(QuadPoly clone) {
		this.one = clone.one;
		this.two = clone.two;
		this.three = clone.three;
	}

	public String toString() {
		return new String(one + "x^2 + " + two + "x + " + three);
	}
	
	public void printTotals(QuadPoly quadPoly1, QuadPoly quadPoly2, QuadPoly total) {
		System.out.println("QuadPoly 1: " + quadPoly1 + " + ");
		System.out.println("QuadPoly 2: " + quadPoly2 + " = ");
		System.out.println(total);
	}

	public QuadPoly add(QuadPoly quadPoly1, QuadPoly quadPoly2) {
		return new QuadPoly(QuadPolyWorker.add(quadPoly1, quadPoly2));
	}

	public void compare(QuadPoly quadPoly) {
		int larger = QuadPolyWorker.compareQuadPoly(this, quadPoly);
		QuadPolyWorker.printComparison(this, quadPoly, larger);
	}
}
