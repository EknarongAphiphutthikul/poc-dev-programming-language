package th.eknarong.aph.poc.pocjpaormspringboot

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<PocJpaOrmSpringBootApplication>().with(TestcontainersConfiguration::class).run(*args)
}
