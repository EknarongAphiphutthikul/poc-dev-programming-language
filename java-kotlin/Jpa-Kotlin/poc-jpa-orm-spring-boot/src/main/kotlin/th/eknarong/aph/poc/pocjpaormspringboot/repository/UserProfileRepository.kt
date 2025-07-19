package th.eknarong.aph.poc.pocjpaormspringboot.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import th.eknarong.aph.poc.pocjpaormspringboot.entity.UserProfile
import java.time.LocalDate

@Repository
interface UserProfileRepository : JpaRepository<UserProfile, Long> {
    
    @Query("SELECT up FROM UserProfile up WHERE up.user.id = :userId")
    fun findByUserId(@Param("userId") userId: Long): UserProfile?
    
    @Query("SELECT up FROM UserProfile up WHERE up.phoneNumber = :phoneNumber")
    fun findByPhoneNumber(@Param("phoneNumber") phoneNumber: String): UserProfile?
    
    @Query("SELECT up FROM UserProfile up WHERE up.birthDate >= :startDate AND up.birthDate <= :endDate")
    fun findByBirthDateBetween(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<UserProfile>
    
    @Query("SELECT up FROM UserProfile up WHERE up.bio IS NOT NULL AND up.bio != ''")
    fun findProfilesWithBio(): List<UserProfile>
    
    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE up.id = :id")
    fun findByIdWithUser(@Param("id") id: Long): UserProfile?
    
    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.profilePictureUrl IS NOT NULL")
    fun countProfilesWithPicture(): Long
}