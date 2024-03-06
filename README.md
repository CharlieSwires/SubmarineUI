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

User Interface
--------------

DepthKeeping.java
-----------------

EngineRoom.java
---------------

Navigation.java
---------------

<p> Const contains the IP address of the Pi and the port number. </p>
<p> GenericGet contains the get request used by all of the UI.</p>
<p> pid contains the pid controller used for dive angle and navigation.</p>