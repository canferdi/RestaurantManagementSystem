package com.ferdican.restaurantsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "waiter_profile")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Waiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;

    private String firstName;
    private String lastName;
    private String phoneNumber;

    public Waiter(Users users) {
        this.userId = users;
    }

}
