import com.squareup.moshi.Json

data class Candidate(
    val name: String,
    val profilepicture: String,
    val description: String,
    val votes: List<Int>
)