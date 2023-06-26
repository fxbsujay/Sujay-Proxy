package com.susu.proxy.core;

import com.susu.proxy.core.task.TaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TaskSchedulerTests {

    @Test
    public void taskSchedulerTest() {
        TaskScheduler scheduler = new TaskScheduler("Test-Task-Scheduler");

        scheduler.scheduleOnce("Test-Task-Once", new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    log.info("index={}", i);
                }
            }
        },10);

        scheduler.schedule("Test-Task", new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    log.info("index={}", i);
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void taskSchedulerDaemonTest() {
        TaskScheduler scheduler = new TaskScheduler("Test-Task-Scheduler", 1, true);

        scheduler.scheduleOnce("Test-Task-Once", new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    log.info("index={}", i);
                }
            }
        },10);

        scheduler.schedule("Test-Task", new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    log.info("index={}", i);
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
