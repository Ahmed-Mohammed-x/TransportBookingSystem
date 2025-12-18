package transport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class BookingSystem {

    private List<Transport> allTransports;
    private List<Booking> bookings;
    private TripSearcher tripSearcher;
    private Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //10 german cities to work with
    private static final String[] GERMAN_CITIES = {
            "Berlin", "Munich", "Hamburg", "Frankfurt", "Cologne",
            "Stuttgart", "Düsseldorf", "Dortmund", "Essen", "Leipzig"
    };

    public BookingSystem() {
        this.bookings = new ArrayList<>();
        this.scanner = new Scanner(System.in);

        try {
            System.out.println("🚆 loading transport database..");
            this.allTransports = DatabaseManager.loadTransports();
            this.tripSearcher = new TripSearcher(allTransports);
            System.out.println("✅ Loaded " + allTransports.size() + " trips successfully!\n");
        } catch (Exception e) {
            System.err.println("❌ Error loading database: " + e.getMessage());
            System.exit(1);
        }
    }

    public void start() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("          GERMAN TRANSPORT BOOKING SYSTEM                  ");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  Connecting 10 major German cities with train, bus, & flight");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        boolean running = true;
        while (running) {
            showMenu();

            //Switch concept instead of if else :)
            int choice = getIntInput("Enter your choice (1-4): ", 1, 4);

            try {
                switch (choice) {
                    case 1:
                        searchAndBook();
                        break;
                    case 2:
                        viewBookings();
                        break;
                    case 3:
                        cancelBooking();
                        break;
                    case 4:
                        System.out.println("\n Thank you for using German Transport Booking System!");
                        System.out.println("Safe travels! ️\n");
                        running = false;
                        break;
                }
            } catch (BookingException e) {
                System.out.println("\n❌ " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }

    private void showMenu() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                      MAIN MENU                            ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  1. 🔍 Search & Book Trip                                 ║");
        System.out.println("║  2. 📋 View My Bookings                                   ║");
        System.out.println("║  3. ❌ Cancel Booking                                     ║");
        System.out.println("║  4. 🚪 Exit                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private void searchAndBook() throws BookingException {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("                  🔍 SEARCH FOR TRIPS");
        System.out.println("═══════════════════════════════════════════════════════════");

        // Show available cities
        System.out.println("\n📍 Available Cities:");
        for (int i = 0; i < GERMAN_CITIES.length; i++) {
            System.out.printf("   %2d. %s\n", i + 1, GERMAN_CITIES[i]);
        }

        //get departure city
        System.out.println();
        int depChoice = getIntInput("Select departure city (1-10): (Berlin Recommended) ", 1, 10);
        String departure = GERMAN_CITIES[depChoice - 1];

        //get arrival city
        int arrChoice = getIntInput("Select arrival city (1-10): (Munich Recommended) ", 1, 10);
        while (arrChoice == depChoice) {
            System.out.println("⚠️  Arrival city must be different from departure city!");
            arrChoice = getIntInput("Select arrival city (1-10): ", 1, 10);
        }
        String arrival = GERMAN_CITIES[arrChoice - 1];

        // Get date
        System.out.println("\n📅 Enter travel date (format:eg 2025-12-21)/ recommended:");
        System.out.print("   Date: ");
        String dateStr = scanner.nextLine().trim();
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException("Invalid date format. Use YYYY-MM-DD please");
        }

        //Search trips (with multithreading)
        System.out.println("\n🔍 Searching for trips from " + departure + " to " + arrival + " on " + date + "...");
        List<Transport> results = tripSearcher.searchTrips(departure, arrival, date);

        if (results.isEmpty()) {
            System.out.println("\n⚠️  No trips found for this route and date.\n");
            return;
        }

        // Display results
        System.out.println("\n✅ Found " + results.size() + " trip(s):\n");
        System.out.println("═══════════════════════════════════════════════════════════");
        for (int i = 0; i < results.size(); i++) {
            Transport t = results.get(i);
            System.out.printf("%2d. %s\n", i + 1, t.toString());
            System.out.println("-----------------------------------------------------------");
        }

        //Select trip
        int tripChoice = getIntInput("\nSelect trip number (or 0 to cancel): ", 0, results.size());
        if (tripChoice == 0) {
            System.out.println("Booking cancelled.\n");
            return;
        }

        Transport selectedTrip = results.get(tripChoice - 1);

        //Number of Passengers
        int passengerCount = getIntInput("\nNumber of passengers (1-10): ", 1, 10);

        // Class selection
        System.out.println("\n🎫 Select class:");
        System.out.println("  1. Standard/2nd Class (€" + String.format("%.2f", selectedTrip.getStandardPrice()) + " per person!)");
        System.out.println("  2. Premium/1st Class (€" + String.format("%.2f", selectedTrip.getPremiumPrice()) + " per person!)");
        int classChoice = getIntInput("Your choice? (1-2): ", 1, 2);
        boolean isPremium = (classChoice == 2);

        // Check availability
        int availableSeats = selectedTrip.getAvailableSeats(isPremium);
        if (availableSeats < passengerCount) {
            throw new BookedSeatsException(
                    String.format("Not enough seats. Need %d, but only %d available in %s class.",
                            passengerCount, availableSeats, isPremium ? "premium" : "standard"));
        }

        // Get Client name
        System.out.print("\n👤 Enter Client name: ");
        String passengerName = scanner.nextLine().trim();
        if (passengerName.isEmpty()) {
            throw new InvalidInputException("Client name cannot be empty.");
        }

        // Seat selection
        System.out.println("\n💺 Seat Selection:");

        //Determine if seat reservation costs extra
        boolean seatReservationCostsExtra = false;
        double reservationFee = 0.0;

        if (selectedTrip.getType().equals("TRAIN") && !isPremium) {
            //Train 2nd class: Seat reservation costs €5 extra
            seatReservationCostsExtra = true;
            reservationFee = 5.0;
            System.out.println("   1. 💺 Reserve specific seat (+€" + String.format("%.2f", reservationFee) + " per person)");
            System.out.println("   2. 🤖 No reservation (free seating, sit anywhere available)");
        } else if (selectedTrip.getType().equals("FLIGHT") && !isPremium) {
            //Flight 2nd/economy class: Seat selection costs €5 extra
            seatReservationCostsExtra = true;
            reservationFee = 5.0;
            System.out.println("   1. 💺 Select your seat (+€" + String.format("%.2f", reservationFee) + " per person)");
            System.out.println("   2. 🤖 Random seat assignment (no extra charge)");
        } else {
            // Bus or Premium class: Seat reservation included
            System.out.println("   1. 💺 Choose your seat (included in ticket price)");
            System.out.println("   2. 🤖 Auto-assign seat (included in ticket price)");
        }

        int seatChoice = getIntInput("Your choice (1-2): ", 1, 2);

        List<Seat> allocatedSeats;
        boolean paidForReservation = false;

        if (seatChoice == 1) {
            paidForReservation = true;
            //Users choose seats manually
            allocatedSeats = manualSeatSelection(selectedTrip, passengerCount, isPremium);
        } else {
            //Auto Assign seats
            allocatedSeats = SeatAllocator.allocateSeats(selectedTrip, passengerCount, isPremium);
            System.out.println("\n✅ Auto-assigned seats: ");
            for (Seat seat : allocatedSeats) {
                System.out.println("   • " + seat.getSeatNumber() + " (" + seat.getPosition() + ", Row " + seat.getRow() + ")");
            }
        }

        // Calc total price with fees
        double basePrice = selectedTrip.getPrice(isPremium) * passengerCount;
        double totalReservationFee = 0.0;

        if (seatReservationCostsExtra && paidForReservation) {
            totalReservationFee = reservationFee * passengerCount;
        }

        double totalPrice = basePrice + totalReservationFee;

        // Booking preview and confirmation
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              📋 BOOKING PREVIEW                           ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.printf("║  Passenger:    %-42s ║\n", passengerName);
        System.out.printf("║  Transport:    %-42s ║\n", selectedTrip.getDisplayType());
        System.out.printf("║  Route:        %-42s ║\n", selectedTrip.getDeparture() + " → " + selectedTrip.getArrival());
        System.out.printf("║  Date:         %-42s ║\n", selectedTrip.getDate());
        System.out.printf("║  Time:         %-42s ║\n", selectedTrip.getDepartureTime() + " - " + selectedTrip.getArrivalTime());
        System.out.printf("║  Duration:     %-42s ║\n", selectedTrip.getDurationString());
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.printf("║  Class:        %-42s ║\n", isPremium ? "Premium/1st Class" : "Standard/2nd Class");
        System.out.printf("║  Passengers:   %-42d ║\n", passengerCount);

        // Format seat list
        StringBuilder seatList = new StringBuilder();
        for (int i = 0; i < allocatedSeats.size(); i++) {
            seatList.append(allocatedSeats.get(i).getSeatNumber());
            if (i < allocatedSeats.size() - 1) seatList.append(", ");
        }
        System.out.printf("║  Seats:        %-42s ║\n", seatList.toString());

        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.printf("║  Ticket Price: €%-41.2f ║\n", basePrice);

        if (totalReservationFee > 0) {
            System.out.printf("║  Seat Reservation Fee: €%-33.2f ║\n", totalReservationFee);
            System.out.println("║  ─────────────────────────────────────────────────────── ║");
        }

        System.out.printf("║  Total Price:  €%-41.2f ║\n", totalPrice);
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        // Confirmation prompt
        System.out.print("\n❓ Confirm booking? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes") && !confirmation.equals("y")) {
            System.out.println("\n❌ Booking cancelled.\n");
            return;
        }

        // Create booking
        Booking booking = new Booking(selectedTrip, allocatedSeats, passengerCount, passengerName, isPremium);
        bookings.add(booking);

        // Show confirmation
        System.out.println("\n✅ BOOKING CONFIRMED!\n");
        System.out.println(booking);
        System.out.println();
    }


    private List<Seat> manualSeatSelection(Transport transport, int count, boolean premium)
            throws InvalidInputException {

        List<Seat> selectedSeats = new ArrayList<>();

        System.out.println("\n💺 Manual Seat Selection");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("You need to select " + count + " seat(s).");
        System.out.println("═══════════════════════════════════════════════════════════");

        for (int i = 0; i < count; i++) {
            // Display seat map
            transport.displaySeatMap(premium);

            if (!selectedSeats.isEmpty()) {
                System.out.println("✅ Already selected seats: ");
                for (Seat s : selectedSeats) {
                    System.out.println("   • " + s.getSeatNumber() + " (" + s.getPosition() + ", Row " + s.getRow() + ")");
                }
                System.out.println();
            }

            System.out.println("Selecting seat " + (i + 1) + " of " + count);
            System.out.print("Enter seat number (e.g., 1A, 5B, 10C): ");
            String seatInput = scanner.nextLine().trim().toUpperCase();

            //get list of available seats excluding selected and occupied seats
            List<Seat> availableSeats = new ArrayList<>();
            for (Seat s : transport.getSeats()) {
                if (s.isPremium() == premium && !s.isOccupied() && !selectedSeats.contains(s)) {
                    availableSeats.add(s);
                }
            }

            //find the seat
            Seat chosenSeat = null;
            for (Seat seat : availableSeats) {
                if (seat.getSeatNumber().equalsIgnoreCase(seatInput)) {
                    chosenSeat = seat;
                    break;
                }
            }

            if (chosenSeat == null) {
                System.out.println("❌ Invalid seat number or seat not available. Try again.");
                i--;
                // Retry when error found
                continue;
            }

            //marked seat is displayed
            chosenSeat.bookSeat();
            selectedSeats.add(chosenSeat);
            System.out.println("✅ Seat " + seatInput + " selected!\n");
        }

        //release temporarily (Booking constructor will reserve them again)
        for (Seat seat : selectedSeats) {
            seat.release();
        }

        return selectedSeats;
    }

    private void viewBookings() {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("                    MY BOOKINGS ^_^"                          );
        System.out.println("═══════════════════════════════════════════════════════════\n");

        if (bookings.isEmpty()) {
            System.out.println("You have no bookings yet.\n");
            return;
        }

        for (int i = 0; i < bookings.size(); i++) {
            System.out.printf("Booking #%d:\n", i + 1);
            System.out.println(bookings.get(i));
            System.out.println();
        }
    }

    private void cancelBooking() throws BookingException {
        if (bookings.isEmpty()) {
            System.out.println("\n⚠️  You have no bookings to cancel.\n");
            return;
        }

        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("                  ❌ CANCEL BOOKING"                          );
        System.out.println("═══════════════════════════════════════════════════════════\n");

        //show bookings (trying different ways to display information)
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            System.out.printf("%d. %s | %s → %s | %s | %s\n",
                    i + 1, b.getBookingId(), b.getTransport().getDeparture(),
                    b.getTransport().getArrival(), b.getTransport().getDate(),
                    b.getCustomerName());
        }

        int choice = getIntInput("\nSelect booking to cancel (0 to go back): ", 0, bookings.size());
        if (choice == 0) {
            return;
        }

        Booking booking = bookings.get(choice - 1);
        booking.cancel(); // Release seats
        bookings.remove(choice - 1);

        System.out.println("\n✅ Booking " + booking.getBookingId() + "  has been cancelled successfully!");
        System.out.println("   Seats have been released.");
        System.out.println("Come back soon! \n");
    }

    private int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("⚠️  Please enter a number between " + min + " and " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Invalid input! Please enter a number :)");
            }
        }
    }

    public static void main(String[] args) {
        BookingSystem system = new BookingSystem();
        system.start();
    }
}