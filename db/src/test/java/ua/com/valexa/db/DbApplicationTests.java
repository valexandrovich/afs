package ua.com.valexa.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.valexa.db.model.red.GovUa01;
import ua.com.valexa.db.repository.red.GovUa01Repository;

import java.util.UUID;

@SpringBootTest(properties = {

        "spring.datasource.url=jdbc:postgresql://localhost:5432/otp",
        "spring.datasource.username=otp",
        "spring.datasource.password=otp",
        "spring.jpa.hibernate.ddl-auto=update",
        "logging.level.ua.com.valexa=debug",
})
class DbApplicationTests {

    @Autowired
    GovUa01Repository govUa01Repository;

    @Test
    void contextLoads() {

        GovUa01 govUa01 = new GovUa01();
        govUa01.setId(UUID.randomUUID());
        govUa01Repository.save(govUa01);

    }

    @Test
    void tst3(){
        GovUa01 r = new GovUa01();
        r.setCaseNumber("asdasd");
        r.setCourtName("asdasd");

        System.out.println(r.hashCode());
    }

}
