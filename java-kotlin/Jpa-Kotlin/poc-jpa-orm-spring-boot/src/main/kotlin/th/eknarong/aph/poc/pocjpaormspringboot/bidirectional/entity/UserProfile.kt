package th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.Hibernate
import java.time.LocalDate

@Entity
@Table(name = "user_profiles") 
class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(name = "phone_number")
    var phoneNumber: String? = null,
    
    @Column(name = "birth_date")
    var birthDate: LocalDate? = null,
    
    @Column(length = 500)
    var bio: String? = null,
    
    @Column(name = "profile_picture_url")
    var profilePictureUrl: String? = null,

    // @JsonIgnore ignores this field when serializing to JSON
    // because return entity to client side, it loads table user
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false, orphanRemoval = false)
    // user_id column in the user_profiles table
    // id column in the users table
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    var user: User? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserProfile

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , phoneNumber = $phoneNumber , birthDate = $birthDate , bio = $bio , profilePictureUrl = $profilePictureUrl )"
    }
}