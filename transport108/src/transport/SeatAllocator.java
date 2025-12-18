package transport;

import java.util.*;


public class SeatAllocator {

    /**
     * Allocates seats based on transport type and class
     * Premium: Always gets the best available seats (EXCELLENT quality, front rows)
     * Standard on Train/Flight: Gets GOOD quality reserved seats (window/aisle preferred)
     * Standard on Bus: Random assignment from available standard seats
     */


    public static List<Seat> allocateSeats(Transport transport, int count, boolean premium)
            throws BookedSeatsException {

        // get available seats for the class chosen
        List<Seat> available = new ArrayList<>();
        for (Seat seat : transport.getSeats()) {
            if (seat.isPremium() == premium && !seat.isOccupied()) {
                available.add(seat);
            }
        }

        // Check if enough seats available
        if (available.size() < count) {
            String classType;
            if (premium) {
                classType = "premium";
            } else {
                classType = "standard";
            }

            //slowly learning how %d is such a saver
            throw new BookedSeatsException(
                    String.format("Not enough %s seats available. Need %d, but only %d available.",
                            classType, count, available.size()));
        }

        List<Seat> chosenSeats = new ArrayList<>();
        if (premium) {

            // Premium seats
            chosenSeats = allocatePremiumSeats(available, count);

        }

        else {
            //Made a special condition since in buses it doesnt matter as much as in flights and trains
            if (transport.getType().equals("BUS")) {

                // Bus: Random assignment
                chosenSeats = allocateRandomSeats(available, count);

            } else
            {
                // Train/Flight reserve good seats
                chosenSeats = allocateStandardReservedSeats(available, count);
            }
        }

        return chosenSeats;
    }

    //Premium seats allocation
    private static List<Seat> allocatePremiumSeats(List<Seat> available, int count) {
        // Sort by quality
        available.sort((s1, s2) -> {
            int qualityCompare = qualityOrder(s1.getQuality()) - qualityOrder(s2.getQuality());
            if (qualityCompare != 0) return qualityCompare;

            int rowCompare = s1.getRow() - s2.getRow();
            if (rowCompare != 0) return rowCompare;

            return positionOrder(s1.getPosition()) - positionOrder(s2.getPosition());
        });

        return available.subList(0, count);
    }

// handling groups in good quality seats
    private static List<Seat> allocateStandardReservedSeats(List<Seat> available, int count) {
        // Filter for good quality seats first
        List<Seat> goodSeats = new ArrayList<>();
        for (Seat s : available) {
            if (s.getQuality().equals("GOOD")) {
                goodSeats.add(s);
            }
        }

        // If not enough good seats, fall back to all available
        if (goodSeats.size() < count) {
            goodSeats = new ArrayList<>(available);
        }

        // Try to find seats in the same row or adjacent rows
        List<Seat> clustered = findClusteredSeats(goodSeats, count);

        if (clustered.size() >= count) {
            return clustered.subList(0, count);
        }

        // if clustering fails, sort by quality and row, prefer window/aisle
        goodSeats.sort((s1, s2) -> {
            int qualityCompare = qualityOrder(s1.getQuality()) - qualityOrder(s2.getQuality());
            if (qualityCompare != 0) return qualityCompare;

            int rowCompare = s1.getRow() - s2.getRow();
            if (rowCompare != 0) return rowCompare;

            return positionOrder(s1.getPosition()) - positionOrder(s2.getPosition());
        });

        return goodSeats.subList(0, count);
    }

    //Random allocation for buses
    private static List<Seat> allocateRandomSeats(List<Seat> available, int count) {
        Collections.shuffle(available);
        return available.subList(0, count);
    }

    //next or adjacent seats
    private static List<Seat> findClusteredSeats(List<Seat> seats, int count) {
        // Group by row
// Group by row
        Map<Integer, List<Seat>> byRow = new HashMap<>();
        for (Seat seat : seats) {
            if (!byRow.containsKey(seat.getRow())) {
                byRow.put(seat.getRow(), new ArrayList<>());
            }
            byRow.get(seat.getRow()).add(seat);
        }

        List<Seat> result = new ArrayList<>();

        // Sort rows
        List<Integer> rows = new ArrayList<>(byRow.keySet());
        Collections.sort(rows);

        // Try to fill from consecutive rows
        for (int i = 0; i < rows.size() && result.size() < count; i++) {
            List<Seat> rowSeats = byRow.get(rows.get(i));
            for (Seat seat : rowSeats) {
                if (result.size() < count) {
                    result.add(seat);
                }
            }
        }

        return result;
    }

    // Order by Quality
    private static int qualityOrder(String quality) {
        switch (quality) {
            case "EXCELLENT": return 0;
            case "GOOD": return 1;
            case "AVERAGE": return 2;
            case "POOR": return 3;
            default: return 4;
        }
    }

    //Order of positions
    private static int positionOrder(String position) {
        switch (position) {
            case "WINDOW": return 0;
            case "AISLE": return 1;
            case "MIDDLE": return 2;
            default: return 3;
        }
    }
}