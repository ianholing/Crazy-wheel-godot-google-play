# Crazy Wheel Android Godot Template with Google play integration


I'm an Android dev, and I personally spent lot of time looking for the way to integrate my game with Google Play platform. After reading all the Godot oficial documentation and compile plugins, templates and all that really difficult stuff I saw that my final APK was finally really old (Related to Google versions) and not even working with new Google Play system.. I decided to move forward and simply use the template as an Android project.

Investigating, I saw that I can copy my Godot project in the Assets folder and it works perfectly! (Is a good choice to use a compiled version of Godot files for final projects, but this is good for dev and educational purpouses)

I had to delete the part of downloading de expansion package and voilà! It really works!

I will use this template for my future projects, maybe is a good idea to do something similar you too.

You only should be sure that the Godot engine.cfg file contain this:

```
[android]

modules="com/android/godot/GodotGoogleGamePlayServices"
```

You can also try the complete game here:

https://play.google.com/store/apps/details?id=com.dualgames.crazywheel


You can also check the entire Godot project here:

https://github.com/ianholing/Crazy-wheel-godot-google-play/tree/master/app/src/main/assets
