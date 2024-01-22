package ua.com.valexa.importer.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class Properties {


    @Value("${cpms-queue}")
    private String cpmsQueue;


}
