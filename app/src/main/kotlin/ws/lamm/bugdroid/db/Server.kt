package ws.lamm.bugdroid.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

@Table(name = "Servers")
class Server : Model() {
    @Column(name = "type")
    var type: String? = null
    @Column(name = "name")
    var name: String? = null
    @Column(name = "url")
    var url: String? = null
    @Column(name = "user")
    var user: String? = null
    @Column(name = "password")
    var password: String? = null
    @Column(name = "json")
    var json: Boolean? = null
}
