package com.ferdican.restaurantsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "work_schedules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    // Haftanın günleri ve saat aralıkları
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "work_days", joinColumns = @JoinColumn(name = "schedule_id"))
    private List<WorkDay> workDays;
} 