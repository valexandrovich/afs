package ua.com.valexa.importer.service.govua;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepResponseDto;
import ua.com.valexa.importer.service.Importable;

import java.util.Map;

@Service("govua01")
public class Govua01Importer implements Importable {

    @Autowired
    JobLauncher jobLauncher;


    @Autowired
    @Qualifier("govua01job")
    Job job;
    @Override
    public StepResponseDto handleImport(Long stepId, Map<String, String> parameters) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        System.out.println("Import " + stepId + " " + parameters);


        JobParameters jp = new JobParametersBuilder()
                .addString("file", parameters.get("file"))
                .toJobParameters();

        jobLauncher.run(job, jp);


        return null;
    }
}
