package eeet2580.kunlun.opwa.backend.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Replace the default Jackson converter with one using our custom ObjectMapper
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (int i = 0; i < messageConverters.size(); i++) {
            HttpMessageConverter<?> converter = messageConverters.get(i);
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                messageConverters.set(i, mappingJackson2HttpMessageConverter());
            }
        }

        return restTemplate;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
