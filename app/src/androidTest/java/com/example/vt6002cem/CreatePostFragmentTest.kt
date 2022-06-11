package com.example.vt6002cem


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CreatePostFragmentTest {

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
    fun createPostFragmentTest() {
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
        textInputEditText.perform(replaceText(""), closeSoftKeyboard())

        val textInputEditText2 = onView(
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
        textInputEditText2.perform(click())

        val textInputEditText3 = onView(
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
        textInputEditText3.perform(replaceText("test@gmail.com"), closeSoftKeyboard())

        val textInputEditText4 = onView(
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
        textInputEditText4.perform(replaceText("ikouhaha765"), closeSoftKeyboard())

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

        val bottomNavigationItemView2 = onView(
            allOf(
                withId(R.id.navigation_create_post), withContentDescription("Create Post"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_view),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView2.perform(click())

        val materialButton2 = onView(
            allOf(
                withId(R.id.uploadImageBtn), withText("Upload"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val textInputEditText5 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText5.perform(replaceText("test"), closeSoftKeyboard())

        val textInputEditText6 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText6.perform(replaceText("test"), closeSoftKeyboard())

        val textInputEditText7 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withId(R.id.priceEdit),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText7.perform(replaceText("556698"), closeSoftKeyboard())

        val textInputEditText8 = onView(
            allOf(
                withText("556698"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.priceEdit),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText8.perform(pressImeActionButton())

        val materialButton3 = onView(
            allOf(
                withId(R.id.actionButton), withText("Create"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())
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
