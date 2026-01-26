package kr.co.yeogiga.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {
    public final static String MESSAGE_PUBLISH_TASK_EXECUTOR = "messagePublishTaskExecutor";
    
    /**
     * 이벤트 메시지 발행을 위한 ThreadPoolTaskExecutor 빈
     *
     * @return 비동기 이메일 발송 작업 용 ThreadPoolTaskExecutor
     */
    @Bean(name = MESSAGE_PUBLISH_TASK_EXECUTOR)
    public Executor messagePublishTaskExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cores * 2;
        int maxPoolSize = cores * 4;
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(100);
        
        executor.setThreadNamePrefix("message-publish-exec-");
        
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        
        return executor;
    }
}
