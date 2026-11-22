package org.shopwise.com.messages;

import org.shopwise.com.event.OrderCreatedEvent;
import org.shopwise.com.utils.KafkaConfigProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


import org.shopwise.com.entity.InventoryReservation;
import org.shopwise.com.event.InventoryResponseEvent;

import org.shopwise.com.service.InventoryReservationService;


import java.time.LocalDateTime;

@Service
public class InventoryKafkaConsumer {

    private final InventoryReservationService reservationService;
    private final InventoryKafkaProducer inventoryKafkaProducer;

    public InventoryKafkaConsumer(
            InventoryReservationService reservationService,
            InventoryKafkaProducer inventoryKafkaProducer) {

        this.reservationService = reservationService;
        this.inventoryKafkaProducer = inventoryKafkaProducer;
    }

    @KafkaListener(
            topics = KafkaConfigProperties.ORDER_CREATED_TOPIC,
            groupId = KafkaConfigProperties.INVENTORY_SERVICE_GROUP
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {

        System.out.println("------------------------------------");
        System.out.println("OrderCreatedEvent received");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Order Number: " + event.getOrderNumber());

        // Step 1: Process inventory reservation
        InventoryReservation reservation =
                reservationService.reserveInventory(event);

        // Step 2: Build inventory response
        InventoryResponseEvent response = new InventoryResponseEvent();

        response.setOrderId(event.getOrderId());
        response.setOrderNumber(event.getOrderNumber());

        response.setReservationStatus(
                reservation.getReservationStatus()
        );

        response.setProcessedAt(LocalDateTime.now());

        if ("RESERVED".equalsIgnoreCase(
                reservation.getReservationStatus())) {

            response.setMessage(
                    "Inventory successfully reserved."
            );

        } else {

            response.setMessage(
                    "Inventory reservation rejected. "
                            + "Insufficient stock."
            );
        }

        // Step 3: Publish response to Order Service
        inventoryKafkaProducer.publishInventoryResponse(response);

        System.out.println("Inventory processing completed.");
        System.out.println("Order ID: " + response.getOrderId());
        System.out.println("Reservation Status: "
                + response.getReservationStatus());
        System.out.println("------------------------------------");
    }
}
