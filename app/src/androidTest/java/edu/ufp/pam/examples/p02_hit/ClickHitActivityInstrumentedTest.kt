package edu.ufp.pam.examples.p02_hit

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not

import edu.ufp.pam.examples.R
//import edu.ufp.pam.examples.p02_hit.HitEmptyViewMainActivity

//@RunWith(AndroidJUnit4::class)
//@LargeTest
//class ClickHitActivityInstrumentedTest {
//
//    private lateinit var countToBeReached: String
//
//    @get:Rule
//    var activityRule: ActivityScenarioRule<HitEmptyViewMainActivity> =
//        ActivityScenarioRule(HitEmptyViewMainActivity::class.java)
//
//    @Before
//    fun initValidCount() {
//        // Specify count to be reached with the hits on button
//        countToBeReached = "10"
//    }
//
//    @Test
//    fun clickHitButton() {
//        val hitCounter = countToBeReached.toInt()
//        // Hit button for 10 times
//        for (i in 1..hitCounter){
//            onView(withId(R.id.buttonHit)).perform(click())
//        }
//        // Check that after clicks, the content of TextView is 10
//        onView(withId(R.id.textViewHitCounter)).check(matches(withText(countToBeReached)))
//    }
//
//    @Test
//    fun checkHitButtonsLabel() {
//        // Check that all buttons with buttonHit id (more than 1 buttons can have the same id as
//        // long they are in different layout files) do not have "Login" text label
//        onView(allOf(withId(R.id.buttonHit), not(withText("Login"))))
//    }
//}
