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
-keep class com.google.** { *; }
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

# https://github.com/square/okhttp/pull/6792
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**
#-keep class ** { *; }

-keep class kotlin.coroutines.Continuation { *; }

-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}

#-keep class com.** { *; }

#-keep class android.** { *; }
#-keep class androidx.** { *; }
-keep class retrofit2.** { *; }

#-keep class okhttp3.** { *; }
#-keep class okio.** { *; }
#-keep class org.** { *; }

#-keep class dagger.** { *; }
#-keep class dalvik.** { *; }
#-keep class hilt_aggregated_deps.** { *; }
#-keep class java.** { *; }
#-keep class javax.** { *; }


-keep class kotlinx.** { *; }
-keep class com.gusto.pikgoogoo.data.** { *; }
-keep class com.gusto.pikgoogoo.api.** { *; }
-keep class com.gusto.pikgoogoo.util.** { *; }
-keep class com.gusto.pikgoogoo.adapter.** { *; }
-keep class com.gusto.pikgoogoo.di.** { *; }
-keep class com.gusto.pikgoogoo.ui.** { *; }
-keep class com.gusto.pikgoogoo.MyApplication { *; }
-keepattributes Signature