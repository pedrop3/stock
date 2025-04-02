package com.learn.stock.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter @Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "product")
public class Product implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int minStockLevel;
    private int maxStockLevel;
    private int currentStock;
    private boolean obsolete;

}
