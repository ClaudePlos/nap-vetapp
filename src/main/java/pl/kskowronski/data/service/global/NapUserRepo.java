package pl.kskowronski.data.service.global;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kskowronski.data.entity.global.NapUser;

import java.math.BigDecimal;
import java.util.Optional;

public interface NapUserRepo extends JpaRepository<NapUser, BigDecimal>  {
    public Optional<NapUser> findByUsername(String username);
}

