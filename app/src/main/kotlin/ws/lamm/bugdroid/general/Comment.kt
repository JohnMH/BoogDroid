package ws.lamm.bugdroid.general

abstract class Comment {
    var id: Int = 0
    var text: String = ""
    var author: User = User()
    var date: String = ""
    var number = 0

    var bug: Bug = ws.lamm.bugdroid.bugzilla.Bug()

    override fun toString(): String {
        return text
    }
}
