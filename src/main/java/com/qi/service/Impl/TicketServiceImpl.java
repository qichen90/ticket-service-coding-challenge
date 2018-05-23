package com.qi.service.Impl;

import com.qi.model.Seat;
import com.qi.model.SeatHold;
import com.qi.model.SeatState;
import com.qi.model.Venue;
import com.qi.service.TicketService;
import com.qi.support.CodeGenerator;
import lombok.Getter;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of TicketService
 */
public class TicketServiceImpl implements TicketService {

    private static final int DEFAULT_CONFIRMATION_CODE_LENGTH = 10;
    private static int DEFAULT_EXPIRATION_TIME_IN_SECONDS = 600;

    private Venue venue;

    @Getter
    private Map<Integer, SeatHold> seatsHoldTable;
    @Getter
    private Map<String, SeatHold> confirmationTable;

    private int expirationTimeInSec = DEFAULT_EXPIRATION_TIME_IN_SECONDS;

    private AtomicInteger id = new AtomicInteger(0);

    public TicketServiceImpl(Venue venue) {
        if(venue == null){
            throw new IllegalArgumentException("venue provided is not valid");
        }
        this.venue = venue;
        seatsHoldTable = new HashMap<Integer, SeatHold>();
        confirmationTable = new HashMap<String, SeatHold>();
    }

    // constructor with expiration time
    public TicketServiceImpl(Venue venue, int expirationTimeInSec){
        if(venue == null){
            throw new IllegalArgumentException("venue provided is not valid");
        }
        if(expirationTimeInSec < 0){
            throw new IllegalArgumentException("expirationTimeInSec provided is not valid");
        }
        this.venue = venue;
        this.expirationTimeInSec = expirationTimeInSec;
        seatsHoldTable = new HashMap<Integer, SeatHold>();
        confirmationTable = new HashMap<String, SeatHold>();
    }

    /**
     * The number of seats in the venue that are neither held nor reserved
     *
     * @return the number of tickets available in the venue
     */
    @Override
    public int numSeatsAvailable() {
        return venue.getNumberOfOpenSeats();
    }

    /**
     * Find and hold the best available seats for a customer
     *
     * @param numSeats the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object identifying the specific seats and related
    information
     */
    @Override
    public synchronized SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        if(numSeats <= 0 || !EmailValidator.getInstance().isValid(customerEmail)){
            return null;
        }
        //reset expired SeatHold
        expireSeatHold();

        if(numSeats > numSeatsAvailable()){
            return null;
        }

        // find seats
        List<Seat> listOfSeats = findSeat(numSeats);

        venue.stateChange(listOfSeats, SeatState.HELD);
        int seatHoldId = id.incrementAndGet();
        SeatHold seatHold = new SeatHold(seatHoldId, customerEmail, listOfSeats);
        seatsHoldTable.put(seatHoldId, seatHold);
        return seatHold;
    }

    /**
     * Commit seats held for a specific customer
     *
     * @param seatHoldId the seat hold identifier
     * @param customerEmail the email address of the customer to which the
    seat hold is assigned
     * @return a reservation confirmation code
     */
    @Override
    public synchronized String reserveSeats(int seatHoldId, String customerEmail) {
        SeatHold seathold = seatsHoldTable.get(seatHoldId);
        // no seatHoldId matched or has expired
        if(!seatsHoldTable.containsKey(seatHoldId)) {
            return null;
        }
        // Email is not valid or email doesn't match the held email
        if(!EmailValidator.getInstance().isValid(customerEmail) || !seathold.getCustomerEmail().equals(customerEmail)){
            return null;
        }

        // process the seathold to set reserved for seats
        venue.stateChange(seathold.getSeatsHold(), SeatState.RESERVED);

        String confirmationCode = CodeGenerator.generateCode(DEFAULT_CONFIRMATION_CODE_LENGTH);
        seathold.setConfirmationCode(confirmationCode);
        confirmationTable.put(confirmationCode, seathold);
        seatsHoldTable.remove(seatHoldId);
        return confirmationCode;
    }

    /**
     *
     *  for expired SeatHold: reset Held seats to OPEN
     */
    private void expireSeatHold() {
        Date currentDate = new Date();
        long currentTimeInMS = currentDate.getTime();
        for(Integer seatHoldId: seatsHoldTable.keySet()) {
            SeatHold seatHold = seatsHoldTable.get(seatHoldId);
            long heldTimeInMS = seatHold.getDateCreated().getTime();
            // Current SeatHold is expired, reset the seat to OPEN and remove it from the seatsholdTable
            if(currentTimeInMS - heldTimeInMS >= expirationTimeInSec * 1000){
                venue.stateChange(seatHold.getSeatsHold(), SeatState.OPEN);
                seatsHoldTable.remove(seatHoldId);
            }
        }
    }

    /**
     *
     * @param numSeats int
     * @return list of available seats
     */
    private List<Seat> findSeat(int numSeats) {
        if(numSeatsAvailable() == 0 || numSeatsAvailable() < numSeats){
            return null;
        }
        List<Seat> listOfSeats = new ArrayList<Seat>();

        int row = venue.getRow();
        int col = venue.getCol();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                Seat seat = venue.getSeat(i, j);
                if(SeatState.OPEN.equals(seat.getState())){
                    listOfSeats.add(seat);
                }
                if(listOfSeats.size() == numSeats) return listOfSeats;
            }
        }
        return null;
    }
}
