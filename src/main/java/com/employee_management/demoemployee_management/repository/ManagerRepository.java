package com.employee_management.demoemployee_management.repository;


import com.employee_management.demoemployee_management.mode.Manager;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ManagerRepository extends MongoRepository<Manager, String> {
    boolean existsByManagerId(String managerId);
}
