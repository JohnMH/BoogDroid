package ws.lamm.bugdroid

import android.app.Application
import android.content.Context
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidCoreModule
import org.kodein.di.android.support.androidSupportModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import ws.lamm.bugdroid.bugzilla.Server
import ws.lamm.bugdroid.orm.BugzillaServer
import ws.lamm.bugdroid.orm.MyObjectBox

class Application : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        import(androidCoreModule(this@Application))
        import(androidSupportModule(this@Application))

        bind() from singleton { MyObjectBox.builder()
                .androidContext(this)
                .build()
                .boxFor(BugzillaServer::class)}
    }

    override fun onCreate() {
        super.onCreate()

        val bugzillaServerBox : Box<BugzillaServer> by instance()

        try {
            Server.servers.clear()

            for (server in bugzillaServerBox.all) {
                Server.servers.add(server)            }

        } catch (e: Exception) {
        }

        appContext = applicationContext
    }

    companion object {

        lateinit var appContext: Context
            private set
    }
}
