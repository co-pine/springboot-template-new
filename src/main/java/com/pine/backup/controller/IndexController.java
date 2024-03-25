package com.pine.backup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制器
 *
 * @author pine
 */
@RestController
@RequestMapping
public class IndexController {

    @GetMapping
    public String health() {
        return "Hello, new template";
    }

}
