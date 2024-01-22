package ua.com.valexa.downloader;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
class DownloaderApplicationTests {





}
