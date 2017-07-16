import java.util.Scanner;

public class RoundPennies {
	
	public static Scanner inputScanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		System.out.println("How many pennies you got?");
		int numberOfPennies = inputScanner.nextInt();
		int numberOfPounds = (numberOfPennies + 50) / 100;
		System.out.println("You have £" + numberOfPounds + " ish");
	}
}
