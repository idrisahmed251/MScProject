import java.util.Scanner;

public class AddQuadPoly {
	public static Scanner inputScanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		int[] input = getQuadPolyValues();
		
		QuadPoly quadPoly1 = new QuadPoly(input[0], input[1], input[2]);
		QuadPoly quadPoly2 = new QuadPoly(input[3], input[4], input[5]);
		
		int whatToDo;
		do {
			System.out.println("What would you like to do?");
			System.out.println("[1] add the two QuadPolys");
			System.out.println("[2] Find which QuadPoly is bigger");
			System.out.println("[3] exit");
			whatToDo = inputScanner.nextInt();
			if (whatToDo == 1) {
				QuadPoly total = new QuadPoly(quadPoly1);
				total = new QuadPoly(total.add(quadPoly1, quadPoly2));
				total.printTotals(quadPoly1, quadPoly2, total);
			} else if (whatToDo == 2)
				quadPoly1.compare(quadPoly2);	
		} while (whatToDo != 3);
		
		System.exit(0);
		
	}
	
	private static int[] getQuadPolyValues() {
		int[] input = new int[6];
		System.out.println("Please enter both quadpoly's in the form a b c d e f");
		System.out.println("a b and c will form the firt quadpoly as ax^2 + bx + c");
		System.out.println("d e and f will form the firt quadpoly as dx^2 + ex + f");
		
		for(int i = 0; i < 6; i++) { input[i] = inputScanner.nextInt(); }
		return input;
	}
}
