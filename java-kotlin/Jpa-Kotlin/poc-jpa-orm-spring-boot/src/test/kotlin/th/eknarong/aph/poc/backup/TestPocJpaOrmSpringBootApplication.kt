package th.eknarong.aph.poc.backup

import org.springframework.boot.fromApplication
import org.springframework.boot.with
import th.eknarong.aph.poc.pocjpaormspringboot.PocJpaOrmSpringBootApplication


fun main(args: Array<String>) {
    fromApplication<PocJpaOrmSpringBootApplication>().with(TestcontainersConfiguration::class).run(*args)
}
