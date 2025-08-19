package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(User.Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.name ILIKE %:name%")
    List<User> findByRoleAndNameContainingIgnoreCase(@Param("role") User.Role role, @Param("name") String name);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = 'MECHANIC'")
    List<User> findAllMechanics();

    @Query("SELECT u FROM User u WHERE u.role = 'CLIENT'")
    List<User> findAllClients();
}