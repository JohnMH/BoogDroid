package ws.lamm.bugdroid.general

import ws.lamm.bugdroid.bugzilla.ChangeStatusInfo

open class StatusInfo {
    var isOpen: Boolean = false
    var name: String = ""
    var changeList: List<ChangeStatusInfo>? = null

    override fun toString(): String {
        return name
    }
}
