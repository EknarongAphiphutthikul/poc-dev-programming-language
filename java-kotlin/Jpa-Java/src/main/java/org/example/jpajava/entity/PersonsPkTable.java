package org.example.jpajava.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonsPkTable {
    /*
     not supported by mariadb
     @GeneratedValue(strategy = GenerationType.AUTO)
     */
    @Id
    @GeneratedValue(generator = "generatorauto")
    @GenericGenerator(
            name = "generatorauto",
            strategy = "org.example.jpajava.generator.GeneratorAuto"
    )
    private int personId;
    private String lastName;
    private String firstName;
    private int age;
}
