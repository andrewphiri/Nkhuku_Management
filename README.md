# Nkhuku Management - Android App

Welcome to Nkhuku Management, your ultimate broiler management companion, designed to streamline the
process of rearing broilers efficiently. This README provides an overview of the app's
functionality, setup instructions, and details regarding unit and instrumented tests.

## Features

### Home Screen

Package ['and.drew.nkhukumanagement.userinterface.home'][1]

- View a list of active flocks currently in inventory.
- Filter between active and closed flocks.
- Access a list of closed flocks for reference.
  [1]: [app/src/main/java/and/drew/nkhukumanagement/userinterface/home/HomeScreen.kt](app/src/main/java/and/drew/nkhukumanagement/userinterface/home/HomeScreen.kt)

### Details Screen

- View comprehensive details of an individual flock including vaccinations, feed, weight, and health
  status.

### Accounts Screen

- Keep track of finances related to your flocks.
- View summary accounts of flocks.
- Filter between active and closed accounts.

### Income Screen

- Monitor income earned from broiler sales.

### Expenses Screen

- Track expenses incurred in rearing broilers.

### Planner Screen

- Estimate the quantity of feed, drinkers, and feeders needed based on the number of chicks.

### Tips Screen

- Receive valuable tips on raising broilers.
- Tips are stored in Firebase Cloudstore, requiring users to sign in for access.

### Overview Screen

- Get a summarized view of finances and flock details.

### Add Vaccination Screen

- Record vaccinations administered to a flock.

### Feed Screen

- Monitor the quantity of feed consumed by flocks.

### Weight Screen

- Record weekly average weights of the flock.

### Settings Screen

- Manage account information used for signing into the app.
- Configure default currency preferences.
- Enable/disable notifications for vaccinations.

## Technical Details

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
- UI interactions, navigation, and integration with Firebase services are tested to ensure seamless
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
at [support@nkhukumanagement.com](mailto:support@nkhukumanagement.com).

## Acknowledgments

We extend our gratitude to all contributors and users who have helped in shaping Nkhuku Management
into a comprehensive broiler management solution.

Thank you for choosing Nkhuku Management for your broiler rearing needs!

---
Nkhuku Management - Empowering Efficient Broiler Management