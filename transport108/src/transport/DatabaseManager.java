package transport;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseManager {

    // load all files in the transport files
    private static final String TRANSPORT_FOLDER = "./transport files";

    public static List<Transport> loadTransports() throws IOException {
        List<Transport> transports = new ArrayList<>();

        File folder = new File(TRANSPORT_FOLDER);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("Transport data folder not found!\n" +
                    "Please create a folder named '" + TRANSPORT_FOLDER + " in your project root\n" +
                    "and add your transport data files (.txt or .csv) inside it.");
        }

        System.out.println("📁 Loading transport data from: " + TRANSPORT_FOLDER + "/");
        transports.addAll(loadFromFolder(folder, 0));

        if (transports.isEmpty()) {
            throw new IOException("No valid transport data found in '" + TRANSPORT_FOLDER + "/' folder!");
        }

        System.out.println("✅ Loaded " + transports.size() + " transports and preoccupied seats to simulate real booking experience.\n");
        return transports;
    }

    private static List<Transport> loadFromFolder(File folder, int depth) throws IOException {
        List<Transport> transports = new ArrayList<>();

        File[] files = folder.listFiles();
        if (files == null) {
            return transports;
        }

        String indent = "  ".repeat(depth + 1);

        for (File file : files) {
            // If it's a folder, recursively load from it
            if (file.isDirectory()) {
                System.out.println(indent + "📂 " + file.getName() + "/");
                transports.addAll(loadFromFolder(file, depth + 1));
            }
            // If it's a .txt or .csv file, load it
            else if (file.isFile() && (file.getName().endsWith(".txt") || file.getName().endsWith(".csv"))) {
                System.out.println(indent + "📄 " + file.getName());
                transports.addAll(loadFromFile(file));
            }
            // Ignore other file types
        }

        return transports;
    }


    private static List<Transport> loadFromFile(File file) throws IOException {
        List<Transport> transports = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            int loadedFromFile = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Skip comment lines (starting with #)
                if (line.startsWith("#")) {
                    continue;
                }

                // Skip header/decorative lines
                if (line.startsWith("Format:") ||
                        line.startsWith("Dates:") ||
                        line.startsWith("Cities:") ||
                        line.startsWith("Company:") ||
                        line.startsWith("Routes:") ||
                        line.startsWith("TOTAL:") ||
                        line.startsWith("Covers") ||
                        line.startsWith("Mix of") ||
                        line.startsWith("Realistic") ||
                        line.startsWith("Some dates") ||
                        line.contains("==========")) {
                    continue;
                }

                // Skip lines that don't start with valid transport types
                if (!line.startsWith("TRAIN") &&
                        !line.startsWith("BUS") &&
                        !line.startsWith("FLIGHT")) {
                    continue;
                }

                try {
                    Transport transport = parseLine(line);
                    randomlyOccupySeats(transport);
                    transports.add(transport);
                    loadedFromFile++;
                } catch (Exception e) {
                    System.err.println("      ⚠️error on line " + lineNumber + ": " + e.getMessage());
                }
            }

            if (loadedFromFile > 0) {
                System.out.println("       Loaded " + loadedFromFile + " transport/s");
            }
        }

        return transports;
    }

    private static Transport parseLine(String line) {
        String[] parts = line.split(",");

        if (parts.length != 10) {
            throw new IllegalArgumentException("Expected 10 fields, got " + parts.length);
        }

        String type = parts[0].trim();
        String id = parts[1].trim();
        String departure = parts[2].trim();
        String arrival = parts[3].trim();
        LocalDate date = LocalDate.parse(parts[4].trim());
        LocalTime departureTime = LocalTime.parse(parts[5].trim());
        LocalTime arrivalTime = LocalTime.parse(parts[6].trim());
        double standardPrice = Double.parseDouble(parts[7].trim());
        double premiumPrice = Double.parseDouble(parts[8].trim());
        String extraInfo = parts[9].trim();

        // Create appropriate transport object based on type
        switch (type.toUpperCase()) {
            case "TRAIN":
                return new Train(id, departure, arrival, date, departureTime, arrivalTime,
                        standardPrice, premiumPrice, extraInfo);
            case "BUS":
                return new Bus(id, departure, arrival, date, departureTime, arrivalTime,
                        standardPrice, premiumPrice, extraInfo);
            case "FLIGHT":
                return new Flight(id, departure, arrival, date, departureTime, arrivalTime,
                        standardPrice, premiumPrice, extraInfo);
            default:
                throw new IllegalArgumentException("Unknown transport type: " + type);
        }
    }

    /**
     * Randomly occupy 20-40% of seats to simulate a real booking system
     */
    private static void randomlyOccupySeats(Transport transport) {
        Random random = new Random();
        List<Seat> seats = transport.getSeats();

        double occupationRate = 0.20 + (random.nextDouble() * 0.20); // 20%-40%
        int seatsToOccupy = (int) (seats.size() * occupationRate);

        List<Integer> occupiedIndices = new ArrayList<>();
        while (occupiedIndices.size() < seatsToOccupy) {
            int randomIndex = random.nextInt(seats.size());
            if (!occupiedIndices.contains(randomIndex)) {
                occupiedIndices.add(randomIndex);
                seats.get(randomIndex).bookSeat();
            }
        }
    }
}