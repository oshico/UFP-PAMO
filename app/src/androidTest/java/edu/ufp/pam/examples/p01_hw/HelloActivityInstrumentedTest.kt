package edu.ufp.pam.examples.p01_hw

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import edu.ufp.pam.examples.R

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import kotlin.jvm.java

//@RunWith(AndroidJUnit4::class)
//class HelloActivityInstrumentedTest {
//    private lateinit var stringToBeChecked: String
//
//    @get:Rule
//    var activityRule: ActivityScenarioRule<HelloWorldEmptyViewMainActivity> =
//        ActivityScenarioRule(HelloWorldEmptyViewMainActivity::class.java)
//
//    @Before
//    fun initValidValues() {
//        stringToBeChecked = "Name"
//    }
//
//    @Test
//    fun checkTextViewHelloContent() {
//        Espresso.onView(ViewMatchers.withId(R.id.textViewHelloWorldInfoType))
//            .perform(ViewActions.click()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(ViewMatchers.withId(R.id.textViewHelloWorldInfoType))
//            .check(ViewAssertions.matches(ViewMatchers.withText(stringToBeChecked)))
//    }
//
//    @Test
//    fun checkEditTextHelloContent() {
//        //val str = InstrumentationRegistry.getInstrumentation().context.getString(R.string.hello_world_on_android)
//        Espresso.onView(ViewMatchers.withId(R.id.editTextHelloWorldInsert))
//            .perform(ViewActions.clearText(), ViewActions.typeText("Insert"));
//        Espresso.onView(ViewMatchers.withId(R.id.editTextHelloWorldInsert))
//            .check(ViewAssertions.matches(ViewMatchers.withText(stringToBeChecked)))
//    }
//}