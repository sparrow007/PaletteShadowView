# PaletteShadowView
This library shows the shadow of the image by it's palette color.

![final 1](https://user-images.githubusercontent.com/22986571/54155896-0f46d300-446b-11e9-9f6a-dfd72c5691e8.jpg)

[![Platform](https://img.shields.io/badge/platform-android-blue.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-19%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=19)

USAGE
-----
To add shadow in your imageview and color of the shadow will be according to pallete color in your image as well as other properties to handle shadow radius , color , offset and also shape of the image. you can grab this library using your Gradle file. 
 
Gradle
------

```
dependencies {
    ...
    implementation 'com.jackandphantom.android:paletteshadowview:1.0.1'
    implementation 'com.android.support:palette-v7:28.0.0'
}
```
##### 1.Few ScreenShots
Shadow | Corner Radius | Shadow Offset
---|---|---
<img src = "https://user-images.githubusercontent.com/22986571/54161886-ae72c700-4479-11e9-87ad-ff5b07bfc30e.png" width = 250 height = 400/> | <img src = "https://user-images.githubusercontent.com/22986571/54161900-b6326b80-4479-11e9-96e2-3fb7f11981f5.png" width = 250 height = 400/> | <img src = "https://user-images.githubusercontent.com/22986571/54161906-bdf21000-4479-11e9-9209-7a7cae9caf58.png" width = 250 height = 400/>

XML
-----

```xml
<!-- <a> circular progressbar xml</a> -->
<com.jackandphantom.paletteshadowview.PaletteShadowView
        android:layout_width="250dp"
        android:layout_height="250dp"
        android:layout_marginTop="120dp"
        android:id="@+id/hr"
        app:paletteOffsetX="15"
        app:paletteOffsetY="15"
        android:layout_centerHorizontal="true"
        app:paletteSrc="@drawable/image"
        />
```
### xml attributes

Xml attribute | Description
---|---
  app:paletteOffsetX | represents the offset of the shadow in the x direction
  app:paletteOffsetY | represents the offset of the shadow in the y direction
  app:paletteSrc | represents a picture resource
  app:paletteRoundRadius | Indicates the corner radius
  app:paletteShadowRadius | Indicates shadow blurring
  app:paletteShadowColor | Indicate the color of the shadow
  
  JAVA
-----
  ```xml
  PaletteShadowView paletteShadowView = findViewById(R.id.paletteImage);
        paletteShadowView.setImageResource(R.drawable.image);
        paletteShadowView.setShadowOffest(10, 10);
        paletteShadowView.setRoundedRadius(50);
  ```
  
  ### Public Methods
Method | Description
---|---
 Public void setShadowColor(int color) | Represents the color of the custom settings control shadow
 public void setRoundedRadius(int radius) | Represents the corner of image
 public void setShadowOffest(int offsetX, int offsetY) | Represents the offset of the shadow in the x and y direction
 public void setShadowRadius(int radius) | Indicates shadow blurring
 Public int[] getVibrantColor() | Represents an array of colors to get the Vibrant theme; assuming that the color array is arry, arry[0] is the color used by the recommended title, arry[1] is the color used by the recommended body, and arry[2] is the recommended The color used for the background. Colors are for recommendations only, you can choose
 Public int[] getDarkVibrantColor()| Represents an array of colors for obtaining the DarkVibrant theme. The meaning of the array element is the same as above
 Public int[] getLightVibrantColor()| Represents the color array of the LightVibrant theme. The meaning of the array element is the same as above
 Public int[] getMutedColor()| Represents the color array of the Muted theme. The meaning of the array element is the same as above.
 Public int[] getDarkMutedColor()| Represents the color array of the DarkMuted theme. The meaning of the array element is the same as above.
 Public int[] getLightMutedColor()| Represents the color array of the LightMuted theme. The meaning of the array element is the same as above
 public void setVibrantColor() | set the vibrant theme as the shadow color , remember it may be null.
 public void setDarkVibrantColor() | set the DarkVibrant theme as the shadow color , remember it may be null.
 public void setLightVibrantColor() | set the LightVibrant theme as the shadow color , remember it may be null.
 public void setMutedColor() | set the Muted theme as the shadow color , remember it may be null.
 public void setDarkMutedColor() | set the DarkMuted theme as the shadow color , remember it may be null.
 public void setLightMutedColor() | set the LightMuted theme as the shadow color , remember it may be null.
 
 ### Contribution
 If you want to add feature and find a bug feel free to contribute , you can  create issue related to bug , feature and send a pull.
 
 LICENCE
-----

 Copyright 2019 Ankit kumar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
  
  Thanks to DingMouRen.
