package com.ferdican.restaurantsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "tables")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class RestorantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int number; // Masa numarası

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status = TableStatus.EMPTY;

    @ManyToOne
    @JoinColumn(name = "waiter_id")
    private Waiter waiter; // Atanmış garson (opsiyonel)

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastOrderTime;
} 