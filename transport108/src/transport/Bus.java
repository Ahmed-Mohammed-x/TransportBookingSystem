package transport;

import java.time.LocalDate;
import java.time.LocalTime;

public class Bus extends Transport {

    // I gave buses 50 seats (minimum standard for travel)
    public Bus(String id, String departure, String arrival,
               LocalDate date, LocalTime departureTime, LocalTime arrivalTime,
               double standardPrice, double premiumPrice, String company) {
        super(id, "BUS", departure, arrival, date, departureTime, arrivalTime,
                standardPrice, premiumPrice, company, 50);
    }

    //to initialise all bus seats
    @Override
    protected void initializeSeats() {
        //bus has 50 seats, 4 per row

        // Premium section/1st class for rows 1-3: 12 seats
        for (int r = 1; r <= 3; r++) {
            seats.add(new Seat(r + "A", "WINDOW", r, true, "EXCELLENT"));
            seats.add(new Seat(r + "B", "AISLE", r, true, "EXCELLENT"));
            //aisle
            seats.add(new Seat(r + "C", "AISLE", r, true, "EXCELLENT"));
            seats.add(new Seat(r + "D", "WINDOW", r, true, "EXCELLENT"));
        }

        // Standard section/2nd class (Rows 4-12): 36 seats
        for (int row = 4; row <= 12; row++) {
            seats.add(new Seat(row + "A", "WINDOW", row, false, "AVERAGE"));
            seats.add(new Seat(row + "B", "AISLE", row, false, "AVERAGE"));
            //aisle
            seats.add(new Seat(row + "C", "AISLE", row, false, "AVERAGE"));
            seats.add(new Seat(row + "D", "WINDOW", row, false, "AVERAGE"));
        }

        // Last row near toilet (Row 13): 2 seats - worst
        seats.add(new Seat("13A", "WINDOW", 13, false, "POOR"));
        seats.add(new Seat("13D", "WINDOW", 13, false, "POOR"));
    }

    @Override
    public String getDisplayType() {
        return "🚌 BUS (" + addInfo + ")";
    }
}