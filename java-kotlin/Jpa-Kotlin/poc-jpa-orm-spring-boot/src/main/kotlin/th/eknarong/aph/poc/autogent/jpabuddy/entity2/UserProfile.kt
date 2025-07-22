package th.eknarong.aph.poc.autogent.jpabuddy.entity2

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDate

@Entity
@Table(name = "user_profiles")
open class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    open var phoneNumber: String? = null

    @Column(name = "birth_date")
    open var birthDate: LocalDate? = null

    @Size(max = 500)
    @Column(name = "bio", length = 500)
    open var bio: String? = null

    @Size(max = 255)
    @Column(name = "profile_picture_url")
    open var profilePictureUrl: String? = null
}