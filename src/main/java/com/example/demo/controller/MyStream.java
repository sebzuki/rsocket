/*
 * Sébastien Leboucher
 */
package com.example.demo.controller;

import com.example.demo.configuration.ClaimsUtil;
import com.example.demo.controller.resource.SubcribeNotif;
import com.example.demo.repository.domain.Person;
import com.example.demo.service.MyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Controller
public class MyStream {
    private final MyService service;

    public MyStream(MyService service) {
        this.service = service;
    }

    // @PreAuthorize est optionnel, sauf qi on veut vérifier un / des roles précis
    // @PreAuthorize("hasRole('PRINT') && hasRole('LIST')")
    @MessageMapping({"stream"})
    public Flux<Person> stream(@RequestParam SubcribeNotif notif, @AuthenticationPrincipal Jwt jwt) {
        String login = ClaimsUtil.getLogin(jwt);

        return service.stream();
    }
}
