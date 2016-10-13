# Wearable Sample Application
This sample application demonstrates basic connection establishment between Mobile and Wearable device.
Data can be transferred both ways. Device information will be shown on wearable and mobile device.
This application has the below capabilities. 

* Support to connect Android and Tizen wearable app simultaneously based on the availability
* Support to send device details to both Android and Tizen wearables
* Support to receive the device information from wearables and displays it in mobile device
* Support to tracks the wearable connection and provide callbacks to the application
* Support to respond back to mobile app even when wearable app is in background

## How to setup this project
1). Setup Android Studio. It can be downloaded from the below location
    https://developer.android.com/studio/index.html

2). Setup Tizen Studio. It can be downloaded from below path
    https://developer.tizen.org/ko/development/tools/download

3). Import the Android project using android studio and Tizen using tizen studio

4). Build the android mobile and wear app and install it in respective device

5). Build the Tizen app using Tizen studio and install it in Samsung Gear device

## Directory structure
├── android
│   ├── mobile - Android Mobile app sample application code
│   └── wear   - Andorid Wear app sample applicaton code
└── tizen      - Tizen Gear sample application code

## Screenshots

### Sample Application 

![Sample](/screenshots/application_disconnected.png "Wearable disconnected")
![Sample](/screenshots/application_connected.png "Wearable connected")
![Sample](/screenshots/application_single_wearable.png "One wearable selected")
