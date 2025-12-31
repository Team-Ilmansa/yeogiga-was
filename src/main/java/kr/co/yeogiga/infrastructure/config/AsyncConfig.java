package kr.co.yeogiga.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {
    public final static String EMAIL_TASK_EXECUTOR = "emailTaskExecutor";
    
    /**
     * 이메일 발송 작업 수행을 위한 ThreadPoolTaskExecutor 빈
     *
     * <p> 이메일 발송 로직 성능 측정 데이터 기반 corePoolSize 할당
     * <p> 산정 공식: cores * (1 + wait_time / service_time)
     *
     * <p> 예기치 못한 프로세스 종료 시에도, 대기 작업 수행을 위한 graceful shutdown 적용
     *
     * @return 비동기 이메일 발송 작업 용 ThreadPoolTaskExecutor
     */
    @Bean(name = EMAIL_TASK_EXECUTOR)
    public Executor emailTaskExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cores * 7;
        int maxPoolSize = corePoolSize * 2;
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(100);
        
        executor.setThreadNamePrefix("email-exec-");
        
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        
        return executor;
    }
}
