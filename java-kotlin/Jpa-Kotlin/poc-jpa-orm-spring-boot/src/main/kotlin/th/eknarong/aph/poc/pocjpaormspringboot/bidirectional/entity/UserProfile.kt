package th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "user_profiles") 
class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(name = "phone_number")
    var phoneNumber: String?,
    
    @Column(name = "birth_date")
    var birthDate: LocalDate?,
    
    @Column(length = 500)
    var bio: String?,
    
    @Column(name = "profile_picture_url")
    var profilePictureUrl: String?,
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
)