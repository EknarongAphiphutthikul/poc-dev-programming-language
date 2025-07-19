package th.eknarong.aph.poc.pocjpaormspringboot.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "user_profiles")
data class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "phone_number")
    val phoneNumber: String?,
    
    @Column(name = "birth_date")
    val birthDate: LocalDate?,
    
    @Column(length = 500)
    val bio: String?,
    
    @Column(name = "profile_picture_url")
    val profilePictureUrl: String?,
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)