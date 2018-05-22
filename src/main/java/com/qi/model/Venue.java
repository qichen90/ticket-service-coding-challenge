package com.qi.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Venue defines properties of the venue
 */
@Data
public class Venue {

    private int row;
    private int col;

    private int totalSeats;
    private int numberOfOpenSeats;
    private int numberOfHeldSeats;
    private int numberOfReservedSeats;

    private List<List<Seat>> seats;

    public Venue(int row, int col){
        this.row = row;
        this.col = col;
        initSeats(row, col);
        totalSeats = row * col;
        numberOfHeldSeats = 0;
        numberOfOpenSeats = totalSeats;
        numberOfReservedSeats = 0;
    }

    // initialize the seats of Venue
    private void initSeats(int row, int col){
        if(row <= 0 || col <= 0) {
            throw new IllegalArgumentException("Initial value for Venue row or column number is invalid");
        }
        seats = new ArrayList<List<Seat>>();
        for(int i = 0; i < row; i++){
            List<Seat> seatsInRow = new ArrayList<Seat>();
            for(int j = 0; j < col; j++){
                seatsInRow.add(new Seat(i * row + j, i, j));
            }
            seats.add(seatsInRow);
        }
    }

    /**
     *
     * @param listOfSeats: list of seats needs to do state change
     * @param state: next state
     */
    public synchronized void stateChange(List<Seat> listOfSeats, SeatState state) {
        if(listOfSeats == null || listOfSeats.size() == 0 || state == null) {
            throw new IllegalArgumentException("Input is not valid");
        }

        for(Seat seat: listOfSeats){
            SeatState currentState = seat.getState();
            if(currentState.equals(state)) continue;
            updateStateCount(currentState, false);
            updateStateCount(state, true);

            seat.setState(state);
        }

        if(numberOfOpenSeats < 0 || numberOfHeldSeats < 0 || numberOfReservedSeats < 0
                || numberOfOpenSeats > totalSeats || numberOfReservedSeats > totalSeats || numberOfHeldSeats > totalSeats){
            throw new SecurityException("Seat state change failed");
        }
    }
    // update the number of seats for each state
    private void updateStateCount(SeatState state, boolean toIncrease) {
        switch(state) {
            case OPEN:
                numberOfOpenSeats += toIncrease ? 1 : -1;
                break;
            case RESERVED:
                numberOfReservedSeats += toIncrease ? 1 : -1;
                break;
            case HELD:
                numberOfHeldSeats += toIncrease ? 1 : -1;
                break;
            default:
                throw new EnumConstantNotPresentException(SeatState.class, "No such state");
        }
    }

    /**
     *
     * @param row int position row
     * @param col int position column
     * @return the specific seat
     */
    public Seat getSeat(int row, int col) {
        if(row < 0 || col < 0 || row >= this.row || col >= this.col){
            throw new IllegalArgumentException("The seat is not valid. Please provide valid seat location");
        }
        return seats.get(row).get(col);
    }
}
