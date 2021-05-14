/*
 * Sébastien Leboucher
 */
package com.example.demo.controller;

import com.example.demo.configuration.AuthUtil;
import com.example.demo.controller.resource.SubcribeNotif;
import com.example.demo.repository.domain.Person;
import com.example.demo.service.MyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

@Controller
public class MyStream {
    private final MyService service;
    private final AuthUtil authUtil;

    public MyStream(MyService service, AuthUtil authUtil) {
        this.service = service;
        this.authUtil = authUtil;
    }

    // @PreAuthorize est optionnel, sauf qi on veut vérifier un / des roles précis
    // @PreAuthorize("hasRole('PRINT') && hasRole('LIST')")
    @MessageMapping({"notif"})
    public Flux<Person> stream(@RequestParam SubcribeNotif notif, @AuthenticationPrincipal Jwt jwt) {
        String login = authUtil.getLogin(jwt);
        return service.stream(login);
    }
}
