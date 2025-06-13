package com.ferdican.restaurantsystem.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class WorkDay {
    private String dayOfWeek; // ör: MONDAY, TUESDAY
    private String startTime; // ör: 09:00
    private String endTime;   // ör: 17:00
} 