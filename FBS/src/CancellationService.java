
import java.util.*;

public class CancellationService {

    private Map<String, Seat[]> seatsByDate;
    private Map<String, List<String>> userHistory;

    public CancellationService(Map<String, Seat[]> seatsByDate, Map<String, List<String>> userHistory) {
        this.seatsByDate = seatsByDate;
        this.userHistory = userHistory;
    }

    public void cancelSeat(Scanner sc) {
        System.out.print("Enter your name to cancel: ");
        String name = sc.nextLine();

        System.out.print("Enter booking date of the seat to cancel (dd-MM-yyyy): ");
        String bookingDate = sc.nextLine();

        Seat[] seatsForDate = seatsByDate.get(bookingDate);
        if (seatsForDate == null) {
            System.out.println("No bookings found for this date.");
            return;
        }

        System.out.print("Enter seat number to cancel: ");
        int seatNo;
        while (!sc.hasNextInt()) {
            sc.nextLine();
            System.out.print("Enter a number: ");
        }
        seatNo = sc.nextInt();
        sc.nextLine();

        if (seatNo < 1 || seatNo > seatsForDate.length) {
            System.out.println("❌ Invalid seat number!");
            return;
        }

        Seat seat = seatsForDate[seatNo - 1];
        if (seat == null || !seat.booked) {
            System.out.println("❌ Seat " + seatNo + " is not booked!");
            return;
        }

        if (!seat.name.equalsIgnoreCase(name)) {
            System.out.println("❌ You don’t have access to cancel this seat!");
            return;
        }

        System.out.println("\n✅ Seat " + seatNo + " cancelled successfully!");
        BillPrinter.printCancellationBill(seat, seatNo, seat.seatClass);

        String entry = String.format("%s -> CANCELLED | TicketID: %s, Seat: %d, Refund: %.2f",
                new Date(), seat.ticketId, seatNo, seat.total * 0.80);
        userHistory.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).add(entry);

        seat.cancel();
        seatsForDate[seatNo - 1] = null;
    }
}
