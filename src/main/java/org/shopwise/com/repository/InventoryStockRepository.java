package org.shopwise.com.repository;

import org.shopwise.com.entity.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryStockRepository
        extends JpaRepository<InventoryStock, Long> {

    @Modifying
    @Query("""
        UPDATE InventoryStock i
        SET i.availableQuantity =
            i.availableQuantity - :quantity
        WHERE i.productId = :productId
          AND i.availableQuantity >= :quantity
    """)
    int reserveStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );
}
