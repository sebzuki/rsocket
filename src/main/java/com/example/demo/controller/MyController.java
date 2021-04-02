/*
 * Sébastien Leboucher
 */
package com.example.demo.controller;

import com.example.demo.configuration.AuthUtil;
import com.example.demo.service.MyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class MyController {
    private final MyService service;
    private final AuthUtil authUtil;


    public MyController(MyService service, AuthUtil authUtil) {
        this.service = service;
        this.authUtil = authUtil;
    }

    // @PreAuthorize est optionnel, sauf qi on veut vérifier un ou des roles précis
    // @PreAuthorize("hasRole('PRINT') && hasRole('LIST')")
    @GetMapping("subscribers")
    public Mono<Integer> subscribers() {
        return service.subscribers();
    }

    @GetMapping("ext")
    public Mono<ResponseEntity<Object>> callExt(@AuthenticationPrincipal Jwt jwt) {
        String login = authUtil.getLogin(jwt);
        return service.callExt(login);
    }

    @PostMapping("publish")
    public Mono<Void> publish() {
        service.publish();
        return Mono.empty();
    }
}
