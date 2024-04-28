package org.example.jpajava.controller;

import lombok.extern.slf4j.*;
import org.example.jpajava.entity.*;
import org.example.jpajava.repo.*;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class ExampleController {

    private final RepoPersonsPkSeq repoPersonsPkSeq;
    private final RepoPersonsPkAutoIn repoPersonsPkAutoIn;
    private final RepoPersonsPkTable repoPersonsPkTable;

    public ExampleController(RepoPersonsPkSeq repoPersonsPkSeq,
                             RepoPersonsPkAutoIn repoPersonsPkAutoIn,
                             RepoPersonsPkTable repoPersonsPkTable) {
        this.repoPersonsPkSeq = repoPersonsPkSeq;
        this.repoPersonsPkAutoIn = repoPersonsPkAutoIn;
        this.repoPersonsPkTable = repoPersonsPkTable;
    }

    @GetMapping("/persons-pk-seq")
    public String personPkSeq() {
        PersonsPkSeq en = new PersonsPkSeq();
        en.setAge(10);
        en.setFirstName("firstname");
        en.setLastName("lastname");
        repoPersonsPkSeq.save(en);
        log.info("persons-pk-seq: {}", en.getPersonId());
        return "persons-pk-seq called, pk="+en.getPersonId();
    }

    @GetMapping("/persons-pk-auto-in")
    public String personPkAutoIn() {
        PersonsPkAutoIn en = new PersonsPkAutoIn();
        en.setPersonId(3);
        en.setAge(10);
        en.setFirstName("firstname");
        en.setLastName("lastname");
        repoPersonsPkAutoIn.save(en);
        log.info("persons-pk-auto-in: {}", en.getPersonId());
        return "persons-pk-auto-in called, pk="+en.getPersonId();
    }

    @GetMapping("/persons-pk-auto")
    public String personPkAuto() {
        PersonsPkTable en = new PersonsPkTable();
        en.setAge(10);
        en.setFirstName("firstname");
        en.setLastName("lastname");
        repoPersonsPkTable.save(en);
        log.info("persons-pk-table: {}", en.getPersonId());
        return "persons-pk-table called, pk="+en.getPersonId();
    }
}
