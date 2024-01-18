package ua.com.valexa.db.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.db.model.sys.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}
