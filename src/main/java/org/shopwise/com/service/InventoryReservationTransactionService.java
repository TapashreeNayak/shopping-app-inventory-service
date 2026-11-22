package org.shopwise.com.service;

import org.shopwise.com.entity.InventoryReservation;
import org.shopwise.com.event.OrderCreatedEvent;
import org.shopwise.com.repository.InventoryReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InventoryReservationTransactionService {

    private final InventoryStockService stockService;
    private final InventoryReservationRepository reservationRepository;

    public InventoryReservationTransactionService(
            InventoryStockService stockService,
            InventoryReservationRepository reservationRepository) {

        this.stockService = stockService;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public InventoryReservation reserveAndSave(OrderCreatedEvent event) {

        // Deduct stock
        stockService.deductStock(event);

        // Create reservation record
        InventoryReservation reservation = new InventoryReservation();

        reservation.setOrderId(event.getOrderId());
        reservation.setOrderNumber(event.getOrderNumber());

        // IMPORTANT: Set status before saving
        reservation.setReservationStatus("RESERVED");

        reservation.setCreatedAt(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }
}