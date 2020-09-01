package com.quilmes.messagingpoc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.lang.Nullable;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProcessNewEmployeeMessage {
    @NonNull
    @JsonProperty("messageId")
    private String messageId;

    @NonNull
    @JsonProperty("employeeId")
    private String employeeId;

    @NonNull
    @JsonProperty("employeeType")
    private EmployeeType employeeType;

    @Nullable
    @JsonProperty("outsourcingCompany")
    private String outsourcingCompany;
}
