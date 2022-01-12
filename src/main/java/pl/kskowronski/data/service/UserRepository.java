package pl.kskowronski.data.service;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kskowronski.data.entity.admin.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}