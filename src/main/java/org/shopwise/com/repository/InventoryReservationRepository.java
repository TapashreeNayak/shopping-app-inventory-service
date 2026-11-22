package org.shopwise.com.repository;


import org.shopwise.com.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryReservationRepository extends
        JpaRepository<InventoryReservation, Long> {

    boolean existsByOrderId(Long orderId);

    Optional<InventoryReservation> findByOrderId(Long orderId);
}
