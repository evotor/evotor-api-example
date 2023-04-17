package ru.qualitylab.evotor.evotortest6;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import org.junit.Test;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("ru.qualitylab.evotor.evotortest", appContext.getPackageName());
    }
}
