package com.employee_management.demoemployee_management.mode;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "managers")
public class Manager {
    @Id
    private String managerId;
    private String name;
    private boolean isActive = true;
}