package com.ferdican.restaurantsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UsersType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTypeId;

    private String userTypeName;

    @OneToMany(targetEntity = Users.class, mappedBy = "userTypeId", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Users> users;

}
