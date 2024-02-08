# Nkhuku Management - Android App

Welcome to Nkhuku Management, your ultimate broiler management companion, designed to streamline the
process of rearing broilers efficiently. This README provides an overview of the app's
functionality, setup instructions, and details regarding unit and instrumented tests.

## Features

### Home Screen

Package ['and.drew.nkhukumanagement.userinterface.home']

- View a list of active flocks currently in inventory.
- Filter between active and closed flocks.
- Access a list of closed flocks for reference.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/home/HomeScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/home/HomeScreen.kt)

### Details Screen

Package ['app/src/main/java/and/drew/nkhukumanagement/userinterface/flock']

- View comprehensive details of an individual flock including vaccinations, feed, weight, and health
  status.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/flock/FlockDetailsScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/flock/FlockDetailsScreen.kt)

### Accounts Screen

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/accounts]
- Keep track of finances related to your flocks.
- View summary accounts of flocks.
- Filter between active and closed accounts.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/accounts/AccountsScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/accounts/AccountsScreen.kt)

### Income Screen

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/accounts]
- Monitor income earned from broiler sales.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/accounts/AccountsScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/accounts/AccountsScreen.kt)

### Expenses Screen

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/accounts]
- Track expenses incurred in rearing broilers.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/accounts/ExpenseScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/accounts/ExpenseScreen.kt)

### Planner Screen

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/planner]
- Estimate the quantity of feed, drinkers, and feeders needed based on the number of chicks.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/planner/PlannerScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/planner/PlannerScreen.kt)

### Tips Screen

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/tips]
- Receive valuable tips on raising broilers.
- Tips are stored in Firebase Cloudstore, requiring users to sign in for access.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/tips/TipsScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/tips/TipsScreen.kt)
### Overview Screen

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/overview]

- Get a summarized view of finances and flock details.
[app/src/main/java/and/drew/nkhukumanagement/userinterface/overview/OverviewScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/overview/OverviewScreen.kt)

### Add Vaccination Screen

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/vaccination]
- Record vaccinations administered to a flock.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/vaccination/AddVaccinationsScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/vaccination/AddVaccinationsScreen.kt)

### Feed Screen

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/feed]
- Monitor the quantity of feed consumed by flocks.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/feed/FeedScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/feed/FeedScreen.kt)

### Weight Screen

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/weight]

- Record weekly average weights of the flock.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/weight/WeightScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/weight/WeightScreen.kt)

### Settings Screen

Package [app/src/main/java/and/drew/nkhukumanagement/settings]

- Manage account information used for signing into the app.
- Configure default currency preferences.
- Enable/disable vaccination reminders.
- Create a backup by clicking on create a backup, and then save to local storage or share it to
  another app
- Restore backup by clicking on Restore and restore the file
  [app/src/main/java/and/drew/nkhukumanagement/settings/SettingsScreen.kt](app/src/main/java/and/drew/nkhukumanagement/settings/SettingsScreen.kt)

### AccountSetupScreens

Package [app/src/main/java/and/drew/nkhukumanagement/userinterface/login]

- This screen has using a horizontal pager to present the user with two screens, a sign up and sign
  in screen

### Sign In Screen

- Users can sign in using their registered email or through Google authentication.
- Provides a seamless login experience with options for both email and Google sign-in methods.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/login/SignInScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/login/SignInScreen.kt)

### Sign Up Screen

- Users can create a new account by registering with their email or opting for Google sign-up.
- Offers a hassle-free registration process catering to individual preferences.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/login/SignUpScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/login/SignUpScreen.kt)

### Verify Email Screen

- If users are signing up for the first time, they will be prompted to verify their email address
  before proceeding.
- Ensures the security and authenticity of user accounts, enhancing overall account protection and
  trustworthiness.
  [app/src/main/java/and/drew/nkhukumanagement/userinterface/login/VerifyEmailScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/login/VerifyEmailScreen.kt)

These screens enhance the user experience by providing flexible authentication options
and ensuring the security of user accounts through email verification for new sign-ups.

## Technical Details

- MVVM architecture
- Dependency injection implemented using Hilt.
- Authentication handled through Firebase.
- Room database used for local storage.
- Jetpack's data store proto utilized for currency and notification variables.
- Adaptive layout functionality for enhanced user experience.
- Navigation facilitated using Jetpack Compose Navigation.
- Backup and restore database
- Share backup file

## Testing

### Local Unit Tests

- Local unit tests ensure that individual functions and methods perform correctly.
- Test cases cover critical functionalities such as calculations, data manipulation, and business
  logic.

### Instrumented Tests

- Instrumented tests verify the app's behavior in a real device or emulator environment.
- UI interactions, navigation, are tested to ensure seamless
  functionality.

## Getting Started

1. **Installation**: Download and install the Nkhuku Management APK on your Android device.
2. **Authentication**: Sign in using your Firebase account to access tips and personalized features.
3. **Navigation**: Explore various screens through the navigation menu provided.
4. **Data Management**: Manage flocks, finances, vaccinations, and other aspects of broiler
   management seamlessly.
5. **Settings Configuration**: Customize app settings including currency and notification
   preferences according to your requirements.

## Support

For any queries, feedback, or issues, feel free to contact our support team
at [andrewalfredphiri@gmail.com](andrewalfredphiri@gmail.com).

## Acknowledgments

We extend our gratitude to all contributors and users who have helped in shaping Nkhuku Management
into a comprehensive broiler management solution.

Thank you for choosing Nkhuku Management for your broiler rearing needs!

## License

Nkhuku Management is licensed under the Apache License 2.0. See
the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) link for details.

---
Nkhuku Management - Empowering Efficient Broiler Management