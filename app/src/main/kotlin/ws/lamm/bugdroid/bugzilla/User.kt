package ws.lamm.bugdroid.bugzilla

class User(email: String) {

// Real live example
// "email" : "expeditioneer",   // not usable since domain not given
// "id" : 93460,
// "name" : "expeditioneer",
// "real_name" : "Dennis Lamm"

    var id: Int = 0
    lateinit var name: String
    var email: String? = null
    var avatarUrl: String? = null

    init {
        name = email
        this.email = email
        avatarUrl = null
    }
}
