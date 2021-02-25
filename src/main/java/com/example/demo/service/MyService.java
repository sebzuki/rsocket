/*
 * Sébastien Leboucher
 */
package com.example.demo.service;

import com.example.demo.repository.domain.Person;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
@Log4j2
public class MyService /*implements ApplicationEventPublisherAware*/ {
    private final KafkaTemplate<String, Person> kafkaTemplate;
    private final Sinks.Many<Person> persons;

    public MyService(KafkaTemplate<String, Person> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.persons = Sinks.many()
                .replay()
                .limit(3);
    }

    public Mono<Void> publish() {
        UUID uuid = UUID.randomUUID();
        kafkaTemplate.send("test",
                uuid.toString(),
                new Person()
                        .setId(uuid)
                        .setFirstName("Mark-" + Math.round(Math.random() * 100))
                        .setLastName("Seb-" + Math.round(Math.random() * 100))
        ).completable();
        return Mono.empty();
    }

    @KafkaListener(topics = "test", groupId = "testseb")
    public void consumer(ConsumerRecord<String, Person> record) {
        this.persons.emitNext(record.value(), Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public Flux<Person> findAll() {
        return this.persons.asFlux();
    }

    public int subscribers() {
        return this.persons.currentSubscriberCount();
    }
}
