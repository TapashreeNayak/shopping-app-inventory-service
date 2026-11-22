package org.shopwise.com.service;


import org.shopwise.com.entity.InventoryReservation;
import org.shopwise.com.event.OrderCreatedEvent;
import org.shopwise.com.exception.InsufficientInventoryException;
import org.shopwise.com.repository.InventoryReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryReservationService {

    private final InventoryReservationRepository reservationRepository;
    private final InventoryReservationTransactionService transactionService;

    public InventoryReservationService(
            InventoryReservationRepository reservationRepository,
            InventoryReservationTransactionService transactionService) {

        this.reservationRepository = reservationRepository;
        this.transactionService = transactionService;
    }

    public InventoryReservation reserveInventory(OrderCreatedEvent event) {

        // Check whether reservation already exists
        var existing = reservationRepository.findByOrderId(event.getOrderId());

        if (existing.isPresent()) {
            return existing.get();
        }

        try {
            return transactionService.reserveAndSave(event);

        } catch (InsufficientInventoryException ex) {

            // Stock deduction transaction has rolled back.
            InventoryReservation rejected = new InventoryReservation();

            rejected.setOrderId(event.getOrderId());
            rejected.setOrderNumber(event.getOrderNumber());
            rejected.setReservationStatus("REJECTED");

            return reservationRepository.save(rejected);
        }
    }
}
