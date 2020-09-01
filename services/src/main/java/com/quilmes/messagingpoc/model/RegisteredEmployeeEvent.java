package com.quilmes.messagingpoc.model;

import lombok.*;
import org.springframework.lang.Nullable;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisteredEmployeeEvent {
    @NonNull
    private String eventId;

    @NonNull
    private String employeeId;

    @NonNull
    private EmployeeType employeeType;

    @Nullable
    private String outsourcingCompany;
}
