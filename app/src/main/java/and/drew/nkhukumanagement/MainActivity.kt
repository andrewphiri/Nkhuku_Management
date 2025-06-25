package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.prefs.UserPreferencesRepository
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.NavigationType
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areNavigationBarsVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext
        )
    }
    private val authUiClient by lazy {
        AuthUiClient(
            applicationContext
        )
    }
    private val userPrefsViewModel by lazy {
        ViewModelProvider(this)[UserPrefsViewModel::class.java]
    }
    private val signInViewModel by lazy {
        ViewModelProvider(this)[SignInViewModel::class.java]
    }
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        //Check if user is signed in or email verified before splashScreen
        //This ensures that the Account setup screen is not shown
        signInViewModel.setUserLoggedIn(loggedIn = googleAuthUiClient.getSignedInUser() != null)
        signInViewModel.setEmailVerification(emailVerified = authUiClient.isEmailVerified())
        userPreferencesRepository.userSkipAccount.asLiveData().observe(this) { skip ->
            userPrefsViewModel.setSkipAccount(skip)
        }


        //hideNavigationBar()
        setContent {

            val windowSize = calculateWindowSizeClass(this).widthSizeClass
            val emailVerified by signInViewModel.emailVerified.collectAsState()
            val userSignedIn by signInViewModel.userLoggedIn.collectAsState()
            val skipAccount by userPrefsViewModel.skipAccount.collectAsState()
//            var isEmailVerified by remember { mutableStateOf(false) }
//            emailVerified.observe(this) {
//                isEmailVerified = it
//                Log.i("Email_Verified", isEmailVerified.toString())
//            }

            NkhukuManagementTheme {
                val navigationType: NavigationType
                val contentType: ContentType
                val winSize = LocalWindowInfo.current.containerSize
                when {
                    winSize.width >= 0 -> {
                        when (windowSize) {
                            WindowWidthSizeClass.Compact -> {
                                navigationType = NavigationType.BOTTOM_NAVIGATION
                                contentType = ContentType.LIST_ONLY
                            }

                            WindowWidthSizeClass.Medium -> {
                                navigationType = NavigationType.NAVIGATION_RAIL
                                contentType = ContentType.LIST_ONLY
                            }

                            WindowWidthSizeClass.Expanded -> {
                                navigationType = NavigationType.NAVIGATION_RAIL
                                contentType = ContentType.LIST_AND_DETAIL
                            }

                            else -> {
                                navigationType = NavigationType.BOTTOM_NAVIGATION
                                contentType = ContentType.LIST_ONLY
                            }
                        }
                    }

                    else -> {
                        throw IllegalArgumentException("Dp must be greater than zero")
                    }
                }

                    NkhukuApp(
                        navigationType = navigationType,
                        contentType = contentType,
                        userPrefsViewModel = userPrefsViewModel,
                        isUserSignedIn = userSignedIn,
                        isEmailVerified = emailVerified,
                        isAccountSetupSkipped = skipAccount,
                    )
                }
            }

    }

    fun hideNavigationBar() {
//        // Get the insets controller for the window
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        // Set the decor to fit system windows (this helps in properly handling the insets)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Hide the navigation bar
        controller.hide(WindowInsetsCompat.Type.systemBars())

        // Hide the status bar
       // controller.hide(WindowInsetsCompat.Type.statusBars())

        // Set the behavior of system bars to show transient bars by swipe
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}



@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NkhukuManagementTheme {
        Greeting("Android")
    }
}