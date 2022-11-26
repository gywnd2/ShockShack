import android.widget.EditText
import com.google.gson.annotations.SerializedName

data class standardMemberModel(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("userType")
    val type: String
)