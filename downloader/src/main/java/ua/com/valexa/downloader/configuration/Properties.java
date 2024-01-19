package ua.com.valexa.downloader.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class Properties {

    @Value("${downloader.mountPoint}")
    private String mountPoint;

    @Value("${proxyHost}")
    private String proxyHost;

    @Value("${proxyPort}")
    private Integer proxyPort;

    @Value("${cpms-queue}")
    private String cpmsQueue;


}
