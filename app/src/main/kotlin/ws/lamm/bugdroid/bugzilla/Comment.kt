package ws.lamm.bugdroid.bugzilla

class Comment {
    var id: Int = 0
    var text: String = ""
    lateinit var author: User
    var date: String = ""
    var number = 0

    var bug: Bug = Bug()

    override fun toString(): String {
        return text
    }
}
