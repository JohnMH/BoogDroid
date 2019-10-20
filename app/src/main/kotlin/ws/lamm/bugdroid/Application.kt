package ws.lamm.bugdroid

import android.content.Context

import com.activeandroid.query.Select

import ws.lamm.bugdroid.general.Server
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidCoreModule
import org.kodein.di.android.support.androidSupportModule

class Application : com.activeandroid.app.Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        import(androidCoreModule(this@Application))
        import(androidSupportModule(this@Application))
    }

    override fun onCreate() {
        super.onCreate()

        try {
            val dbServers = Select().from(ws.lamm.bugdroid.db.Server::class.java).execute<ws.lamm.bugdroid.db.Server>()

            Server.servers.clear()

            for (s in dbServers) {
                Server.servers.add(ws.lamm.bugdroid.bugzilla.Server(s))
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
