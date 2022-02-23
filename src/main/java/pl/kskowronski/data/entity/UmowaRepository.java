package pl.kskowronski.data.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UmowaRepository extends JpaRepository<Umowa, Long> {
    Optional<Umowa> findUmowaByUmNumer(String umNumer);
}
