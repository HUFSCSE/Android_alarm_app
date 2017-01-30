# KHK Alarm App
This is the android alarm app.

User Set the Date, Time. then, KhkAlarm will call at setted time by Notification, Vibrate and Alarm Music.

If User click the Notification, KhkAlarm will show Alarm Execute View.
In Alarm Execute View, User can touch the button to stop the Alarm Music or repeat Alarm 5 minute later.

Each Alarm Data has year, month, day, hour, minute and note. and Alarms store in the SQLite, and show in the RecyclerView, MainActivity.

If User click the Edit Button on each alarm view, User can change the date,time or note. also can open map. The map is Google Maps, and show User's current position.

Also when device reboot, KHK Alarm works well.

## Requirement
* minSDKVersion 15, targetSDKVersion 23, buildToolsVersion 25.0.2
* Google Maps Api Key (/app/src/debug/res/values/google_maps_api.xml)

## App Video
[![VideoLabel](https://i.ytimg.com/vi/WxVrGUEllXs/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=eBIiiv6W2LsO3yMjqcM3B_Bg1_Y)](https://youtu.be/WxVrGUEllXs)

## Copyright

App : Kim Heong Kyun   

[Github](https://github.com/HUFSCSE)  [Blog](http://gudrbscse.tistory.com/)

Alarm Music : Kim Kyu Hoon 

[Music_Video](https://youtu.be/xdOHp_rksXU)
