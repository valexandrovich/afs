package ua.com.valexa.db.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.db.model.sys.StoredStep;

@Repository
public interface StoredStepRepository extends JpaRepository<StoredStep, Long> {
}
