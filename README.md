<p align="center">
<img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Swecker logo">
</p>

# Swecker

Swecker is a social alarms Android app
## Features
- Create and sync alarms across devices
- Create alarm groups with your friends
- Follow public channels and receive alarms for your favourite events
## Design
- [Material 3](https://m3.material.io/)
- Dynamic theme (Android 12+)
- Dark mode support
- Multiple layouts (phone and large tablet)
![Tablet screenshot](https://user-images.githubusercontent.com/32399075/188496867-d7181cd6-d9e0-4155-9bab-8309d2563a6c.png)
## Tech stack
- Written in Kotlin with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Dependency Injection with [Hilt](https://dagger.dev/hilt/)
- [Firebase](https://firebase.google.com/) for Auth, Firestore and Cloud Storage
## Architecture
- MVVM
- ViewModels interact with Services (seen as external) and Repositories (local components that can also interact with Services)
- A proper backend is missing, any authenticated user can do everything with Firebase
## Notes
The `google-services.json` in the code is an example file and should be replaced with a valid one. Firestore and Cloud Storage should be configured to allow each authenticated user to read and write.
