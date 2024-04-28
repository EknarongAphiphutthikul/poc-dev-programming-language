package org.example.jpajava.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonsPkAutoIn {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(generator = "overridepk")
    @GenericGenerator(
            name = "overridepk",
            strategy = "org.example.jpajava.generator.GeneratorOverridePk"
    )
    private int personId;
    private String lastName;
    private String firstName;
    private int age;
}
