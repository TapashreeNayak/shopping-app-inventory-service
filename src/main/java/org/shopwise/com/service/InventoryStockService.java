package org.shopwise.com.service;

import org.shopwise.com.event.OrderCreatedEvent;
import org.shopwise.com.event.OrderItemEvent;
import org.shopwise.com.exception.InsufficientInventoryException;
import org.shopwise.com.repository.InventoryStockRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryStockService {

    private final InventoryStockRepository stockRepository;

    public InventoryStockService(
            InventoryStockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void deductStock(OrderCreatedEvent event) {

        for (OrderItemEvent item : event.getItems()) {

            int updated = stockRepository.reserveStock(
                    item.getProductId(),
                    item.getQuantity()
            );

            if (updated == 0) {
                throw new InsufficientInventoryException(
                        "Insufficient stock for product ID: "
                                + item.getProductId()
                );
            }
        }
    }
}
