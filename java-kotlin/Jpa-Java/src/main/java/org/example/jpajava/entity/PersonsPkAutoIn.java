package org.example.jpajava.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonsPkAutoIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int personId;
    private String lastName;
    private String firstName;
    private int age;
}
