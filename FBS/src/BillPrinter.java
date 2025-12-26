
public class BillPrinter {

    public static void printBookingBill(Seat seat, int seatNo, String seatClass) {
        System.out.println("\n------ BOOKING BILL ------");
        System.out.println("Name        : " + seat.name);
        System.out.println("TicketID    : " + seat.ticketId);
        System.out.println("Date        : " + (seat.bookingDate != null ? seat.bookingDate : "N/A"));
        System.out.println("Source      : " + seat.source);
        System.out.println("Destination : " + seat.destination + " (Price: " + seat.destinationPrice + ")");
        System.out.println("Seat No     : " + seatNo + " (" + seatClass + ")");
        System.out.println("Seat Price  : " + seat.price);

        if (seat.discount > 0) {
            System.out.println("Discount    : -" + seat.discount + " (Frequent User 20%)");
        }

        if (seat.foodSelected) {
            System.out.println("Food        : " + seat.foodItem + " (" + seat.foodPrice + ")");
        }
        if (seat.wheelChair) {
            System.out.println("Wheelchair  : " + seat.wheelChairPrice);
        }

        System.out.println("---------------------------");
        System.out.println("Total       : " + seat.total);
        System.out.println("---------------------------");
    }

    public static void printCancellationBill(Seat seat, int seatNo, String seatClass) {
        System.out.println("\n------ CANCELLATION BILL ------");
        System.out.println("Name       : " + seat.name);
        System.out.println("TicketID   : " + seat.ticketId);
        System.out.println("Date       : " + (seat.bookingDate != null ? seat.bookingDate : "N/A"));
        System.out.println("Seat No    : " + seatNo + " (" + seatClass + ")");
        System.out.println("Original   : " + seat.total);
        System.out.println("Cancellation(20%)  : " + (seat.total * 0.20));
        System.out.println("Refund     : " + (seat.total * 0.80));
        System.out.println("---------------------------");
    }
}
