package com.example.vt6002cem


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentForStaffTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(SplashActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        )

    @Test
    fun splashActivityTest2() {
        val viewGroup = onView(
            allOf(
                withId(R.id.swipe_refresh_layout),
                withParent(withParent(withId(R.id.nav_host_fragment_activity_main))),
                isDisplayed()
            )
        )
        viewGroup.check(matches(isDisplayed()))

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.navigation_settings), withContentDescription("Settings"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_view),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val linearLayout = onView(
            allOf(
                withParent(
                    allOf(
                        withId(android.R.id.content),
                        withParent(withId(androidx.appcompat.R.id.decor_content_parent))
                    )
                ),
                isDisplayed()
            )
        )
        linearLayout.check(matches(isDisplayed()))

        val textInputEditText = onView(
            allOf(
                withId(R.id.emailEditText),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.email),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("test@gmail.com"), closeSoftKeyboard())

        val textInputEditText2 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withId(R.id.password),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText2.perform(replaceText("ikouhaha765"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.loginButton), withText("Login"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val viewGroup2 = onView(
            allOf(
                withId(R.id.swipe_refresh_layout),
                withParent(withParent(withId(R.id.nav_host_fragment_activity_main))),
                isDisplayed()
            )
        )
        viewGroup2.check(matches(isDisplayed()))

        val button = onView(
            allOf(
                withId(R.id.editButton),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val materialButton2 = onView(
            allOf(
                withId(R.id.editButton),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val imageButton = onView(
            allOf(
                withContentDescription("Navigate up"),
                withParent(
                    allOf(
                        withId(androidx.appcompat.R.id.action_bar),
                        withParent(withId(androidx.appcompat.R.id.action_bar_container))
                    )
                ),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(androidx.appcompat.R.id.action_bar),
                        childAtPosition(
                            withId(androidx.appcompat.R.id.action_bar_container),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
