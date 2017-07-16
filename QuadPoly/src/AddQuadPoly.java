import java.util.Scanner;

public class AddQuadPoly {
	public static Scanner inputScanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		int[] input = getInput();
		
		QuadPoly quadPoly1 = new QuadPoly(input[0], input[1], input[2]);
		QuadPoly quadPoly2 = new QuadPoly(input[3], input[4], input[5]);
		QuadPoly total = new QuadPoly(quadPoly1);
		total.add(quadPoly2);
		
		printTotals(quadPoly1, quadPoly2, total);
	}
	
	private static int[] getInput() {
		int[] input = new int[6];
		System.out.println("Please enter both quadpoly's in the form a b c d e f");
		System.out.println("a b and c will form the firt quadpoly as ax^2 + bx + c");
		System.out.println("d e and f will form the firt quadpoly as dx^2 + ex + f");
		
		for(int i = 0; i < 6; i++) { input[i] = inputScanner.nextInt(); }
		return input;
	}
	
	private static void printTotals(QuadPoly quadPoly1, QuadPoly quadPoly2, QuadPoly total) {
		System.out.println("QuadPoly 1: " + quadPoly1 + " + ");
		System.out.println("QuadPoly 2: " + quadPoly2 + " = ");
		System.out.println(total);
	}
}
