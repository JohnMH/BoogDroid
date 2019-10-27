package ws.lamm.bugdroid.orm

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class BugzillaServer {

    @Id
    var id: Long = 0

    var type: String = ""

    var name: String = ""

    var url: String = ""

    var user: String = ""

    var password: String = ""

    var apiKey: String = ""

    @Convert( converter = WebService.Converter::class, dbType = Integer::class)
    var webService: WebService = WebService.REST

//    constructor(type: String, name: String) {
//        this.type = type
//        this.name = name
//    }
}
