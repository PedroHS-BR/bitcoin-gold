package com.hidra.bitcoingold.repository;

import com.hidra.bitcoingold.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    UserDetails findUserByEmail(String email);

    Optional<User> findByEmail(String email);
    User findByWalletId(String walletId);

}
