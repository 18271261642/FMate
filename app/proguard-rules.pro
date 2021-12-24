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
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
-keep   class com.amap.api.trace.**{*;}

#    3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

#    定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.loc.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#    搜索
-keep   class com.amap.api.services.**{*;}

#    2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

#    导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}
#    Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature

-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);}
    -keepattributes SourceFile,LineNumberTable
    -keepnames class * implements java.io.Serializable

    # 保留自定义控件(继承自View)不能被混淆
    -keep public class * extends android.view.View {
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
        public void set*(***);
        *** get* ();
    }
    #微信分享
    -keep class com.tencent.mm.opensdk.** {
        *;
    }

    -keep class com.tencent.wxop.** {
        *;
    }

    -keep class com.tencent.mm.sdk.** {
        *;
    }
     #实体类不参与混淆
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }
    #排除OKHTTP
    -dontwarn com.squareup.**
    -dontwarn okio.**
    -keep public class org.codehaus.* { *; }
    -keep public class java.nio.* { *; }
    #排除天气sdk
    -keep public class com.qweather.plugin.bean.** { *; }
    -keep public class com.qweather.plugin.view.** { *; }

    -keep class com.example.xingliansdk.network.**{*;}
    -keep class com.example.otalib.**{*;}
    -keep class no.nordicsemi.android.dfu.** {*; }
    -keep class com.example.xingliansdk.ui.sleep.**{*;}
    -keep class com.example.xingliansdk.bean.**{*;}
    -keep class com.shon.connector.bean.**{*;}
    -keep class com.example.xingliansdk.ui.fragment.map.**{*;}
     -keep class com.example.xingliansdk.service.**{*;}

     -keep public class com.sina.weibo.sdk.**{*;}
     -keep public class com.example.xingliansdk.listeners.**{*;}