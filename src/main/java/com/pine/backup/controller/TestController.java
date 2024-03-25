package com.pine.backup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试 controller
 *
 * @author pine
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String virtualThreadTest() {

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()){
            executor.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return "Hello, new template";
    }

}
