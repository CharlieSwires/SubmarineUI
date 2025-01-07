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
<p>The red text indicates the depth sensor has failed completely.
SUCCESS indicates UI used correctly i.e. depth and max pitch entered correctly for the Dive. On the dial is shown in black actual angle, green destination angle. COMMUNICATION OK indicates two way comms happening. Plane Angle indicates angle of fore plane and Tanks Filling indicates what the pumps are doing. </p>
<p>1.	Dive is for taking the sub from the surface to under water.</p> 
<p>2.	Alter Depth is for underwater to underwater depth change. </p>
<p>3.	Emergency Surface is to empty tanks of water and rise at 45 degrees full throttle.</p> 
<p>4.	Freeze is to cut engines dead. </p>
<p>5.	Crash Dive is to fill tanks and use a 45degree angle down to 5m depth.</p> 
<p>6.	Zero is to initialise the depth and angle sensor readings. </p>
<p>7.	Surface is the opposite of Dive, set rise angle and 0 depth.</p>
<p>8.	Left slider is the required angle.</p>
<p>9.	Right slider is the required depth.</p>

EngineRoom.java
---------------
<img src="https://raw.githubusercontent.com/CharlieSwires/SubmarineUI/master/EngineRoomNoComms.png" width="500" height="500" alt="EngineRoomNoComms" title="Engine Room No Comms"/>
<p>1.	Emergency Left full right forward full left reverse.</p>
<p>2.	Emergency Right full right reverse full left forward.</p>
<p>3.	Emergency Reverse full both back</p>
<p>4.	All Stop both stop</p>
<p>5.	All Full both full forward.</p>
<p>6.	Common used to signal both motors forward or backward same.</p>
<p>7.	Left by itself</p>
<p>8.	Right by itself.</p>

Navigation.java
---------------
<img src="https://raw.githubusercontent.com/CharlieSwires/SubmarineUI/master/NavigationRoom.png" width="500" height="500" alt="NavigationRoom" title="Navigation Room"/>
<p>1.	Set Course is pressed following of the setting of the absolute bearing and changes the line to green from red.</p>
<p>2.	Rudder operates when the set course is not green.</p>
<p>3.	Relative spins the dial so you can match your bearing with the real world, black line is the bearing as is the text.</p>

All Together
------------
<img src="https://raw.githubusercontent.com/CharlieSwires/SubmarineUI/master/All.png" width="800" height="450" alt="All" title="All Together"/>

<p> Const contains the IP address of the Pi and the port number. </p>
<p> GenericGet contains the get request used by all of the UI.</p>
<p> pid contains the pid controller used for dive angle and navigation.</p>
