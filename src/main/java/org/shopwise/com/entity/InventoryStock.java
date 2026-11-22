package org.shopwise.com.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "INVENTORY_STOCK")
public class InventoryStock {

    @Id
    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "AVAILABLE_QUANTITY")
    private Integer availableQuantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    // Getters and setters
}
