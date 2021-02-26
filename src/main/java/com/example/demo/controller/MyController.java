/*
 * Sébastien Leboucher
 */
package com.example.demo.controller;

import com.example.demo.repository.domain.Person;
import com.example.demo.service.MyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class MyController {
    private final MyService service;

    public MyController(MyService service) {
        this.service = service;
    }

    @GetMapping("subscribers")
    public int subscribers() {
        return service.subscribers();
    }

    @PostMapping("publish")
    public void publish() {
        service.publish();
    }

    @MessageMapping("findAll")
    public Flux<Person> findAll() {
        return service.findAll();
    }
}
