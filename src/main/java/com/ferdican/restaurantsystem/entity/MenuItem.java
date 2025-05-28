package com.ferdican.restaurantsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class MenuItem {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    private String description;

    private Double price;

    private String imageUrl;

    //@Enumerated(EnumType.STRING)
    private String category;

}
