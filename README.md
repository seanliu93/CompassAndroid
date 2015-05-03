# CompassAndroid
Compass lets you find other device locations in real time using iBeacons, Cisco CMX, GPS and OpenDaylight. 
(NOTE: CMX and iBeacons are currently not available. Please check back later for further updates)

## Requirements
* Android Studio 1.x.x
* Android OS v5.0+

## User Registration
1. User succesfully creates a username called newUserId (for example) using LoginViewController
2. A container with "resourceID":"newUserID" is created under InCSE1/UserAE/
3. A container with "resourceID":"deviceMACAddress" is created under InCSE1/UserAE/newUserID/ and InCSE1/LocationAE/Things/
4. Four containers with resourceIDs: LocBeacon, LocCMX, LocGPS, and AccuracyFlag are created under InCSE1/LocationAE/Things/deviceMACAddress/

## Updating Location Data
### GPS
Compass uses the Android LocationManager to monitor the Current Location (latitude, longitude) of the device.

## MyHttpClient
Contains all HTTP request methods for the app. 

### Accuracy Flag
An AccuracyFlag container is created with it's labels attribute set to a 3-bit binary number in order to prioritize location 
accuracy for each UUID or deviceMACAddress container under InCSE1/LocationAE/Things/.
* If labels[:1] == 1, then the LocBeacon container has the most accurate device position
* If labels[:2] == 01, then the LocCMX container has the most accurate device position
* If labels[:3] == 001, then LocGPS container has the most accuracte device position
* Else, the device's position could not be found

(NOTE: Due to CMX and iBeacon not working for Android, the labels attribute of AccuracyFlag will only either be "001" or "000" depending if
GPS is on.)

## Data Flow Diagram
![alt tag](https://camo.githubusercontent.com/9d73139b93554a12eb102d5d45c7704c9296cad8/687474703a2f2f692e696d6775722e636f6d2f67375262516b442e706e67)
