package org.example.jpajava.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonsPkSeq {
    /*
    not supported by mariadb
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_generator")
    @SequenceGenerator(name="seq_generator", sequenceName = "cus_seq")
     */
    @Id
    @GeneratedValue(generator = "generatorseq")
    @GenericGenerator(
            name = "generatorseq",
            parameters = @Parameter(name = "seq-name", value = "cus_seq"),
            strategy = "org.example.jpajava.generator.GeneratorSeq"
    )
    private int personId;
    private String lastName;
    private String firstName;
    private int age;
}
