# IRoverBT

This document was created to help with getting software and hardware together to facilitate sending IR codes using the PRONTO HEX and the HEX format used in the IRRemote library.

Hardware consists of an Arduino and a Bluetooth module like the HC-06.

Compile the Android software using Android Studio/Gradle and export to your mobile.

Supports Gingerbread and newer Android OSs.

Should only require/request Bluetooth permissions.

You MUST pair your Bluetooth module with your Android phone before launching the App.

There are two fields in the application that show up after connecting to your bluetooth module..

1) Send HEX

2) Send PRONTO

The other buttons are for a hard coded Pronto Test code and to disconnect the bluetooth module from the android device.

The two text fields use totally DIFFERENT code formats. 

For 1, use the EasySender sketch on your Arduino. (Ken Shirriff IRRemote Library - Also on Github)

For 2, use the Send Pronto sketch on your Arduino. (http://irdb.tk/send/)

**Note on sending using Pronto codes: You <b><i>MUST</B></i> put <b>SEND</b> before the code and <b>\n</b> after the code for it to work. Found that out the hard way.

For more details on the sketches and how to use them with your project, check out the originating sites. 

Sketches are located in the "Hardware - Schems and Sketches" folder.

Sketches tested on Arduino Uno and Mini.

All codes and formatting came from online and are listed as Open Source.

I only put them together as a form of proof of concept.

This is my first Android App made in Android Studio.

My next step will be to set up static buttons and load the codes arbritrarily using a layout file that you can import into the app through the app rather that entering the code manually in the text boxes.

I hope that the codes I was able to put together here can come in handy for you.

Enjoy and thanks for looking in.

          gemmcom
