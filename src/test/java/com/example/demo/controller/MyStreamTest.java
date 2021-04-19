package com.example.demo.controller;

import com.example.demo.configuration.AuthUtil;
import com.example.demo.controller.resource.SubcribeNotif;
import com.example.demo.repository.domain.Person;
import com.example.demo.service.MyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyStreamTest {

    private MyStream myStream;

    @Mock private MyService service;
    @Mock private AuthUtil authUtil;

    @BeforeEach
    void init() {
        myStream = new MyStream(service, authUtil);
    }

    @Test
    void stream_must_call_service() {
        String login = "testLogin";
        SubcribeNotif notif = new SubcribeNotif();
        Flux<Person> resultMock = Flux.just(new Person()
                .setId(UUID.randomUUID())
                .setFirstName("firstName")
                .setLastName("lastname"));
        Jwt token = Jwt.withTokenValue("token")
                .header("test", "test")
                .claim("test", "test")
                .build();

        when(authUtil.getLogin(token)).thenReturn(login);
        when(service.stream(login)).thenReturn(resultMock);

        Flux<Person> personFlux = myStream.stream(notif, token);

        verify(service, times(1)).stream(login);
        assertThat(personFlux).isSameAs(resultMock);
    }
}
