package th.eknarong.aph.poc.pocjpaormspringboot.model

import java.time.LocalDate

data class UserProfileSearchResponse(
    val id: Long? = null,
    val phoneNumber: String? = null,
    val birthDate: LocalDate? = null,
    val bio: String? = null,
    val profilePictureUrl: String? = null,
    val user: UserSearchResponse? = null
)