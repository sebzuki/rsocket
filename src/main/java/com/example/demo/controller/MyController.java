/*
 * Sébastien Leboucher
 */
package com.example.demo.controller;

import com.example.demo.service.MyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class MyController {
    private final MyService service;

    public MyController(MyService service) {
        this.service = service;
    }

    @GetMapping("subscribers")
    public Mono<Integer> subscribers() {
        return Mono.just(service.subscribers());
    }

    @PostMapping("publish")
    public Mono<Void> publish() {
        service.publish();
        return Mono.empty();
    }
}
