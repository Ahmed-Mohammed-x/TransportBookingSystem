package transport;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

public class TripSearcher {

    private List<Transport> allTransports;

    public TripSearcher(List<Transport> transports) {
        this.allTransports = transports;
    }


    // I made a small threading implementation to showcase the concept
    // but it doesnt really give any major performance boost here
    //will be impactful for huge datasets tho
    public List<Transport> searchTrips(String departure, String arrival, LocalDate date)
            throws NoTripsFoundException {

        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<List<Transport>>> futures = new ArrayList<>();

        //search each transport type in parallel
        futures.add(executor.submit(() -> searchByType("TRAIN", departure, arrival, date)));
        futures.add(executor.submit(() -> searchByType("BUS", departure, arrival, date)));
        futures.add(executor.submit(() -> searchByType("FLIGHT", departure, arrival, date)));

        List<Transport> results = new ArrayList<>();

        try {
            //receive results from all threads
            for (Future<List<Transport>> future : futures) {
                results.addAll(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new NoTripsFoundException("Error during trip search: " + e.getMessage());
        } finally {
            executor.shutdown();
        }

        // If no exact matches found, try nearest dates
        if (results.isEmpty()) {
            results = searchNearestDate(departure, arrival, date);
        }

        if (results.isEmpty()) {
            throw new NoTripsFoundException(
                    String.format("No trips found from %s to %s on or near %s",
                            departure, arrival, date));
        }

        //Sorting by date, then by departure time (trying Lambda sort instead of for loop)
        results.sort((t1, t2) -> {
            int dateCompare = t1.getDate().compareTo(t2.getDate());
            if (dateCompare != 0) return dateCompare;
            return t1.getDepartureTime().compareTo(t2.getDepartureTime());
        });

        return results;
    }

    private List<Transport> searchByType(String type, String departure, String arrival, LocalDate date) {
        List<Transport> results = new ArrayList<>();

        for (Transport t : allTransports) {
            if (t.getType().equals(type) &&
                    t.getDeparture().equalsIgnoreCase(departure) &&
                    t.getArrival().equalsIgnoreCase(arrival) &&
                    t.getDate().equals(date)) {

                int availableSeats = t.getAvailableSeats(false) + t.getAvailableSeats(true);
                if (availableSeats > 0) {
                    results.add(t);
                }
            }
        }

        return results;
    }

    /**
     * Search for nearest available dates (within ±3 days)
     */
    private List<Transport> searchNearestDate(String departure, String arrival, LocalDate targetDate) {
        System.out.println("\n⚠️ sorry. no trips found for the date chose. Searching nearby dates...\n");

        List<Transport> nearby = new ArrayList<>();

        // Search ±14 days
        for (int offset = 1; offset <= 14; offset++) {
            //trying future dates first
            LocalDate futureDate = targetDate.plusDays(offset);
            nearby.addAll(searchExactDate(departure, arrival, futureDate));

            //trying past dates
            LocalDate pastDate = targetDate.minusDays(offset);
            nearby.addAll(searchExactDate(departure, arrival, pastDate));

            // If some trips found return them
            if (!nearby.isEmpty()) {
                System.out.println("✅ Found trips on nearby dates!\n");
                return nearby;
            }
        }

        return nearby;
    }


    //helper method for calling or else I need to duplicate code ±14
    private List<Transport> searchExactDate(String departure, String arrival, LocalDate date) {
        List<Transport> results = new ArrayList<>();

        for (Transport t : allTransports) {
            if (t.getDeparture().equalsIgnoreCase(departure) &&
                    t.getArrival().equalsIgnoreCase(arrival) &&
                    t.getDate().equals(date)) {

                int availableSeats = t.getAvailableSeats(false) + t.getAvailableSeats(true);
                if (availableSeats > 0) {
                    results.add(t);
                }
            }
        }

        return results;
    }
}