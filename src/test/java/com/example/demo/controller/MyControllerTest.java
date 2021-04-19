package com.example.demo.controller;

import com.example.demo.configuration.AuthUtil;
import com.example.demo.service.MyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MyController.class)
@Import(TestConfig.class)
class MyControllerTest {

    @MockBean private MyService service;
    @MockBean private AuthUtil authUtil;
    @Autowired private WebTestClient webClient;

    @Test
    void my_controller_must_call_service_callExt() {
        String login = "testLogin";
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(authUtil.getLogin(any())).thenReturn(login);
        when(service.callExt(login)).thenReturn(Mono.just(objectResponseEntity));

        webClient.get()
                .uri("/api/ext")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(0);

        verify(service, times(1)).callExt(login);
    }

}
