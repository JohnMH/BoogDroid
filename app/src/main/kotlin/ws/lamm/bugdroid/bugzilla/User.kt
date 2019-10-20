package ws.lamm.bugdroid.bugzilla

import ws.lamm.bugdroid.general.User

class User(email: String) : User() {
    init {
        name = email
        this.email = email
        avatarUrl = null
    }
}
