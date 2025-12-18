package transport;

// I made a custom-made exception class since its common practice to debug and know the cause of a triggered exception :)


//all booking-related errors
class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

//trip-finding related errors
class NoTripsFoundException extends BookingException {
    public NoTripsFoundException(String message) {
        super(message);
    }
}

//seats are occupied
class BookedSeatsException extends BookingException {
    public BookedSeatsException(String message) {
        super(message);
    }
}

// When we have an invalid date
class InvalidDateException extends BookingException {
    public InvalidDateException(String message) {
        super(message);
    }
}

// Exception for invalid input
class InvalidInputException extends BookingException {
    public InvalidInputException(String message) {
        super(message);
    }
}

//when booking is not found for cancellation
//class BookingNotFoundException extends BookingException {
    //public BookingNotFoundException(String message) {
        //super(message);
    //}
//}