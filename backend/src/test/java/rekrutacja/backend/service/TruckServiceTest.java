package rekrutacja.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rekrutacja.backend.model.Truck;
import rekrutacja.backend.model.TruckStatus;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TruckServiceTest {

    private TruckService truckService;
    private Set<Truck> trucks;
    private LinkedList<Truck> queue1;
    private LinkedList<Truck> queue2;

    @BeforeEach
    public void setUp() {
        truckService = new TruckService();
        truckService.init();
    }

    @Test
    public void testCalculateEstimatedWaitTime_TruckInQueue() {

        int waitTime = truckService.calculateEstimatedWaitTime(2L);
        assertTrue(waitTime < 35, "Estimated wait time should be 34 seconds");
    }

    @Test
    public void testCalculateEstimatedWaitTime_TruckArrived() {
        Truck truck1 = new Truck(1L, 5, 5, TruckStatus.ARRIVED);
        Truck truck2 = new Truck(2L, 10, 10, TruckStatus.ARRIVED);
        Truck truck3 = new Truck(3L, 7, 7, TruckStatus.ARRIVED);
        truckService.assignToQueue(truck1);
        truckService.assignToQueue(truck2);
        truckService.assignToQueue(truck3);

        int waitTime = truckService.calculateEstimatedWaitTime(3L);
        assertTrue(waitTime > 0, "Estimated wait time should be greater than 0");
    }

    @Test
    public void testCalculateEstimatedWaitTime_TruckNotFound() {
        Truck truck1 = new Truck(1L, 5, 2, TruckStatus.ARRIVED);
        truckService.assignToQueue(truck1);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            truckService.calculateEstimatedWaitTime(82L);
        });

        assertEquals("Truck not found", exception.getMessage());
    }


    @Test
    public void testStatus() {

        var status = truckService.status();
        assertEquals(15, ((List<?>) status.get("trucks")).size(), "Status should return all trucks");
    }
}
