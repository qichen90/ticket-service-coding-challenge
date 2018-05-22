package com.qi.model;

import lombok.Data;

/**
 * Seat represents the single seat in Venue
 * It contains location and the state of the seat
 */
@Data
public class Seat {

    private int seatId;
    private int seatLocationRow;
    private int seatLocationCol;
    private SeatState state;

    public Seat(int seatId, int seatLocationRow, int seatLocationCol) {
        this.seatId = seatId;
        this.seatLocationRow = seatLocationRow;
        this.seatLocationCol = seatLocationCol;
        state = SeatState.OPEN;
    }
}
