package chat.twenty.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * apache.httpClient 라이브러리를 사용하여 ConnectionPool 사용하는 RestTemplate 빌드
 * ( gpt 서버는 keep-alive 를 지원한다,  Connection:"keep-alive" )
 */

@Configuration
public class RestTemplateConfig {

    @Bean
    HttpClient httpClient() {
        return HttpClientBuilder.create()
                .setMaxConnTotal(20)    // 최대 거넥션
                .build();
    }

    @Bean
    HttpComponentsClientHttpRequestFactory factory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setReadTimeout(20000); // 수신 timeout 20초
        factory.setConnectTimeout(20000); // 연결 timeout 20초
        return factory;
    }

    @Bean
    RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }
}

