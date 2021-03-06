package to.pns.naroencounter;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import to.pns.naroencounter.Other.NovelDB;
import to.pns.naroencounter.data.NovelInfo;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        NovelDB db = new NovelDB(appContext);
        NovelInfo list = db.getNovelInfo("n7975cr");
        db.close();
    }
}
