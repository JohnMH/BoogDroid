package ws.lamm.bugdroid.orm

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.objectbox.kotlin.boxFor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class BugzillaServerTest {

    //@TempDir
    private val testDirectory: File = File("objectbox-example/test-db")

    private lateinit var store: BoxStore

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        BoxStore.deleteAllFiles(testDirectory)

        store = MyObjectBox.builder()
                .directory(testDirectory)
                .debugFlags(DebugFlags.LOG_QUERIES)
                .build()
    }

    @AfterEach
    @Throws(Exception::class)
    fun tearDown() {
        store.close()

        BoxStore.deleteAllFiles(testDirectory)
    }



    @Test
    @Throws(Exception::class)
    fun playground() {
        val bugzillaServer: Box<BugzillaServer> = store.boxFor()

        bugzillaServer
    }
}