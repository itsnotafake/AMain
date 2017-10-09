# Atakr
This work is licensed under a [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.](https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode)

![CC Logo](https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png)

## Installation
1. Clone the project.
2. Open in Android Studio.
3. Launch the application on your phone or emulator
Note: In the future, this project will be put on the App Store and removed from Github.

## About
Atakr is a video browsing/watching application that hosts videos on Youtube. It is intended that the video content uploaded to Atakr focuses on video games. Any sort of video game video is welcome. In the future, users will be able to browse these videos by video game title (Dota2, CSGO, League of Legends, etc) , the genre of the video uploaded (Comedy, Pro, Cool, etc), and the genre of the specific video game (FPS, MMORPG, RPG, RTS, etc).

They way Atakr works is simple. Users that want to share content either upload their content to Youtube or find content on Youtube to share. They then click Youtube's share button and share to Atakr. At this stage they can rename the video if they so desire. The video will then be available on Atakr.

## TODO and Reflections
* The biggest problem with Atakr moving forward is the lack of a traditional backend. Google Firebase was very effective in quickly getting a backend up with minimal overhead, but its database (mainly the fact that it isn't a relational database) made it very difficult to do some queries essential to to the app's functionality. It is in fact possible to hook up firebase with a server on another cloud platform (for example Heroku) and use its services in this way. Thus, future versions of Atakr can use Heroku, Nodejs, and Heroku's database modules to perform much of the server side work while still leveraging Firebase's User authentication.

* Right now, user accounts aren't instrumental to the functionality of Atakr. They are a nice feature to have, but forcing users to create accounts before they can use Atakr is definitely detrimental to the app's growth. Future implementation of the application should remove the mandatory account creation at the beginning, and only point users in that direction of they want credit for video uploads.

* There needs to be a way to let users know that the main mechanism for sharing videos on Atakr is to simply find a video on Youtube and share it to Atakr. Right now this is in the app's description on the play store page, but it needs to be explicitly stated. A DialogAlert for new users is most probably the best way to implement this.

* As of right now, there are many static variables held in MainActivity that are crucial to how data is passed around in the application and between the application and Firebase. It is incredibly messy and by far not the best way to handle this. In the future, a lot of these variables should be moved to a seperate class, or the entire paradigm of how data is moved around the app and certain states are represented should be re-implemented.

## Screenshots

### Main Page
![MainPage](https://github.com/itsnotafake/AMain/blob/master/MainActivity.jpg)

### Video Play
![VideoPlay](https://github.com/itsnotafake/AMain/blob/master/VideoPlay.png)

### Share Video
![ShareVideo](https://github.com/itsnotafake/AMain/blob/master/ShareVideo.png)
