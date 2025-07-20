package th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(unique = true, nullable = false)
    var email: String? = null,
    
    @Column(nullable = false)
    var name: String? = null,
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null,

    // @JsonIgnore ignores this field when serializing to JSON
    // because return entity to client side, it loads table user profile
    @JsonIgnore
    // mappedBy not working because fk is in UserProfile table
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = false, orphanRemoval = false)
    @JoinColumn(name = "id", referencedColumnName = "user_id")
    var profile: UserProfile? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var orders: MutableList<Order>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , email = $email , name = $name , createdAt = $createdAt )"
    }
}