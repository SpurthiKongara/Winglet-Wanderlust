
public class Seat {

    boolean booked = false;
    String name;
    String dob;
    String phone;
    String ticketId;
    int price;           // Seat base price
    int discount;
    int total;           // Total including seat, destination, food, wheelchair
    String seatClass;
    boolean wheelChair = false;
    int wheelChairPrice = 0;
    boolean foodSelected = false;
    String foodType = ""; // Veg / Non-Veg
    String foodItem = "";
    int foodPrice = 0;

    // Destination
    String source = "Hyderabad";
    String destination;
    int destinationPrice = 0;

    // Booking date stored as "DD-10-2025"
    String bookingDate;

    public Seat(String name, String dob, String phone, String ticketId, int price, int discount,
            String seatClass, String destination, int destinationPrice, String bookingDate) {
        this.booked = true;
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.ticketId = ticketId;
        this.price = price;
        this.discount = discount;
        this.destination = destination;
        this.destinationPrice = destinationPrice;
        this.total = price + destinationPrice - discount;
        this.seatClass = seatClass;
        this.bookingDate = bookingDate;
    }

    public void cancel() {
        this.booked = false;
        this.ticketId = null;
        this.total = 0;
        this.foodSelected = false;
        this.foodType = "";
        this.foodItem = "";
        this.foodPrice = 0;
        this.wheelChair = false;
        this.wheelChairPrice = 0;
        this.destination = null;
        this.destinationPrice = 0;
        this.bookingDate = null;
    }
}
