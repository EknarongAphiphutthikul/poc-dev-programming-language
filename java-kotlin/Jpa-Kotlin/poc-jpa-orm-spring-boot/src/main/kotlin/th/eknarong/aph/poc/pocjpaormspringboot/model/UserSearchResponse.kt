package th.eknarong.aph.poc.pocjpaormspringboot.model

import java.time.LocalDateTime

data class UserSearchResponse(
    val id: Long? = null,
    val email: String? = null,
    val name: String? = null,
    val createdAt: LocalDateTime? = null,
    val profile: UserProfileSearchResponse? = null,
    val orders: MutableList<OrderSearchResponse>? = null
) 