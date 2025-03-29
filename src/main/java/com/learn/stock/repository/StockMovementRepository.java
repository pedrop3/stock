package com.learn.stock.repository;

import com.learn.stock.model.MovementType;
import com.learn.stock.model.Product;
import com.learn.stock.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProduct(Product product);

    @Query("""
                SELECT m.product, COUNT(m)
                FROM StockMovement m
                WHERE m.type = :type
                GROUP BY m.product
                ORDER BY COUNT(m) DESC
            """)
    Collection<Object[]> findTurnoverGrouped(@Param("type")  MovementType movementType);
}