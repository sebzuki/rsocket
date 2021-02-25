package com.example.demo.service;

import com.example.demo.repository.domain.Person;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class KafkaEvent {

    private Person person;
}
