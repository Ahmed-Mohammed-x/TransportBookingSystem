package transport;

import java.time.LocalDate;
import java.time.LocalTime;

public class Flight extends Transport {

    // Aircraft vary greatly but for this simple implementation 180 seats
    public Flight(String id, String departure, String arrival,
                  LocalDate date, LocalTime departureTime, LocalTime arrivalTime,
                  double standardPrice, double premiumPrice, String airline) {
        super(id, "FLIGHT", departure, arrival, date, departureTime, arrivalTime,
                standardPrice, premiumPrice, airline, 180);
    }

    @Override
    protected void initializeSeats() {
        // The layout for this 180 seats, 9 seats per row (A-C | D-F | G-J layout)

        // Business Class
        for (int row = 1; row <= 5; row++) {
            seats.add(new Seat(row + "A", "WINDOW", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "B", "MIDDLE", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "C", "AISLE", row, true, "EXCELLENT"));
            // Aisle
            seats.add(new Seat(row + "D", "AISLE", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "E", "MIDDLE", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "F", "AISLE", row, true, "EXCELLENT"));
            // Aisle
            seats.add(new Seat(row + "G", "AISLE", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "H", "MIDDLE", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "J", "WINDOW", row, true, "EXCELLENT"));
        }

        // Economy Good Quality
        for (int row = 6; row <= 15; row++) {
            seats.add(new Seat(row + "A", "WINDOW", row, false, "GOOD"));
            seats.add(new Seat(row + "B", "MIDDLE", row, false, "GOOD"));
            seats.add(new Seat(row + "C", "AISLE", row, false, "GOOD"));
            // Aisle
            seats.add(new Seat(row + "D", "AISLE", row, false, "GOOD"));
            seats.add(new Seat(row + "E", "MIDDLE", row, false, "GOOD"));
            seats.add(new Seat(row + "F", "AISLE", row, false, "GOOD"));
            // Aisle
            seats.add(new Seat(row + "G", "AISLE", row, false, "GOOD"));
            seats.add(new Seat(row + "H", "MIDDLE", row, false, "GOOD"));
            seats.add(new Seat(row + "J", "WINDOW", row, false, "GOOD"));
        }

        // Economy Poor Quality (back near toilets)
        for (int row = 16; row <= 20; row++) {
            seats.add(new Seat(row + "A", "WINDOW", row, false, "POOR"));
            seats.add(new Seat(row + "B", "MIDDLE", row, false, "POOR"));
            seats.add(new Seat(row + "C", "AISLE", row, false, "POOR"));
            // Aisle
            seats.add(new Seat(row + "D", "AISLE", row, false, "POOR"));
            seats.add(new Seat(row + "E", "MIDDLE", row, false, "POOR"));
            seats.add(new Seat(row + "F", "AISLE", row, false, "POOR"));
            // =Aisle
            seats.add(new Seat(row + "G", "AISLE", row, false, "POOR"));
            seats.add(new Seat(row + "H", "MIDDLE", row, false, "POOR"));
            seats.add(new Seat(row + "J", "WINDOW", row, false, "POOR"));
        }
    }

    @Override
    public String getDisplayType() {
        return "✈️ FLIGHT (" + addInfo + ")";
    }
}