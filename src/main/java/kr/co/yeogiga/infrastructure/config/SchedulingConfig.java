package kr.co.yeogiga.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(4);
        taskScheduler.setThreadNamePrefix("general-scheduling-");
        return taskScheduler;
    }
    
    @Bean(name = "eventCleanupTaskScheduler")
    public ThreadPoolTaskScheduler eventCleanupTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(4);
        taskScheduler.setThreadNamePrefix("event-cleanup-scheduling-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setAwaitTerminationSeconds(60);
        return taskScheduler;
    }
}
