# HPIFitness  app  
Create simple fitness app for Android with the following requirements:   
*	Support different users with user credentials (username and password)
*	Get and store locally the distance walked
*	Feedback on achieving milestones (multiples of 1000 feet)
*	Whenever the person is at office, periodic reminder to stand up and walk every 1 hour.
*	Display daily statistics (in text format) on the main-screen.

#Library for Solution  
To measure and save user's distance walked, I applied the following libraries:
*	[Butter Knife](http://jakewharton.github.io/butterknife/) - Bind view to class member fields
*	[Realm](https://realm.io/) - Faster and easier-to-use Local database for authentication and persisting user's walk data.
*	[Google Play Service](https://developers.google.com/android/guides/setup) - Provide library for getting access to Google Map and user location  

# Failed Attempt
* [Google Fit](https://developers.google.com/fit/android/reference)-Tried to use Google fit API to get precise user's steps, and distance walked but data from sensors did not change after many testing. Therefore decided to take advantage of user GPS and Google map.  

# Challenge and Learning
* Due to Android activity lifecycle, the distance walked can not be record in the main screen. Therefore a service is created in the background to record the distance and time walked. It is very difficult to maintain a service in the background because the Android system could kill it to claim memory and power usage. The best practice is binding the service to the main screen, and running it in the foreground with a notification.   
* The distance walked is not precise because the Android location service provides location at different interval and the errors increase drastically. For this app, I measure the distance walked by adding consecutively the distance between the current and previous location.
* The best user experience is letting the user to control the reminder, therefore I let the user turn on and off the notification
* For authentication, the best practice is not to save user information (username and password on the phone) because there is the risk that it will be found.  

# To-Do
* Drawing user's path on Google map still has to be done.

# Android Version
* 5.0 Lollipop - 6.0 Marshmallow

# Warning
* Turn on GPS after installation

# Screenshot for final app
![Alt text](/images/walkactivity.png?raw=true "walkactivity")  

![Alt text](/images/mainscreen.png?raw=true "mainscreen")

![Alt text](/images/login.png?raw=true "login")

![Alt text](/images/logout.png?raw=true "logout")

![Alt text](/images/achievement.png?raw=true "achievement")

![Alt text](/images/savedata.png?raw=true "savedata")

# Steps Follow to Complete App
1. Create a repository for the app to record my work process, results from research  
2. Do research about the best way of getting user distance walked. Result from search:  
  * Using Google API to calculate the distance walked
  *	Google Fit API - https://developers.google.com/fit/android/data-types#data_types_for_instantaneous_readings
  *	Accelerometer to calculate the number of walking steps and then calculate distance by multiplying walking steps by walking stride length (too much work).
  *	Information to display: Total time, distance, average pace and calories
  *	Fitness website visited (1)http://bit.ly/2ckEyWP (2) http://www.ideafit.com/fitness-library/walking-the-latest-research
  *	Database: SQLite, Realm, Shared preference
3.	Wireframing based on the requirements and result from research
  *	Visit the following to find inspiration:
  *	http://androidniceties.tumblr.com/
  *	http://www.materialup.com/  
![Alt text](/images/wireframing.jpg?raw=true "wireframe")  
4.	Sketch a diagram for the architecture of the app  
![Alt text](/images/architecture.png?raw=true "architecture")  
5.	Create an application in Android Studio: create different branch for every major requirement.
6.	*First branch:* Google map service connection:
  * Create a map activity
  * Connect to google service by registering the app in Google developer console
7.	*Second branch:* Create a location service
  *	Get location using Location API in service
  *	Calculate distance and time in Service
  *	Connect service to map activity
8.	*Third Branch:* create authentication
  * Create log in layout
  * Create  Sign up layout
9.	*Fourth Branch:* Create authentication to Realm database
  * Connect Log in to database
  * Connect Sign up to database
10.	*Fifth Branch:* Create main screen
	* Create logout
  * Display main statistic
  * Create notification for every 1 hour
11.	*Sixth Branch:* Connect Main screen to walk screen
  *	Follow Activity lifecycle for updating main screen
  * Save user walk data in Realm databases
12.	*Last Branch:* improve UI, and refactoring
  * Change UI colors
  * Check code for refactoring and naming convention
  *	Checks requirements
