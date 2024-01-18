package ua.com.valexa.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.valexa.db.model.sys.StoredJob;
import ua.com.valexa.db.model.sys.StoredStep;
import ua.com.valexa.db.repository.sys.StoredJobRepository;
import ua.com.valexa.db.repository.sys.StoredStepRepository;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SchedulerApplicationTests {


    @Autowired
    StoredJobRepository storedJobRepository;

    @Autowired
    StoredStepRepository storedStepRepository;

    @Test
    void contextLoads() {

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

    }

}
