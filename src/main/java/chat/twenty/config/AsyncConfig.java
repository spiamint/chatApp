package chat.twenty.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override   // ThreadPoolTaskExecutor 재정의
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1); // 기본 스레드 수
        executor.setMaxPoolSize(5); // 동시 처리 가능한 최대 스레드 수
        executor.setQueueCapacity(4); // 기본 스레드 수가 모두 사용중일 때, 추가로 생성할 수 있는 스레드 수
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

//    @Override // 예외처리 (로그만 찍음)
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() { new MyAsyncExceptionHandler() null; }
}
