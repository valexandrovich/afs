package ua.com.valexa.importer.service;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepRequestDto;
import ua.com.valexa.common.dto.sys.StepResponseDto;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ImporterService {

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    final ApplicationContext applicationContext;

    @Autowired
    private Importable importable ;

    public ImporterService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public CompletableFuture<StepResponseDto> handleImport(StepRequestDto stepRequestDto) {

        return CompletableFuture.supplyAsync(() -> {
                return importable.handleImport(stepRequestDto.getId(), stepRequestDto.getParameters());
        }, executorService);
    }
}
