package com.employee_management.demoemployee_management.controller;

import com.employee_management.demoemployee_management.DTO.UserDTO;
import com.employee_management.demoemployee_management.mode.User;
import com.employee_management.demoemployee_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String healthChekc(){
        return "I'm runing...";
    }

    @PostMapping("/create_user")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/get_users")
    public List<UserDTO> getUsers(@RequestBody Map<String, String> filters) {
        return userService.getUsers(filters.get("user_id"), filters.get("mob_num"), filters.get("manager_id"));
    }

    @PostMapping("/delete_user")
    public String deleteUser(@RequestBody Map<String, String> request) {
        userService.deleteUser(request.get("user_id"), request.get("mob_num"));
        return "User deleted successfully!";
    }

    @PostMapping("/update_user")
    public String updateUser(@RequestBody Map<String, Object> request) {
        // Safe type casting using explicit conversion
        Object userIdsObj = request.get("user_ids");
        Object updateDataObj = request.get("update_data");

        if (!(userIdsObj instanceof List<?>)) {
            return "Invalid user_ids format. Must be a list.";
        }
        if (!(updateDataObj instanceof Map<?, ?>)) {
            return "Invalid update_data format. Must be an object.";
        }

        @SuppressWarnings("unchecked")
        List<String> userIds = (List<String>) userIdsObj;

        @SuppressWarnings("unchecked")
        Map<String, String> updateData = (Map<String, String>) updateDataObj;

        return userService.updateUser(userIds, updateData);
    }
}