package com.visma.meetings.mapper;

import com.visma.meetings.dto.PersonDTO;
import com.visma.meetings.model.Person;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PersonMapper implements Function<PersonDTO, Person> {
    @Override
    public Person apply(PersonDTO personDTO) {
        return new Person(
                personDTO.id(),
                personDTO.name());
    }
}
