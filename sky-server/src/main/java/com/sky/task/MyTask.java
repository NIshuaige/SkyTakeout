/**
 * @Author：乐
 * @Package：com.sky.task
 * @Project：sky-take-out
 * @name：MyTask
 * @Date：2024/3/13 0013  19:37
 * @Filename：MyTask
 */
package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自定义定时任务类
 */
@Component
@Slf4j
public class MyTask {


//    @Scheduled(cron = "0/5 * * * * ? ")
    public void testTask(){
        log.info("定时任务 task：{}",new Date());
    }
}
