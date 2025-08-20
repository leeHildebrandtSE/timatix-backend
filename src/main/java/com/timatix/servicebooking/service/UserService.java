package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.User;
import com.timatix.servicebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllMechanics() {
        return userRepository.findAllMechanics();
    }

    public List<User> getAllClients() {
        return userRepository.findAllClients();
    }

    public User createUser(User user) {
        validateUserForCreation(user);

        // In a real app, you'd hash the password here
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        log.info("Creating new user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(userDetails.getName());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());

        // Don't allow email or role changes in update
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            // Hash password in real implementation
            user.setPassword(userDetails.getPassword());
        }

        log.info("Updating user with id: {}", id);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private void validateUserForCreation(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
    }

    // Authentication method (simplified - use Spring Security in production)
    public Optional<User> authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }
}