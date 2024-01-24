package ua.com.valexa.transformer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepRequestDto;
import ua.com.valexa.common.dto.sys.StepResponseDto;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TransformerService {



    ExecutorService executorService = Executors.newFixedThreadPool(2);
    final ApplicationContext applicationContext;

    @Autowired
    private Transformable transformable ;

    public TransformerService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public CompletableFuture<StepResponseDto> handleTransform(StepRequestDto stepRequestDto) {
        return CompletableFuture.supplyAsync(() -> {
            return transformable.handleTransform(stepRequestDto.getId(), stepRequestDto.getParameters());
        }, executorService);
    }

}
