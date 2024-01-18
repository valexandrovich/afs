package ua.com.valexa.downloader.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class Properties {

    @Value("${downloader.mountPoint}")
    private String mountPoint;

    @Value("${downloader.proxyHost}")
    private String proxyHost;

    @Value("${downloader.proxyPort}")
    private Integer proxyPort;


}
