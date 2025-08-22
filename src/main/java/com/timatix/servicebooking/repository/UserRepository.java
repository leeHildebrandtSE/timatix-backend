package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(User.Role role);

    List<User> findByRoleAndNameContainingIgnoreCase(User.Role role, String name);

    boolean existsByEmail(String email);

    List<User> findAllByRole(User.Role role);

    default List<User> findAllMechanics() {
        return findAllByRole(User.Role.MECHANIC);
    }

    default List<User> findAllClients() {
        return findAllByRole(User.Role.CLIENT);
    }
}
