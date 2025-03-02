package com.employee_management.demoemployee_management.service;

import com.employee_management.demoemployee_management.DTO.UserDTO;
import com.employee_management.demoemployee_management.exception.UserNotFoundException;
import com.employee_management.demoemployee_management.mode.User;
import com.employee_management.demoemployee_management.repository.ManagerRepository;
import com.employee_management.demoemployee_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ManagerRepository managerRepository;

    public User createUser(User user) {
//        user.setMobNum(user.getMobNum().replaceAll("[^0-9]", "").substring(Math.max(user.getMobNum().length() - 10, 0)));
        user.setMobNum(user.getMobNum().replaceAll("[^0-9]", "").substring(Math.max(user.getMobNum().length()-10, 0)));
        user.setPanNum(user.getPanNum().toUpperCase());

        if (user.getManagerId() != null && !managerRepository.existsByManagerId(user.getManagerId())) {
            throw new RuntimeException("Invalid Manager ID");
        }

        return userRepository.save(user);
    }

    public List<UserDTO> getUsers(String userId, String mobNum, String managerId) {
        List<User> users;

        if (userId != null) {
            users = userRepository.findById(userId).map(List::of).orElseThrow(() -> new UserNotFoundException("User not found"));
        } else if (mobNum != null) {
            users = userRepository.findByMobNum(mobNum).map(List::of).orElseThrow(() -> new UserNotFoundException("User not found"));
        } else if (managerId != null) {
            users = userRepository.findByManagerId(managerId);
        } else {
            users = userRepository.findAll();
        }

        return users.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void deleteUser(String userId, String mobNum) {
        Optional<User> user = userId != null ? userRepository.findById(userId) : userRepository.findByMobNum(mobNum);
        user.ifPresentOrElse(userRepository::delete, () -> {
            throw new UserNotFoundException("User not found");
        });
    }

    public String updateUser(List<String> userIds, Map<String, String> updateData) {
        if (userIds.isEmpty()) {
            throw new RuntimeException("No user_ids provided for update");
        }

        boolean bulkUpdate = userIds.size() > 1;
        if (bulkUpdate && (updateData.containsKey("full_name") || updateData.containsKey("mob_num") || updateData.containsKey("pan_num"))) {
            return "Only manager_id can be updated in bulk.";
        }

        for (String userId : userIds) {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                if (updateData.containsKey("full_name")) {
                    user.setFullName(updateData.get("full_name"));
                }

                if (updateData.containsKey("mob_num")) {
                    String mobNum = updateData.get("mob_num").replaceAll("[^0-9]", "").substring(Math.max(updateData.get("mob_num").length() - 10, 0));
                    user.setMobNum(mobNum);
                }

                if (updateData.containsKey("pan_num")) {
                    user.setPanNum(updateData.get("pan_num").toUpperCase());
                }

                if (updateData.containsKey("manager_id")) {
                    String newManagerId = updateData.get("manager_id");
                    if (!managerRepository.existsByManagerId(newManagerId)) {
                        throw new RuntimeException("Invalid Manager ID");
                    }

                    if (user.getManagerId() != null) {
                        user.setActive(false);
                        userRepository.save(user);
                        User newUser = new User();
                        newUser.setFullName(user.getFullName());
                        newUser.setMobNum(user.getMobNum());
                        newUser.setPanNum(user.getPanNum());
                        newUser.setManagerId(newManagerId);
                        newUser.setCreatedAt(user.getCreatedAt());
                        newUser.setUpdatedAt(LocalDateTime.now());
                        userRepository.save(newUser);
                    } else {
                        user.setManagerId(newManagerId);
                        user.setUpdatedAt(LocalDateTime.now());
                        userRepository.save(user);
                    }
                } else {
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                }
            } else {
                return "User with ID " + userId + " not found.";
            }
        }
        return "User(s) updated successfully!";
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setMobNum(user.getMobNum());
        dto.setPanNum(user.getPanNum());
        dto.setManagerId(user.getManagerId());
        dto.setActive(user.isActive());
        return dto;
    }
}