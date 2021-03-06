package mobile.addons.cblock.data

import android.os.Build
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import mobile.addons.cblock.ext.log
import mobile.addons.cblock.ext.logSubscribe
import mobile.addons.cblock.ext.logThis
import mobile.addons.cblock.ext.safeUnsubscribe
import mobile.addons.cblock.model.CallLogModel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import rx.Observable
import rx.observers.TestSubscriber
import javax.inject.Inject


/**
 * Android test for [CallLogModel]
 */
@RunWith(AndroidJUnit4::class)
class CallLogModelTest : BaseModelTest() {

    @Inject lateinit var callLogModel: CallLogModel

    @Before
    fun setUp() {
        // grant permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val ui = getInstrumentation().uiAutomation
            val grant = "pm grant " + getTargetContext().packageName
            ui.executeShellCommand("$grant android.permission.READ_CALL_LOG")
            ui.executeShellCommand("$grant android.permission.WRITE_CALL_LOG")
        }

        // provide dependencies
        testComponent().inject(this)
    }

    /**
     * Subscribe to update from call(incoming and missed) log list
     * To make incoming call on emulator, click (...) "more" in bottom right corner and select "Phone"
     */
    @Test
    fun subscribeToCallLog() {
        val testSubscriber = TestSubscriber<List<CallLogItem>>()

        // check subscription without errors and not completed (wait for update from call log)
        val subscription = callLogModel.getCallLogList()
                .doOnSubscribe { logSubscribe("getCallLogList") }
                .doOnNext { logThis("getCallLogList:OnNext") }
                .doOnUnsubscribe { logThis("getCallLogList") }
                .flatMap { Observable.from(it) }
                .doOnNext { this.printItemDetails(it) }
                .toList()
                .subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertNotCompleted()
        assertFalse(subscription.isUnsubscribed)

        // check unsubscribe (no update from call log will received)
        subscription.safeUnsubscribe()
        testSubscriber.assertUnsubscribed()
        assertTrue(subscription.isUnsubscribed)
    }

    private fun printItemDetails(item: CallLogItem): CallLogItem = with(item) {
        log("id=$id")
        log("phoneNumber = $phoneNumber")
        log("date = $date")
        log("name = $name")
        item
    }

}
