package ua.com.valexa.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.db.model.TestEntity;

@Repository
public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
}
