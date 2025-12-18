package transport;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

public class Booking {
    private String bookingId;
    private Transport transport;
    private List<Seat> bookedSeats;
    private String customerName;
    private int passengerCount;
    private double totalPrice;
    private LocalDateTime bookingTime;
    private boolean isPremium;

    public Booking(Transport transport, List<Seat> seats, int passengerCount, String customerName,
                   boolean isPremium) {
        this.bookingId = generateBookingId();
        this.transport = transport;
        this.bookedSeats = new ArrayList<>(seats);
        this.passengerCount = passengerCount;
        this.customerName = customerName;

        this.isPremium = isPremium;
        this.totalPrice = calculatePrice();
        this.bookingTime = LocalDateTime.now();

        //reserve all seats
        for (Seat seat : seats) {
            seat.bookSeat();
        }
    }

    //help generate booking id to keep track
    private String generateBookingId() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // calculate total price for passengers depending on class and because we get transport.getPRice
    // we get the price depending on the class
    private double calculatePrice() {
        return transport.getPrice(isPremium) * passengerCount;
    }

    public void cancel() {
        //release all seats that were booked
        for (Seat seat : bookedSeats) {
            seat.release();
        }
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public Transport getTransport() { return transport; }

    public List<Seat> getBookedSeats() { return bookedSeats; }
    public String getCustomerName() { return customerName; }
    public int getPassengerCount() { return passengerCount; }
    public double getTotalPrice() { return totalPrice; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public boolean isPremium() { return isPremium; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder output = new StringBuilder();
        output.append("═══════════════════════════════════════════════════════════\n");
        output.append(String.format("  Booking ID: %s\n", bookingId));
        output.append(String.format("  Passenger: %s (%d person%s)\n", customerName, passengerCount,
                passengerCount > 1 ? "s" : ""));
        output.append(String.format("  Transport: %s\n", transport.getDisplayType()));
        output.append(String.format("  Route: %s → %s\n", transport.getDeparture(), transport.getArrival()));
        output.append(String.format("  Date: %s | %s - %s (%s)\n",
                transport.getDate(), transport.getDepartureTime(),
                transport.getArrivalTime(), transport.getDurationString()));
        output.append(String.format("  Class: %s\n", isPremium ? "Premium/1st Class" : "Standard/2nd Class"));
        output.append(String.format("  Seats: "));
        for (int i = 0; i < bookedSeats.size(); i++) {
            output.append(bookedSeats.get(i).getSeatNumber());
            if (i < bookedSeats.size() - 1) output.append(", ");
        }
        output.append("\n");
        output.append(String.format("  Total Price: €%.2f\n", totalPrice));
        output.append(String.format("  Booked: %s\n", bookingTime.format(formatter)));
        output.append("═══════════════════════════════════════════════════════════");
        return output.toString();
    }
}