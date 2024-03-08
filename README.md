SubmarineUI
-----------
<p>in git bash</p>
<p>git clone https://github.com/CharlieSwires/SubmarineUI</p>

<p>This contains both the Java and .class files</p>

build
-----
<p>I've used JDK17.</p>

<p>Use eclipse or Intellij to build and run the binaries.</p>

Playing the Video Stream
------------------------

<p>On your PC which is sharing it's WiFi hotspot with the Pi
connect to rtsp://192.168.137.205:8554/ on VLC. Your IP address may be different.</p>
<img src="https://raw.githubusercontent.com/CharlieSwires/SubmarineUI/master/VLCFromPi.png" width="640" height="480" alt="VLCFromPi" title="VLC From Pi On Host"/>

User Interface
--------------

DepthKeeping.java
-----------------
<img src="https://raw.githubusercontent.com/CharlieSwires/SubmarineUI/master/DepthKeeping.png" width="500" height="500" alt="DepthKeepingNoComms" title="Depth Keeping No Comms"/>

EngineRoom.java
---------------
<img src="https://raw.githubusercontent.com/CharlieSwires/SubmarineUI/master/EngineRoomNoComms.png" width="500" height="500" alt="EngineRoomNoComms" title="Engine Room No Comms"/>

Navigation.java
---------------
<img src="https://raw.githubusercontent.com/CharlieSwires/SubmarineUI/master/NavigationRoom.png" width="500" height="500" alt="NavigationRoom" title="Navigation Room"/>

All Together
------------
<img src="https://raw.githubusercontent.com/CharlieSwires/SubmarineUI/master/All.png" width="1000" height="562" alt="All" title="All Together"/>

<p> Const contains the IP address of the Pi and the port number. </p>
<p> GenericGet contains the get request used by all of the UI.</p>
<p> pid contains the pid controller used for dive angle and navigation.</p>
