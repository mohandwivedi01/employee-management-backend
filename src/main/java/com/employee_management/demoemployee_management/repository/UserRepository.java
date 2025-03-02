package com.employee_management.demoemployee_management.repository;


import com.employee_management.demoemployee_management.mode.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByMobNum(String mobNum);
    List<User> findByManagerId(String managerId);
}
