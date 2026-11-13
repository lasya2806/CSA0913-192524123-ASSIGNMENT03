
import java.util.Scanner;
import java.io.FileNotFoundException;

public class ATMSimulation {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        double balance = 5000;

        try {
            System.out.print("Enter Account Number: ");
            int account = sc.nextInt();

            System.out.print("Enter Withdrawal Amount: ");
            double amount = sc.nextDouble();

            if (amount < 0) {
                throw new IllegalArgumentException(
                    "Negative amount is not allowed");
            }

            if (amount > balance) {
                throw new ArithmeticException(
                    "Insufficient balance");
            }

            balance = balance - amount;

            System.out.println("Withdrawal Successful!");
            System.out.println("Remaining Balance: " + balance);

        } catch (java.util.InputMismatchException e) {
            System.out.println("Error: Please enter numbers only.");

        } catch (IllegalArgumentException |
                 ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());

        } finally {
            System.out.println("ATM Transaction Completed.");
            sc.close();
        }
    }
}

