package ua.com.valexa.importer.service;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import ua.com.valexa.common.dto.sys.StepResponseDto;

import java.util.Map;

public interface Importable {
    StepResponseDto handleImport(Long stepId, Map<String, String> parameters) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException;
}
