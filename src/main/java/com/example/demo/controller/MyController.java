/*
 * Sébastien Leboucher
 */
package com.example.demo.controller;

import com.example.demo.configuration.ClaimsUtil;
import com.example.demo.controller.resource.SubcribeNotif;
import com.example.demo.repository.domain.Person;
import com.example.demo.service.MyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
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

    // @PreAuthorize est optionnel, sauf qi on veut vérifier un / des roles précis
    // @PreAuthorize("hasRole('PRINT') && hasRole('LIST')")
    @MessageMapping({"stream"})
    public Flux<Person> stream(@RequestParam SubcribeNotif notif, @AuthenticationPrincipal Jwt jwt) {
        String login = ClaimsUtil.getLogin(jwt);

        return service.stream();
    }
}
