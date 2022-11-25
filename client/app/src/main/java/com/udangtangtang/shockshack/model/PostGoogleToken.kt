import com.google.gson.annotations.SerializedName

data class PostGoogleToken(
    @SerializedName("idToken")
    val token: String
)