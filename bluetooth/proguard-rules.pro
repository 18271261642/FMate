# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#noinspection ShrinkerUnresolvedReference
-keep class com.shon.bluetooth.core.**{*;}

-keep class com.shon.connector.call.**{*;}
-keep class com.shon.connector.bean.**
-keep class com.shon.connector.Config{*;}
-keep  class com.shon.connector.BleWrite{*;}
-keep class com.shon.bluetooth.BLEManager{*;}
-keep class com.shon.connector.**{*;}
 -keep class no.nordicsemi.** {*; }
 -keep class no.nordicsemi.android.support.v18.**{*;}