
import java.text.*;
import java.util.*;

public class BookingService {

    private Map<String, Seat[]> seatsByDate = new HashMap<>();
    private Map<String, List<String>> userHistory;
    private Map<String, Integer> userBookingCount = new HashMap<>();

    private final String[] destinations = {"Mumbai", "Delhi", "Chennai", "Bengaluru", "Kolkata"};
    private final int[] destinationPrices = {500, 700, 400, 450, 800};
    private int ticketCounter = 1000;

    public BookingService(Map<String, List<String>> userHistory) {
        this.userHistory = userHistory;
    }

    private Seat[] getSeatsForDate(String date) {
        Seat[] datedSeats = seatsByDate.get(date);
        if (datedSeats == null) {
            datedSeats = new Seat[10];
            seatsByDate.put(date, datedSeats);
        }
        return datedSeats;
    }

    public void bookSeat(Scanner sc) {
        System.out.println("\n--- Book Seat ---");
        System.out.println("1. 1-Way");
        System.out.println("2. 2-Way");
        System.out.print("Enter choice: ");
        int choice;
        while (!sc.hasNextInt()) {
            sc.nextLine();
            System.out.print("Enter 1 or 2: ");
        }
        choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            bookOneWay(sc);
        } else if (choice == 2) {
            bookTwoWay(sc);
        } else {
            System.out.println("❌ Invalid choice!");
        }
    }

    private void bookOneWay(Scanner sc) {
        System.out.println("Enter your details:");
        System.out.print("Name: ");
        String name = sc.nextLine();
        String userKey = name.toLowerCase();

        String dob;
        while (true) {
            System.out.print("DOB (dd-MM-yyyy): ");
            dob = sc.nextLine();
            if (validateDOB(dob)) {
                break;
            }
            System.out.println("❌ Invalid DOB format!");
        }

        String phone;
        while (true) {
            System.out.print("Phone Number (10 digits): ");
            phone = sc.nextLine();
            if (validatePhone(phone)) {
                break;
            }
            System.out.println("❌ Invalid phone number!");
        }

        String bookingDate;
        while (true) {
            System.out.print("Enter travel date (dd-MM-yyyy): ");
            bookingDate = sc.nextLine();
            if (validateFutureDate(bookingDate)) {
                break;
            }
            System.out.println("❌ Invalid date! You can only book for future dates.");
        }

        System.out.println("\nSelect Destination from Hyderabad:");
        for (int i = 0; i < destinations.length; i++) {
            System.out.println((i + 1) + ". " + destinations[i] + " (Price: " + destinationPrices[i] + ")");
        }
        int destChoice;
        while (true) {
            System.out.print("Enter destination number: ");
            if (sc.hasNextInt()) {
                destChoice = sc.nextInt();
                sc.nextLine();
                if (destChoice >= 1 && destChoice <= destinations.length) {
                    break;
                }
            } else {
                sc.nextLine();
            }
            System.out.println("❌ Invalid choice!");
        }
        String destination = destinations[destChoice - 1];
        int destinationPrice = destinationPrices[destChoice - 1];

        Seat[] seatsForDate = getSeatsForDate(bookingDate);
        showAvailability(bookingDate);
        int seatNo;
        while (true) {
            System.out.print("Enter seat number to book: ");
            if (sc.hasNextInt()) {
                seatNo = sc.nextInt();
                sc.nextLine();
                if (seatNo < 1 || seatNo > seatsForDate.length) {
                    System.out.println("❌ Invalid seat number!");
                } else if (seatsForDate[seatNo - 1] != null && seatsForDate[seatNo - 1].booked) {
                    System.out.println("❌ Seat already booked!");
                    showAvailability(bookingDate);
                } else {
                    break;
                }
            } else {
                sc.nextLine();
                System.out.println("❌ Please enter a number.");
            }
        }

        String seatClass = getSeatClass(seatNo);
        int price = getSeatPrice(seatNo);
        int previousBookings = userBookingCount.getOrDefault(userKey, 0);
        boolean eligibleForDiscount = previousBookings >= 1;
        int discount = eligibleForDiscount ? (int) (price * 0.20) : 0;

        if (eligibleForDiscount) {
            System.out.println("🎉 You’re eligible for a 20% loyalty discount!");
        }

        String ticketId = "T" + (++ticketCounter);
        Seat seat = new Seat(name, dob, phone, ticketId, price, discount, seatClass, destination, destinationPrice,
                bookingDate);
        seatsForDate[seatNo - 1] = seat;

        specialAssistanceForBooking(sc, seat, seatNo, name);

        System.out.println("\n✅ Seat booked successfully!");
        BillPrinter.printBookingBill(seat, seatNo, seat.seatClass);
        addHistory(seat, "BOOKED", seatNo);
        userBookingCount.put(userKey, previousBookings + 1);
    }

    private void bookTwoWay(Scanner sc) {
        System.out.println("Enter your details for DEPARTURE trip:");
        System.out.print("Name: ");
        String name = sc.nextLine();
        String userKey = name.toLowerCase();

        String dob;
        while (true) {
            System.out.print("DOB (dd-MM-yyyy): ");
            dob = sc.nextLine();
            if (validateDOB(dob)) {
                break;
            }
            System.out.println("❌ Invalid DOB format!");
        }

        String phone;
        while (true) {
            System.out.print("Phone Number (10 digits): ");
            phone = sc.nextLine();
            if (validatePhone(phone)) {
                break;
            }
            System.out.println("❌ Invalid phone number!");
        }

        int previousBookings = userBookingCount.getOrDefault(userKey, 0);
        boolean eligibleForFrequentDiscount = previousBookings >= 1;
        if (eligibleForFrequentDiscount) {
            System.out.println("🎉 You’re eligible for a 20% frequent user discount on both legs!");
        }

        // ------- Departure -------
        System.out.println("\nDeparture Trip:");
        String departureDate;
        while (true) {
            System.out.print("Enter departure date (dd-MM-yyyy): ");
            departureDate = sc.nextLine();
            if (validateFutureDate(departureDate)) {
                break;
            }
            System.out.println("❌ Invalid date! Must be after today.");
        }

        System.out.println("\nSelect Destination from Hyderabad:");
        for (int i = 0; i < destinations.length; i++) {
            System.out.println((i + 1) + ". " + destinations[i] + " (Price: " + destinationPrices[i] + ")");
        }
        int destChoice;
        while (true) {
            System.out.print("Enter destination number: ");
            if (sc.hasNextInt()) {
                destChoice = sc.nextInt();
                sc.nextLine();
                if (destChoice >= 1 && destChoice <= destinations.length) {
                    break;
                }
            } else {
                sc.nextLine();
            }
            System.out.println("❌ Invalid choice!");
        }
        String departureDestination = destinations[destChoice - 1];
        int departureDestPrice = destinationPrices[destChoice - 1];

        Seat[] depSeats = getSeatsForDate(departureDate);
        showAvailability(departureDate);
        int depSeatNo;
        while (true) {
            System.out.print("Enter seat number to book for Departure: ");
            if (sc.hasNextInt()) {
                depSeatNo = sc.nextInt();
                sc.nextLine();
                if (depSeatNo >= 1 && depSeatNo <= depSeats.length
                        && (depSeats[depSeatNo - 1] == null || !depSeats[depSeatNo - 1].booked)) {
                    break;
                } else {
                    System.out.println("❌ Seat already booked or invalid!");
                }
            } else {
                sc.nextLine();
            }
        }

        String depSeatClass = getSeatClass(depSeatNo);
        int depPrice = getSeatPrice(depSeatNo);
        int depDiscount = eligibleForFrequentDiscount ? (int) (depPrice * 0.20) : 0;

        String depTicketId = "T" + (++ticketCounter);
        Seat departureSeat = new Seat(name, dob, phone, depTicketId, depPrice, depDiscount, depSeatClass,
                departureDestination, departureDestPrice, departureDate);
        depSeats[depSeatNo - 1] = departureSeat;
        specialAssistanceForBooking(sc, departureSeat, depSeatNo, name);

        // ------- Return -------
        System.out.println("\n--- Return Trip Details ---");
        String returnSource = departureDestination;
        String returnDestination = "Hyderabad";

        String returnDate;
        while (true) {
            System.out.print("Enter return date (dd-MM-yyyy): ");
            returnDate = sc.nextLine();
            if (validateFutureDate(returnDate)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date dep = sdf.parse(departureDate);
                    Date ret = sdf.parse(returnDate);
                    if (ret.after(dep)) {
                        break;
                    } else {
                        System.out.println("❌ Return date must be after departure date.");
                    }
                } catch (Exception e) {
                    System.out.println("❌ Invalid date format.");
                }
            } else {
                System.out.println("❌ Invalid date! Must be after today.");
            }
        }

        Seat[] retSeats = getSeatsForDate(returnDate);
        showAvailability(returnDate);
        int retSeatNo;
        while (true) {
            System.out.print("Enter seat number to book for Return: ");
            if (sc.hasNextInt()) {
                retSeatNo = sc.nextInt();
                sc.nextLine();
                if (retSeatNo >= 1 && retSeatNo <= retSeats.length
                        && (retSeats[retSeatNo - 1] == null || !retSeats[retSeatNo - 1].booked)) {
                    break;
                } else {
                    System.out.println("❌ Seat already booked or invalid!");
                }
            } else {
                sc.nextLine();
            }
        }

        String retSeatClass = getSeatClass(retSeatNo);
        int retPrice = getSeatPrice(retSeatNo);
        int retDiscount = eligibleForFrequentDiscount ? (int) (retPrice * 0.20) : 0;

        String retTicketId = "T" + (++ticketCounter);
        Seat returnSeat = new Seat(name, dob, phone, retTicketId, retPrice, retDiscount, retSeatClass, returnDestination,
                departureDestPrice, returnDate);
        returnSeat.source = returnSource;
        retSeats[retSeatNo - 1] = returnSeat;
        specialAssistanceForBooking(sc, returnSeat, retSeatNo, name);

        System.out.println("\n✅ 2-Way Booking Successful! Final Bill:");
        System.out.println("\n--- Departure Trip ---");
        BillPrinter.printBookingBill(departureSeat, depSeatNo, departureSeat.seatClass);
        System.out.println("\n--- Return Trip ---");
        BillPrinter.printBookingBill(returnSeat, retSeatNo, returnSeat.seatClass);

        int grandTotal = departureSeat.total + returnSeat.total;
        double twoWayDiscount = grandTotal * 0.10;
        double finalTotal = grandTotal - twoWayDiscount;

        System.out.println("---------------------------");
        System.out.println("Subtotal (Before 2-Way Discount): " + grandTotal);
        System.out.printf("2-Way Discount (10%%): -%.2f\n", twoWayDiscount);
        System.out.printf("Grand Total (After All Discounts): %.2f\n", finalTotal);
        System.out.println("---------------------------");

        addHistory(departureSeat, "BOOKED (Departure)", depSeatNo);
        addHistory(returnSeat, "BOOKED (Return)", retSeatNo);
        userBookingCount.put(userKey, previousBookings + 1);
    }

    private void specialAssistanceForBooking(Scanner sc, Seat seat, int seatNo, String name) {
        while (true) {
            System.out.println("\n--- Special Assistance Menu ---");
            System.out.println("1. Food");
            System.out.println("2. Wheelchair Service");
            System.out.println("3. Exit Assistance");
            System.out.print("Enter your choice: ");
            int choice;
            while (!sc.hasNextInt()) {
                sc.nextLine();
                System.out.print("Enter 1,2 or 3: ");
            }
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    if (seat.foodSelected) {
                        System.out.println("❌ Already selected food.");
                    } else {
                        foodMenu(sc, seat, seatNo, name);
                    }
                    break;
                case 2:
                    if (seat.wheelChair) {
                        System.out.println("❌ Already booked wheelchair service.");
                    } else {
                        int wheelPrice = 150;
                        System.out.println("Wheelchair Service Price: " + wheelPrice);
                        seat.wheelChair = true;
                        seat.wheelChairPrice = wheelPrice;
                        seat.total += wheelPrice;
                        System.out.println("✅ Wheelchair service added.");
                        BillPrinter.printBookingBill(seat, seatNo, seat.seatClass);
                        addHistory(seat, "WHEELCHAIR ADDED", seatNo);
                    }
                    break;
                case 3:
                    return;
                default:
                    System.out.println("❌ Invalid choice!");
            }
        }
    }

    private void foodMenu(Scanner sc, Seat seat, int seatNo, String name) {
        System.out.println("Select Food Type:");
        System.out.println("1. Veg");
        System.out.println("2. Non-Veg");
        System.out.print("Choice: ");
        int type;
        while (!sc.hasNextInt()) {
            sc.nextLine();
            System.out.print("Enter 1 or 2: ");
        }
        type = sc.nextInt();
        sc.nextLine();

        if (type == 1) {
            System.out.println("Veg Menu:");
            System.out.println("1. Mushroom Biryani - 200");
            System.out.println("2. Ulavacharu Biryani - 150");
            System.out.println("3. Veg Pulav - 100");
            System.out.println("4. Paneer Tikka - 300");
            System.out.println("5. Jackfruit Pulav - 250");
            System.out.print("Select item: ");
            int item;
            while (!sc.hasNextInt()) {
                sc.nextLine();
                System.out.print("Enter item number: ");
            }
            item = sc.nextInt();
            sc.nextLine();
            switch (item) {
                case 1 -> {
                    seat.foodItem = "Mushroom Biryani";
                    seat.foodPrice = 200;
                }
                case 2 -> {
                    seat.foodItem = "Ulavacharu Biryani";
                    seat.foodPrice = 150;
                }
                case 3 -> {
                    seat.foodItem = "Veg Pulav";
                    seat.foodPrice = 100;
                }
                case 4 -> {
                    seat.foodItem = "Paneer Tikka";
                    seat.foodPrice = 300;
                }
                case 5 -> {
                    seat.foodItem = "Jackfruit Pulav";
                    seat.foodPrice = 250;
                }
                default -> {
                    System.out.println("❌ Invalid choice!");
                    return;
                }
            }
        } else if (type == 2) {
            System.out.println("Non-Veg Menu:");
            System.out.println("1. Chicken - 200");
            System.out.println("2. Mutton - 500");
            System.out.println("3. Fish - 450");
            System.out.println("4. Prawn - 550");
            System.out.println("5. Crab - 600");
            System.out.print("Select item: ");
            int item;
            while (!sc.hasNextInt()) {
                sc.nextLine();
                System.out.print("Enter item number: ");
            }
            item = sc.nextInt();
            sc.nextLine();
            switch (item) {
                case 1 -> {
                    seat.foodItem = "Chicken";
                    seat.foodPrice = 200;
                }
                case 2 -> {
                    seat.foodItem = "Mutton";
                    seat.foodPrice = 500;
                }
                case 3 -> {
                    seat.foodItem = "Fish";
                    seat.foodPrice = 450;
                }
                case 4 -> {
                    seat.foodItem = "Prawn";
                    seat.foodPrice = 550;
                }
                case 5 -> {
                    seat.foodItem = "Crab";
                    seat.foodPrice = 600;
                }
                default -> {
                    System.out.println("❌ Invalid choice!");
                    return;
                }
            }
        } else {
            System.out.println("❌ Invalid type choice!");
            return;
        }

        seat.foodSelected = true;
        seat.total += seat.foodPrice;
        System.out.println("✅ Food added successfully: " + seat.foodItem);
        BillPrinter.printBookingBill(seat, seatNo, seat.seatClass);
        addHistory(seat, "FOOD ADDED", seatNo);
    }

    public void changeSeat(Scanner sc) {
        System.out.print("Enter your booking date (dd-MM-yyyy): ");
        String bookingDate = sc.nextLine();

        Seat[] seatsForDate = seatsByDate.get(bookingDate);
        if (seatsForDate == null) {
            System.out.println("No bookings found for this date.");
            return;
        }

        System.out.print("Enter your name to change seat: ");
        String name = sc.nextLine();

        System.out.print("Enter your current seat number: ");
        int currentSeatNo;

        Seat currentSeat = null;

        while (true) {
            if (sc.hasNextInt()) {
                currentSeatNo = sc.nextInt();
                sc.nextLine();
                if (currentSeatNo >= 1 && currentSeatNo <= seatsForDate.length) {
                    Seat seat = seatsForDate[currentSeatNo - 1];
                    if (seat != null && seat.booked && seat.name.equalsIgnoreCase(name)) {
                        currentSeat = seat;
                        break;
                    } else {
                        System.out.println("❌ No booking found under your name at seat " + currentSeatNo);
                        return;
                    }
                }
            } else {
                sc.nextLine();
            }
            System.out.print("Please enter a valid seat number: ");
        }

        if (currentSeat == null) {
            System.out.println("❌ No booked seat found!");
            return;
        }

        showAvailability(bookingDate);

        int newSeatNo;
        while (true) {
            System.out.print("Enter new seat number to change to: ");
            if (sc.hasNextInt()) {
                newSeatNo = sc.nextInt();
                sc.nextLine();
                if (newSeatNo < 1 || newSeatNo > seatsForDate.length) {
                    System.out.println("❌ Invalid seat number!");
                } else if (seatsForDate[newSeatNo - 1] != null && seatsForDate[newSeatNo - 1].booked) {
                    System.out.println("❌ Seat already booked!");
                    showAvailability(bookingDate);
                } else {
                    break;
                }
            } else {
                sc.nextLine();
                System.out.println("❌ Please enter a number.");
            }
        }

        currentSeat.seatClass = getSeatClass(newSeatNo);
        currentSeat.price = getSeatPrice(newSeatNo);
        currentSeat.total = currentSeat.price + currentSeat.destinationPrice;
        if (currentSeat.foodSelected) {
            currentSeat.total += currentSeat.foodPrice;
        }
        if (currentSeat.wheelChair) {
            currentSeat.total += currentSeat.wheelChairPrice;
        }

        seatsForDate[newSeatNo - 1] = currentSeat;
        seatsForDate[currentSeatNo - 1] = null;

        System.out.println("✅ Seat changed successfully from " + currentSeatNo + " to " + newSeatNo);
        BillPrinter.printBookingBill(currentSeat, newSeatNo, currentSeat.seatClass);
        addHistory(currentSeat, "SEAT CHANGED", newSeatNo);
    }

    private void addHistory(Seat seat, String action, int seatNo) {
        String entry = String.format("%s -> %s | TicketID: %s, Seat: %d, Class: %s, Destination: %s, Date: %s, Total: %d",
                new Date(), action, seat.ticketId, seatNo, seat.seatClass, seat.destination, seat.bookingDate, seat.total);
        userHistory.computeIfAbsent(seat.name.toLowerCase(), k -> new ArrayList<>()).add(entry);
    }

    private boolean validateDOB(String dob) {
        try {
            new SimpleDateFormat("dd-MM-yyyy").parse(dob);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateFutureDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            Date enteredDate = sdf.parse(dateStr);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return enteredDate.after(today.getTime()); // must be strictly after today
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validatePhone(String phone) {
        return phone.matches("\\d{10}");
    }

    public void showAvailability(String date) {
        Seat[] seatsForDate = getSeatsForDate(date);

        System.out.println("\n📌 Seat Status for " + date + ":");
        for (int i = 0; i < seatsForDate.length; i++) {
            if (seatsForDate[i] != null && seatsForDate[i].booked) {
                System.out.println("Seat " + (i + 1) + " -> Booked (TicketID: " + seatsForDate[i].ticketId + ", Class: " + seatsForDate[i].seatClass + ", Price: " + seatsForDate[i].price + ")");
            } else {
                System.out.println("Seat " + (i + 1) + " -> Available (Class: " + getSeatClass(i + 1) + ", Price: " + getSeatPrice(i + 1) + ")");
            }
        }
    }

    private String getSeatClass(int seatNo) {
        if (seatNo >= 1 && seatNo <= 3) {
            return "Economy";
        }
        if (seatNo >= 4 && seatNo <= 6) {
            return "Premium Economy";
        }
        if (seatNo >= 7 && seatNo <= 8) {
            return "First Class";
        }
        return "Business";
    }

    private int getSeatPrice(int seatNo) {
        if (seatNo >= 1 && seatNo <= 3) {
            return 500;
        }
        if (seatNo >= 4 && seatNo <= 6) {
            return 1000;
        }
        if (seatNo >= 7 && seatNo <= 8) {
            return 1500;
        }
        return 2000;
    }

    public Map<String, Seat[]> getSeatsByDate() {
        return seatsByDate;
    }

}
