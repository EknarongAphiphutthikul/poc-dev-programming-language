package th.eknarong.aph.poc.autogent.jpabuddy.entity1

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.Instant

@Entity
@Table(name = "users")
open class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    open var email: String? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    open var name: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: Instant? = null

    @OneToMany(mappedBy = "user")
    open var orders: MutableSet<Order> = mutableSetOf()

    @OneToMany(mappedBy = "user")
    open var userProfiles: MutableSet<UserProfile> = mutableSetOf()
}