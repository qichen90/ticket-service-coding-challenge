package com.qi.service.Impl;

import com.qi.model.SeatHold;
import com.qi.model.Venue;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for TicketServiceImpl
 */
public class TicketServiceImplTest {
    private String VALID_TEST_EMAIL = "test@gmail.com";
    private String INVALID_TEST_EMAIL = "test";

    @Test
    public void numSeatsAvailable() {
        Venue venue = new Venue(3, 3);
        TicketServiceImpl service = new TicketServiceImpl(venue);
        Assert.assertEquals(9, service.numSeatsAvailable());

        SeatHold seatHold = service.findAndHoldSeats(3, VALID_TEST_EMAIL);
        Assert.assertEquals(6, service.numSeatsAvailable());

        service.reserveSeats(seatHold.getSeatHoldId(), VALID_TEST_EMAIL);
        Assert.assertEquals(6, service.numSeatsAvailable());
    }

    @Test
    public void findAndHoldSeats() {
        Venue venue = new Venue(3, 3);
        TicketServiceImpl service = new TicketServiceImpl(venue);

        Assert.assertTrue(service.findAndHoldSeats(3, INVALID_TEST_EMAIL) == null);
        Assert.assertTrue(service.findAndHoldSeats(10, VALID_TEST_EMAIL) == null);

        SeatHold seatHold = service.findAndHoldSeats(3, VALID_TEST_EMAIL);
        Assert.assertEquals(1, seatHold.getSeatHoldId());
        Assert.assertEquals(3, venue.getNumberOfHeldSeats());
        Assert.assertEquals(0, venue.getNumberOfReservedSeats());
        Assert.assertEquals(6, venue.getNumberOfOpenSeats());

        seatHold = service.findAndHoldSeats(9, VALID_TEST_EMAIL);
        Assert.assertTrue(seatHold == null);

        // test with ExpirationTime
        Venue venue2 = new Venue(3, 3);
        TicketServiceImpl service2 = new TicketServiceImpl(venue2, 0);

        service2.findAndHoldSeats(3, VALID_TEST_EMAIL);
        Assert.assertEquals(6, service2.numSeatsAvailable());
        service2.findAndHoldSeats(4, VALID_TEST_EMAIL);
        Assert.assertEquals(5, service2.numSeatsAvailable());
        Assert.assertEquals(4, venue2.getNumberOfHeldSeats());
        Assert.assertEquals(0, venue2.getNumberOfReservedSeats());
        Assert.assertEquals(5, venue2.getNumberOfOpenSeats());
    }

    @Test
    public void reserveSeats() {
        Venue venue = new Venue(3, 3);
        TicketServiceImpl service = new TicketServiceImpl(venue);

        String confirmationCode = service.reserveSeats(0, VALID_TEST_EMAIL);
        Assert.assertTrue(confirmationCode == null);

        SeatHold seatHold = service.findAndHoldSeats(3, VALID_TEST_EMAIL);
        confirmationCode =  service.reserveSeats(seatHold.getSeatHoldId(), VALID_TEST_EMAIL);
        Assert.assertTrue(confirmationCode != null);
        Assert.assertTrue(confirmationCode.length() == 10);

        SeatHold seatHold2 = service.findAndHoldSeats(4, VALID_TEST_EMAIL);
        String confirmationCode2 = service.reserveSeats(seatHold2.getSeatHoldId(), INVALID_TEST_EMAIL);
        Assert.assertTrue(confirmationCode2 == null);
        confirmationCode2 = service.reserveSeats(seatHold2.getSeatHoldId(), "test2@gmail.com");
        Assert.assertTrue(confirmationCode2 == null);
        // after reservation, seathold should be remove from SeatHeldTable
        confirmationCode2 = service.reserveSeats(seatHold.getSeatHoldId(), VALID_TEST_EMAIL);
        Assert.assertTrue(confirmationCode2 == null);
        confirmationCode2 = service.reserveSeats(seatHold2.getSeatHoldId(), VALID_TEST_EMAIL);
        Assert.assertTrue(confirmationCode2 != null);
        Assert.assertTrue(!confirmationCode2.equals(confirmationCode));
        Assert.assertEquals(2, service.numSeatsAvailable());
        Assert.assertEquals(0, venue.getNumberOfHeldSeats());
        Assert.assertEquals(7, venue.getNumberOfReservedSeats());
        Assert.assertEquals(2, venue.getNumberOfOpenSeats());
    }
}