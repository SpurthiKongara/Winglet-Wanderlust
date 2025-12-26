
import java.util.*;

public class MainMenu {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<String, List<String>> userHistory = new HashMap<>();

        BookingService bookingService = new BookingService(userHistory);
        CancellationService cancellationService = new CancellationService(bookingService.getSeatsByDate(), userHistory);

        int choice;
        do {
            System.out.println("\n====== Flight Booking System ======");
            System.out.println("1. Book Seat");
            System.out.println("2. Cancel Seat");
            System.out.println("3. Check Available Seats");
            System.out.println("4. Change Seat");
            System.out.println("5. My History");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            while (!sc.hasNextInt()) {
                sc.nextLine();
                System.out.print("Please enter a valid number: ");
            }
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 ->
                    bookingService.bookSeat(sc);
                case 2 ->
                    cancellationService.cancelSeat(sc);
                case 3 -> {
                    System.out.print("Enter date to check availability (dd-MM-yyyy): ");
                    String date = sc.nextLine();
                    bookingService.showAvailability(date);
                }
                case 4 ->
                    bookingService.changeSeat(sc);
                case 5 ->
                    showHistory(sc, userHistory);
                case 6 ->
                    System.out.println("Exiting... Thank you!");
                default ->
                    System.out.println("❌ Invalid choice!");
            }
        } while (choice != 6);

        sc.close();
    }

    private static void showHistory(Scanner sc, Map<String, List<String>> userHistory) {
        System.out.print("Enter your name to view history: ");
        String name = sc.nextLine().toLowerCase();

        List<String> history = userHistory.get(name);
        if (history == null || history.isEmpty()) {
            System.out.println("📌 No history found for " + name + "!");
            return;
        }

        System.out.println("\n📖 Booking & Cancellation History:");
        int i = 1;
        for (String record : history) {
            System.out.println(i + ". " + record);
            i++;
        }
    }
}
