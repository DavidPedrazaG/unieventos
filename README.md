
# UniEventos

UniEventos is a mobile application for purchasing tickets to concerts and events across various cities in Colombia. It enables customers to register, browse, and buy tickets for events of their interest. Administrators can manage events and create discount coupons for users.

## Getting Started

This section will guide you through setting up the project on your local machine.

### Installation Process

1. Clone the repository from GitHub:

   ```bash
   git clone https://github.com/your-repo/UniEventos.git
   ```

2. Open the project in **Android Studio**.

3. Make sure your environment is set up for Android development with **API 28 (Android 9.0)**.

4. Install dependencies:
   - **Jetpack Compose**: The UI framework used for the project.
   - **Firebase**: For data persistence and user authentication (details in `build.gradle`).

5. Configure Firebase:
   - Set up a Firebase project.
   - Link the Firebase project to the Android app.
   - Add the `google-services.json` file to the `app/` directory.

### Software Dependencies

- **Kotlin** (Primary programming language)
- **Jetpack Compose** (UI Framework)
- **Firebase** (Authentication, Firestore for data storage)
- **Android SDK 28** (Minimum API Level: Android 9.0)
  
### Latest Releases

Refer to the [Releases](https://github.com/your-repo/UniEventos/releases) section for information on the latest version and updates.

### API References

- **Firebase Authentication API**: Used for user sign-up, login, and password recovery.
- **Firestore**: Stores event, user, and purchase data.

## Project Overview

UniEventos is designed to help users easily buy tickets for events across Colombia. There are two types of users: **Administrators** and **Clients**.

### For Administrators:
- **Event Management**: Create, edit, view, list, and deactivate events.
- **Coupon Management**: Create coupons with customizable discount percentages.
- **Password Recovery**: Recover forgotten passwords via email.

### For Clients:
- **User Registration and Login**: Users register by providing their ID number, name, address, phone number, email, and password. After registering, a code is sent to their email for account activation.
- **Event Browsing and Filtering**: Filter events by name, type (concert, theater, sports, festival), and city.
- **Ticket Purchase**: Select events, choose seats, apply discount coupons, and complete purchases.
- **Shopping Cart**: Store selected events across sessions, even after logging out or changing devices.
- **Order History**: View past purchases.
- **Account Management**: Edit or delete personal details.
- **Password Recovery**: Reset the password with a time-limited code.

### Coupons System:
- **New User Coupon**: Upon registering, users receive a 15% discount coupon via email.
- **First Purchase Coupon**: After the first purchase, users receive a 10% discount coupon via email.
- Coupons are redeemable at checkout for discounts on event tickets.

### Administrator Features:
- Create events with details such as event name, address, city, description, event type (concert, theater, sports, etc.), and images (event poster and seating layout).
- Manage locations with name, price, and maximum capacity.
- Create custom coupons for special dates or events.

### Persistence:
- **Firebase** is used for storing user data, event information, and purchase history.

### Additional Features:
- Registered users cannot log in immediately after registration; they must first activate their account using a code sent to their email, valid for 15 minutes.
- Orders can only be placed up to two days before an event.
- Orders are validated to ensure the selected location has sufficient capacity.

## Build and Test

To build and run the project:

1. **Build**: In Android Studio, select **Build > Make Project**.
2. **Run**: Select a device or emulator running Android 9.0 (API 28) or higher, then click **Run > Run ‘app’**.

### Running Unit Tests

Unit tests can be executed using the following command in Android Studio:

```bash
./gradlew test
```

Tests cover functionality such as login, event filtering, and purchase validation.

## Contribute

Contributions are welcome to improve the functionality and performance of the UniEventos app. To contribute:

1. Fork the repository.
2. Create a new feature branch:

   ```bash
   git checkout -b feature/your-feature
   ```

3. Commit your changes:

   ```bash
   git commit -m "Add feature: your-feature"
   ```

4. Push the branch:

   ```bash
   git push origin feature/your-feature
   ```

5. Create a pull request on the GitHub repository.

---

If you'd like to learn more about creating good readme files, refer to the [Azure DevOps guidelines](https://docs.microsoft.com/en-us/azure/devops/repos/git/create-a-readme?view=azure-devops).

Inspiration for this README was drawn from:
- [ASP.NET Core](https://github.com/aspnet/Home)
- [Visual Studio Code](https://github.com/Microsoft/vscode)
- [Chakra Core](https://github.com/Microsoft/ChakraCore)

### Project Contributors

- Joseph Maurizio Contreras Vila
- Camilo Duarte Rivera
- David Felipe Pedraza Guadir
