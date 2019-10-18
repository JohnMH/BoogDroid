package ws.lamm.bugdroid

import android.content.Context

import com.activeandroid.query.Select

import me.johnmh.boogdroid.general.Server

class Application : com.activeandroid.app.Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            val dbServers = Select().from(me.johnmh.boogdroid.db.Server::class.java).execute<me.johnmh.boogdroid.db.Server>()

            Server.servers.clear()

            for (s in dbServers) {
                Server.servers.add(me.johnmh.boogdroid.bugzilla.Server(s))
            }

        } catch (e: Exception) {
        }

        appContext = applicationContext
    }

    companion object {

        var appContext: Context? = null
            private set
    }
}
