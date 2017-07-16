
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
	
	public void add(QuadPoly quadPoly) {
		this.one += quadPoly.one;
		this.two += quadPoly.two;
		this.three += quadPoly.three;
	}
	
	public String toString() {
		return new String(one + "x^2 + " + two + "x + " + three);
	}
}
