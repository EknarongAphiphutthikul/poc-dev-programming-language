package th.eknarong.aph.poc.pocjpaormspringboot.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateUserRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    val name: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String
)

data class UpdateUserRequest(
    @field:Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    val name: String?,
    
    @field:Email(message = "Email must be valid")
    val email: String?
)

data class CreateUserProfileRequest(
    val phoneNumber: String?,
    val birthDate: LocalDate?,
    @field:Size(max = 500, message = "Bio must not exceed 500 characters")
    val bio: String?,
    val profilePictureUrl: String?
)

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val createdAt: String,
    val profile: UserProfileResponse?
)

data class UserProfileResponse(
    val id: Long,
    val phoneNumber: String?,
    val birthDate: LocalDate?,
    val bio: String?,
    val profilePictureUrl: String?
)