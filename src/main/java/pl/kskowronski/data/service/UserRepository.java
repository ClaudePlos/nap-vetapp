package pl.kskowronski.data.service;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kskowronski.data.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}