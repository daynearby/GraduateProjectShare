# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontobfuscate
-keep class com.duanqu.qupai.jni.Releasable
-keep class com.duanqu.qupai.jni.ANativeObject
-dontwarn com.google.common.primitives.**
-dontwarn com.google.common.cache.**
-dontwarn com.google.auto.common.**
-dontwarn com.google.auto.factory.processor.**
-dontwarn com.fasterxml.jackson.**
-dontwarn net.jcip.annotations.**
-dontwarn javax.annotation.**
-dontwarn org.apache.http.client.utils.URIUtils

#-- start ---添加混淆，app使用到的gson、volley、bmob---
#如果引用了v4或者v7包
#-dontwarn android.support.**
-dontwarn com.young.share.**
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class com.actionbarsherlock.** { *; }
-keep interface com.actionbarsherlock.** { *; }
-keepattributes *Annotation*
-keep public class * extends android.app.Activity
-keep public class * extends android.support.v7.AppCompatActivity
-keep public class * extends android.support.v4.app.Fragment

-keep class android.support.v7.widget.SearchView{*;}
-keep class android.support.v7.view.SupportMenuInflater{*;}
-keep class android.support.v4.view.ActionProvider{*;}
-keep class com.young.share.views.actionProvider.MapSearchProvider{*;}
-dontwarn com.google.gson.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn com.android.volley.error.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
-dontwarn com.android.volley.toolbox.**

# 这里根据具体的SDK版本修改，在gradle中配置了，这里再配置会出现重复配置的错误
#-libraryjars libs/BmobSDK_v3.4.5_1111.jar
# 忽略警告
-ignorewarning

-keepattributes Signature
-keep class cn.bmob.v3.** {*;}

# 保证继承自BmobObject、BmobUser类的JavaBean不被混淆
-keep class com.young.share.model.MyUser{*;}
-keep class com.young.share.model.ShareMessage_HZ{*;}
-keep class com.young.share.model.Comment_HZ{*;}
-keep class com.young.share.model.Collection_HZ{*;}
-keep class com.young.share.model.DiscountMessage_HZ{*;}
-keep class com.young.share.model.Message_HZ{*;}
-keep class com.young.share.model.MyBmobInstallation{*;}

# 使用了okhttp、okio的包，请添加以下混淆代码
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-keep interface com.squareup.okhttp.** { *; }
-dontwarn okio.**

#----end --  app使用到的gson、volley、bmob，其他的为趣拍的混淆---
-keep class javax.annotation.** { *; }

-keep class * extends com.duanqu.qupai.jni.ANativeObject
-keep @com.duanqu.qupai.jni.AccessedByNative class *
-keep class com.duanqu.qupai.bean.DIYOverlaySubmit
-keep public interface com.duanqu.qupai.android.app.QupaiServiceImpl$QupaiService {*;}
-keep class com.duanqu.qupai.android.app.QupaiServiceImpl

-keep class com.duanqu.qupai.BeautySkinning
-keep class com.duanqu.qupai.render.BeautyRenderer
-keep public interface com.duanqu.qupai.render.BeautyRenderer$Renderer {*;}
-keepclassmembers @com.duanqu.qupai.jni.AccessedByNative class * {
    *;
}
-keepclassmembers class * {
    @com.duanqu.qupai.jni.AccessedByNative *;
}
-keepclassmembers class * {
    @com.duanqu.qupai.jni.CalledByNative *;
}

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclassmembers class * {
    native <methods>;
}
-keepclassmembers class com.duanqu.qupai.** {
    *;
}

-keep class com.duanqu.qupai.recorder.EditorCreateInfo$VideoSessionClientImpl {
    *;
}
-keep class com.duanqu.qupai.recorder.EditorCreateInfo$SessionClientFctoryImpl {
    *;
}
-keep class com.duanqu.qupai.recorder.EditorCreateInfo{
    *;
}

-keepattributes Signature
-keepnames class com.fasterxml.jackson.** { *; }

#百度地图
#-libraryjars libs/baidumapapi_v2_1_2.jar
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

#友盟sdk
-dontshrink
-dontoptimize
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
#-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
#-dontwarn com.umeng.**
#-dontwarn com.tencent.weibo.sdk.**


-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

#-keep public interface com.tencent.**
#-keep public interface com.umeng.socialize.**
#-keep public interface com.umeng.socialize.sensor.**
#-keep public interface com.umeng.scrshot.**

#-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

#-keep class com.umeng.scrshot.**
#-keep public class com.tencent.** {*;}
#-keep class com.umeng.socialize.sensor.**
#-keep class com.umeng.socialize.handler.**
#-keep class com.umeng.socialize.handler.*
#-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
#-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

#-keep class im.yixin.sdk.api.YXMessage {*;}
#-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}


#-keep class com.tencent.** {*;}
#-dontwarn com.tencent.**
#-keep public class com.umeng.soexample.R$*{
#    public static final int *;
#}
#-keep public class com.umeng.soexample.R$*{
#    public static final int *;
#}
#-keep class com.tencent.open.TDialog$*
#-keep class com.tencent.open.TDialog$* {*;}
#-keep class com.tencent.open.PKDialog
#-keep class com.tencent.open.PKDialog {*;}
#-keep class com.tencent.open.PKDialog$*
#-keep class com.tencent.open.PKDialog$* {*;}
#
#-keep class com.sina.** {*;}
#-dontwarn com.sina.**
#-keep class  com.alipay.share.sdk.** {
#   *;
#}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
#-keep class com.linkedin.** { *; }
-keepattributes Signature