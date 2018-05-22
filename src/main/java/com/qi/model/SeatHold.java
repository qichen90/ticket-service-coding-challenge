package com.qi.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * SeatHold is a collection of seats which are held/reserved by a customer on some day.
 */
@Data
public class SeatHold {
    private final int seatHoldId;
    private List<Seat> seatsHold;
    private String customerEmail;
    private String confirmationCode;
    private Date dateCreated;

    public SeatHold(int seatHoldId, String customerEmail, List<Seat> seatsHold) {
        this.seatHoldId = seatHoldId;
        this.customerEmail = customerEmail;
        this.seatsHold = seatsHold;
        dateCreated = new Date();
    }

}
