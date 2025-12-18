package transport;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.*;

// Abstract class I made which will handle the classes of the different means of transport (inheritence)
public abstract class Transport {
    protected String id;
    protected String type;

    protected String departure;
    protected String arrival;
    protected LocalDate date;

    protected LocalTime departureTime;
    protected LocalTime arrivalTime;
    protected double standardPrice;
    protected double premiumPrice;

    protected String addInfo;
    protected List<Seat> seats;
    protected int totalSeats;

    public Transport(String id, String type, String departure, String arrival,
                     LocalDate date, LocalTime departureTime, LocalTime arrivalTime, double standardPrice,
                     double premiumPrice, String addInfo, int totalSeats) {
        this.id = id;
        this.type = type;
        this.departure = departure;
        this.arrival = arrival;

        this.date = date;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;

        this.standardPrice = standardPrice;
        this.premiumPrice = premiumPrice;

        this.totalSeats = totalSeats;
        this.addInfo = addInfo;

        this.seats = new ArrayList<>();

        initializeSeats();
    }

    protected abstract void initializeSeats();
    public abstract String getDisplayType();

    // Count available seats depends on the class type
    public int getAvailableSeats(boolean premium) {
        int count = 0;
        for (Seat seat : seats) {
            if (seat.isPremium() == premium && !seat.isOccupied()) {
                count++;
            }
        }
        return count;
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> available = new ArrayList<>();
        for (Seat seat : seats) {
            if (!seat.isOccupied()) {
                available.add(seat);
            }
        }
        return available;
    }

    // Calculate trip duration
    public Duration getDuration() {
        LocalTime start = departureTime;
        LocalTime end = arrivalTime;
        Duration tripTime = Duration.between(start, end);

        // overnight trips to avoid negative values
        if (tripTime.isNegative()) {
            tripTime = tripTime.plusHours(24);
        }

        return tripTime;
    }



    public String getDurationString() {
        Duration dur = getDuration();
        long hrs = dur.toHours();
        long min = dur.toMinutes() % 60;
        return hrs + "h " + min + " m";
    }

    public double getPrice(boolean premium) {
        double ticketPrice;
        if (premium) {
            ticketPrice = premiumPrice;
        } else {
            ticketPrice = standardPrice;
        }
        return ticketPrice;
    }

    //loop for me to check and show seats based on the class selected (premium/standard)
    public void displaySeatMap(boolean premium) {
        // Get seats for this class
        List<Seat> classSeats = new ArrayList<>();
        for (Seat s : seats) {
            if (s.isPremium() == premium) {
                classSeats.add(s);
            }
        }

        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        if (premium) {
            System.out.println("║           PREMIUM/1ST CLASS SEAT-MAP                      ║");
        } else {
            System.out.println("║            STANDARD/2ND CLASS SEAT-MAP                    ║");
        }
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║          [X] = Occupied     [ ] = Available               ║");
        System.out.println("║                                                           ║");
        System.out.println("║                WINDOW | MIDDLE | AISLE                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        //group seats by row
        Map<Integer, List<Seat>> seatsByRow = new HashMap<>();
        for (Seat s : classSeats) {
            if (s.getRow() > 0) {
                if (!seatsByRow.containsKey(s.getRow())) {
                    seatsByRow.put(s.getRow(), new ArrayList<>());
                }
                seatsByRow.get(s.getRow()).add(s);
            }
        }

        List<Integer> rows = new ArrayList<>(seatsByRow.keySet());
        Collections.sort(rows);

        //loop to go through seats by rows and checks if they are occupied
        for (int row : rows) {
            List<Seat> rowSeats = seatsByRow.get(row);
            rowSeats.sort(Comparator.comparing(Seat::getSeatNumber));

            System.out.printf("Row %2d:  ", row);

            //checks if the seat is occupied or not :)
            for (int i = 0; i < rowSeats.size(); i++) {
                Seat seat = rowSeats.get(i);
                String status;
                if (seat.isOccupied()) {
                    status = "X";
                } else {
                    status = " ";
                }

                // tried different ways but this is the one that worked for me
                // to remove the numbers and get the letters only to display on the seat-map
                String seatLetter = seat.getSeatNumber().replace("1", "").replace("2", "")
                        .replace("3", "").replace("4", "").replace("5", "")
                        .replace("6", "").replace("7", "").replace("8", "")
                        .replace("9", "").replace("0", "");

                System.out.printf("[%s]%s ", status, seatLetter);

                // Add aisle separator after C and F (gaps to display the aisle location between seats)
                if (seatLetter.equals("C") || seatLetter.equals("F")) {
                    System.out.print("    |    ");
                }
            }

            String quality = rowSeats.get(0).getQuality();
            System.out.printf("  (%s)", quality);
            System.out.println();
        }
        System.out.println();
    }

    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public LocalDate getDate() { return date; }
    public LocalTime getDepartureTime() { return departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public double getStandardPrice() { return standardPrice; }
    public double getPremiumPrice() { return premiumPrice; }
    public String getAddInfo() { return addInfo; }
    public List<Seat> getSeats() { return seats; }
    public int getTotalSeats() { return totalSeats; }


    // This
    @Override
    public String toString() {
        int available = getAvailableSeats(false) + getAvailableSeats(true);
        String result = getDisplayType() + " | " + departure + " → " + arrival;
        result = result + " | " + date + " " + departureTime + "-" + arrivalTime;
        result = result + " (" + getDurationString() + ")";
        result = result + " | €" + standardPrice + "/€" + premiumPrice;
        result = result + " | " + available + "/" + totalSeats + " seats";
        return result;
    }
}