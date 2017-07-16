
public class QuadPolyWorker {
	
	public static QuadPoly add(QuadPoly quadPoly1, QuadPoly quadPoly2) {
		QuadPoly total = new QuadPoly(quadPoly1);
		total.one += quadPoly2.one;
		total.two += quadPoly2.two;
		total.three += quadPoly2.three;
		return total;
	}
	
	public static int compareQuadPoly(QuadPoly quadPoly1, QuadPoly quadPoly2) {
		if (quadPoly1.one > quadPoly2.one)
			return 1;
		else if (quadPoly1.one == quadPoly2.one && quadPoly1.two > quadPoly2.two) 
			return 1;
		else if (quadPoly1.one == quadPoly2.one && quadPoly1.two == quadPoly2.two
				&& quadPoly1.three > quadPoly2.three)
			return 1;
		else if (quadPoly1.one == quadPoly2.one && quadPoly1.two == quadPoly2.two
				&& quadPoly1.three == quadPoly2.three)
			return 0;
		else return 2;
	}
	
	public static void printComparison(QuadPoly one, QuadPoly two, int whichIsLarger) {
		if (whichIsLarger == 0)
			System.out.println(one + " is the exact same as " + two);
		else if (whichIsLarger == 1)
			System.out.println(one + " is larger than " + two);
		else
			System.out.println(two + " is larger than " + one);
	}

}
