/*
 * Sébastien Leboucher
 */
package com.example.demo.service;

import com.example.demo.configuration.ReactiveRestTemplate;
import com.example.demo.repository.domain.Person;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

@Service
@Log4j2
public class MyService {
    private final KafkaTemplate<String, Person> kafkaTemplate;
    private final Sinks.Many<Person> stream;
    private final ReactiveRestTemplate restTemplate;

    public MyService(KafkaTemplate<String, Person> kafkaTemplate, ReactiveRestTemplate restTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
        this.stream = Sinks.many()
                .replay()
                .limit(3);
    }

    public void publish() {
        this.stream.emitNext(new Person()
                .setId(UUID.randomUUID())
                .setFirstName("Mark-" + Math.round(Math.random() * 100))
                .setLastName("Seb-" + Math.round(Math.random() * 100)), FAIL_FAST);
    }

//    @KafkaListener(topics = "test", groupId = "testseb")
    public void consumer(ConsumerRecord<String, Person> record) {
        this.stream.emitNext(record.value(), FAIL_FAST);
    }

    public Flux<Person> stream(String login) {
        return this.stream.asFlux();
    }

    public Mono<Integer> subscribers() {
        return Mono.just(this.stream.currentSubscriberCount());
    }

    public Mono<ResponseEntity<Object>> callExt(String login) {
        System.out.println(login);
        return restTemplate.getSecureTemplate()
                .map(template -> template.getForEntity("http://localhost:8083/toto?codeDepartement=34&commune=Lattes", Object.class));
    }
}
