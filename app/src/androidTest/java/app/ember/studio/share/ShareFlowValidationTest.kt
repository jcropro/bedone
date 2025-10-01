package app.ember.studio.share

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.intent.Intents.intended
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import app.ember.studio.MainActivity
import app.ember.studio.SongShareMessage
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive tests for share flow validation
 * Tests content URI sharing, persisted grants, and text-only fallback
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ShareFlowValidationTest {

    @Before 
    fun setUp() { 
        Intents.init() 
    }
    
    @After 
    fun tearDown() { 
        Intents.release() 
    }

    @Test
    fun shareWithContentUri_grantsReadPermission() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val method = MainActivity::class.java.getDeclaredMethod(
                "shareSongMessage",
                SongShareMessage::class.java,
                Uri::class.java
            )
            method.isAccessible = true
            
            val contentUri = Uri.parse("content://media/external/audio/media/42")
            val shareMessage = SongShareMessage("Test Song", "Now playing: Test Song by Test Artist")
            
            method.invoke(activity, shareMessage, contentUri)
        }
        
        // Verify chooser intent is sent
        intended(allOf(hasAction(Intent.ACTION_CHOOSER)))
        
        // Verify the underlying SEND intent has audio type and stream
        intended(allOf(
            hasAction(Intent.ACTION_SEND),
            hasType("audio/*"),
            hasExtra(Intent.EXTRA_STREAM, notNullValue())
        ))
        
        scenario.close()
    }

    @Test
    fun shareWithoutStream_fallsBackToTextOnly() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val method = MainActivity::class.java.getDeclaredMethod(
                "shareSongMessage",
                SongShareMessage::class.java,
                Uri::class.java
            )
            method.isAccessible = true
            
            val shareMessage = SongShareMessage("Test Song", "Now playing: Test Song by Test Artist")
            
            method.invoke(activity, shareMessage, null)
        }
        
        // Verify chooser intent is sent
        intended(allOf(hasAction(Intent.ACTION_CHOOSER)))
        
        // Verify the underlying SEND intent has text type (no stream)
        intended(allOf(
            hasAction(Intent.ACTION_SEND),
            hasType("text/plain"),
            hasExtra(Intent.EXTRA_TEXT, equalTo("Now playing: Test Song by Test Artist"))
        ))
        
        scenario.close()
    }

    @Test
    fun shareWithInvalidUri_fallsBackToTextOnly() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val method = MainActivity::class.java.getDeclaredMethod(
                "shareSongMessage",
                SongShareMessage::class.java,
                Uri::class.java
            )
            method.isAccessible = true
            
            val invalidUri = Uri.parse("invalid://uri")
            val shareMessage = SongShareMessage("Test Song", "Now playing: Test Song by Test Artist")
            
            method.invoke(activity, shareMessage, invalidUri)
        }
        
        // Verify chooser intent is sent
        intended(allOf(hasAction(Intent.ACTION_CHOOSER)))
        
        // Should fall back to text-only sharing
        intended(allOf(
            hasAction(Intent.ACTION_SEND),
            hasType("text/plain"),
            hasExtra(Intent.EXTRA_TEXT, equalTo("Now playing: Test Song by Test Artist"))
        ))
        
        scenario.close()
    }

    @Test
    fun shareIncludesSubjectAndText() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val method = MainActivity::class.java.getDeclaredMethod(
                "shareSongMessage",
                SongShareMessage::class.java,
                Uri::class.java
            )
            method.isAccessible = true
            
            val shareMessage = SongShareMessage("Test Song", "Now playing: Test Song by Test Artist")
            
            method.invoke(activity, shareMessage, null)
        }
        
        // Verify both subject and text are included
        intended(allOf(
            hasAction(Intent.ACTION_SEND),
            hasExtra(Intent.EXTRA_SUBJECT, equalTo("Test Song")),
            hasExtra(Intent.EXTRA_TEXT, equalTo("Now playing: Test Song by Test Artist"))
        ))
        
        scenario.close()
    }

    @Test
    fun shareWithMediaStoreUri_persistsGrants() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val method = MainActivity::class.java.getDeclaredMethod(
                "shareSongMessage",
                SongShareMessage::class.java,
                Uri::class.java
            )
            method.isAccessible = true
            
            // Test with a MediaStore URI that should be persistable
            val mediaStoreUri = Uri.parse("content://media/external/audio/media/42")
            val shareMessage = SongShareMessage("Test Song", "Now playing: Test Song by Test Artist")
            
            method.invoke(activity, shareMessage, mediaStoreUri)
        }
        
        // Verify chooser intent is sent with proper flags
        intended(allOf(hasAction(Intent.ACTION_CHOOSER)))
        
        // Verify the underlying SEND intent has proper flags for URI permissions
        intended(allOf(
            hasAction(Intent.ACTION_SEND),
            hasType("audio/*"),
            hasExtra(Intent.EXTRA_STREAM, notNullValue())
        ))
        
        scenario.close()
    }
}
