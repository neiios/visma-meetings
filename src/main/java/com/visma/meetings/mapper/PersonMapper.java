package com.visma.meetings.mapper;

import com.visma.meetings.dto.PersonDTO;
import com.visma.meetings.model.Person;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Service;

@Service
@UtilityClass
public class PersonMapper {
    public static PersonDTO personToDTO(Person person) {
        return new PersonDTO(
                person.getId(),
                person.getName()
        );
    }

    public static Person dtoToPerson(PersonDTO person) {
        return new Person(
                person.id(),
                person.name()
        );
    }
}
