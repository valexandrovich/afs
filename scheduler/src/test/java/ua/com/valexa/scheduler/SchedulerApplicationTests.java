package ua.com.valexa.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import ua.com.valexa.db.model.enums.StepStatus;
import ua.com.valexa.db.model.sys.Step;
import ua.com.valexa.db.model.sys.StoredJob;
import ua.com.valexa.db.model.sys.StoredStep;
import ua.com.valexa.db.repository.sys.StepRepository;
import ua.com.valexa.db.repository.sys.StoredJobRepository;
import ua.com.valexa.db.repository.sys.StoredStepRepository;

import java.util.HashMap;
import java.util.Map;

//@SpringBootTest
//@TestPropertySource(locations = "classpath:classpath../application.properties")

//@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.port=5672",
        "spring.rabbitmq.username=otp",
        "spring.rabbitmq.password=otp",

        "spring.datasource.url=jdbc:postgresql://localhost:5432/otp",
        "spring.datasource.username=otp",
        "spring.datasource.password=otp",


        "logging.level.ua.com.valexa=debug",
        "scheduler-init-queue=scheduler-init",
        "scheduler-response-queue=scheduler-response",

        "server.port=10001"
})
//@TestPropertySource(locations = "file:../../../../../../../../application.properties")
class SchedulerApplicationTests {


    @Autowired
    StoredJobRepository storedJobRepository;

    @Autowired
    StoredStepRepository storedStepRepository;

    @Autowired
    StepRepository stepRepository;


    @Test
    void tst01(){
        Step s = new Step();
        s.setStatus(StepStatus.NEW);
        s.setComment("asdasda");
        s = stepRepository.save(s);


        Step s1 = new Step();
        s1.setStatus(StepStatus.NEW);
        s1.setComment("asdasda");
        s1 = stepRepository.save(s1);

        System.out.println("a");

    }

    @Test
    void govua01() {

        StoredJob sj = new StoredJob();
        sj.setId(1L);
        sj.setName("Банкроти");
        sj.setDescription("Відомості про справи про банкрутство - Державна судова адміністрація України");
        sj = storedJobRepository.save(sj);

        StoredStep ss = new StoredStep();
        ss.setStoredJob(sj);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("packageId", "vidomosti-pro-spravi-pro-bankrutstvo-1");
        parameters.put("sourceName", "govua01");
        parameters.put("retries", "3");

        ss.setParameters(parameters);
        ss.setServiceName("downloader");
        ss.setWorkerName("govua");
        ss.setStepOrder(1);
        ss = storedStepRepository.save(ss);


        StoredStep ss2 = new StoredStep();
        ss2.setStoredJob(sj);

        ss2.setServiceName("importer");
        ss2.setWorkerName("govua01");
        ss2.setStepOrder(2);
        ss2 = storedStepRepository.save(ss2);

    }


    @Test
    void govua02() {

        StoredJob sj = new StoredJob();
        sj.setId(2L);
        sj.setName("Перелік підприємств-боржників");
        sj.setDescription("Дані про платників, що мають заборгованість із платежів до Пенсійного фонду України - Пенсійний Фонд");
        sj = storedJobRepository.save(sj);

        StoredStep ss = new StoredStep();
        ss.setStoredJob(sj);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("packageId", "1e2d305c-6ecb-48d7-908c-b3c27b8a9acf");
        parameters.put("sourceName", "govua02");
        parameters.put("retries", "3");

        ss.setParameters(parameters);
        ss.setServiceName("downloader");
        ss.setWorkerName("govua");
        ss.setStepOrder(1);
        ss = storedStepRepository.save(ss);

    }

    @Test
    void govua04() {

        StoredJob sj = new StoredJob();
        sj.setId(2L);
        sj.setName("Перелік підприємств-боржників");
        sj.setDescription("Дані про платників, що мають заборгованість із платежів до Пенсійного фонду України - Пенсійний Фонд");
        sj = storedJobRepository.save(sj);

        StoredStep ss = new StoredStep();
        ss.setStoredJob(sj);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("packageId", "1e2d305c-6ecb-48d7-908c-b3c27b8a9acf");
        parameters.put("sourceName", "govua02");
        parameters.put("retries", "3");

        ss.setParameters(parameters);
        ss.setServiceName("downloader");
        ss.setWorkerName("govua");
        ss.setStepOrder(1);
        ss = storedStepRepository.save(ss);

    }


    @Test
    void govua06() {

        StoredJob sj = new StoredJob();
        sj.setId(6L);
        sj.setName("Інформація з автоматизованої системи виконавчого провадження");
        sj.setDescription("Інформація з автоматизованої системи виконавчого провадження - Міністерство юстиції України");
        sj = storedJobRepository.save(sj);

        StoredStep ss = new StoredStep();
        ss.setStoredJob(sj);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("packageId", "6c0eb6c0-d19a-4bb0-869b-3280df46800a");
        parameters.put("sourceName", "govua02");
        parameters.put("retries", "3");

        ss.setParameters(parameters);
        ss.setServiceName("downloader");
        ss.setWorkerName("govua");
        ss.setStepOrder(1);
        ss = storedStepRepository.save(ss);

    }

}
