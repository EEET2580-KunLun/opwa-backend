package eeet2580.kunlun.opwa.backend.external.pawa.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ServiceConfig {

    @Value("${pawa.service.url:https://localhost:8443}")
    private String pawaServiceUrl;

    @Value("${pawa.service.timeout:5000}")
    private int requestTimeout;
}
