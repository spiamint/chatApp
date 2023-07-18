package chat.twenty.config;

import chat.twenty.interceptor.HttpLogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfigurer 구현체.
 * 인터셉터 등록에 사용됨.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new HttpLogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/css/**",
                        "https://maxcdn.bootstrapcdn.com/**", "/static-image/**");
    }
}
