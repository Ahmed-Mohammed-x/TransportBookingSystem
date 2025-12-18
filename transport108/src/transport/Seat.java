package transport;

public class Seat {
    private String SeatNumber;
    private String position;
    private int row;
    private boolean isPremium;
    private String quality; //
    private boolean isOccupied;

    //constructor to initialise new seat
    public Seat(String seatNumber, String position, int row, boolean isPremium, String quality) {
        this.SeatNumber = seatNumber;
        this.position = position;
        this.row = row;
        this.isPremium = isPremium;
        this.quality = quality;
        this.isOccupied = false;
    }

    public boolean bookSeat() {
        if (!isOccupied) {
            isOccupied = true;
            return true;
        }
        return false;
    }

    public void release() {
        isOccupied = false;
    }

    // Getters
    public String getSeatNumber() { return SeatNumber; }
    public int getRow() { return row; }
    public String getPosition() { return position; }
    public boolean isPremium() { return isPremium; }
    public boolean isOccupied() { return isOccupied; }
    public String getQuality() { return quality; }

    @Override //for example it can show the memory address
    public String toString() {
        return SeatNumber + (isOccupied ? "[X]" : "[ ]");
    }
}