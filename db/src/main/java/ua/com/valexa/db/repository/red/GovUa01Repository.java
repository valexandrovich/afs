package ua.com.valexa.db.repository.red;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.db.model.red.GovUa01;

import java.util.UUID;

@Repository
public interface GovUa01Repository extends JpaRepository<GovUa01, UUID> {
}
