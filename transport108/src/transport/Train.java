package transport;

import java.time.LocalDate;
import java.time.LocalTime;

public class Train extends Transport {

    // I chose 200  seats for trains
    public Train(String id, String departure, String arrival,
                 LocalDate date, LocalTime departureTime, LocalTime arrivalTime,
                 double standardPrice, double premiumPrice, String trainClass) {
        super(id, "TRAIN", departure, arrival, date, departureTime, arrivalTime,
                standardPrice, premiumPrice, trainClass, 200);
    }

    //initialising all seats
    @Override
    protected void initializeSeats() {
        // 1st Class rows 1-5: 30seats (EXCELLENT)
        for (int row = 1; row <= 5; row++) {
            seats.add(new Seat(row + "A", "WINDOW", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "B", "AISLE", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "C", "WINDOW", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "D", "WINDOW", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "E", "AISLE", row, true, "EXCELLENT"));
            seats.add(new Seat(row + "F", "WINDOW", row, true, "EXCELLENT"));
        }

        // 2nd Class rows 6-33: 170 seats
        for (int row = 6; row <= 33; row++) {

            // we need string quality check here since first rows are always better even in second class
            String quality = (row <= 15) ? "GOOD" : (row <= 28) ? "AVERAGE" : "POOR";
            seats.add(new Seat(row + "A", "WINDOW", row, false, quality));
            seats.add(new Seat(row + "B", "MIDDLE", row, false, quality));
            seats.add(new Seat(row + "C", "AISLE", row, false, quality));
            seats.add(new Seat(row + "D", "AISLE", row, false, quality));
            seats.add(new Seat(row + "E", "MIDDLE", row, false, quality));
            seats.add(new Seat(row + "F", "WINDOW", row, false, quality));
        }

        // Last 2 seats near the toilet
        seats.add(new Seat("34A", "WINDOW", 34, false, "POOR"));
        seats.add(new Seat("34B", "AISLE", 34, false, "POOR"));
    }

    @Override
    public String getDisplayType() {
        return "🚆 TRAIN (" + addInfo + ")";
    }
}