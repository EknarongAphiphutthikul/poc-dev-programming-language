package org.example.jpajava.repo;

import org.example.jpajava.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface RepoPersonsPkTable extends JpaRepository<PersonsPkTable, Long> {
}
