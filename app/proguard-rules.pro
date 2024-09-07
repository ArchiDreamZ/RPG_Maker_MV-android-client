# 保留应用程序入口Activity
#-keep public class jp.hbox.ImoutoFantasy.WebPlayerActivity { *; }

# 移除不必要的Kotlin相关规则（如果您的项目不是用Kotlin编写的，可以完全移除Kotlin相关规则）
-dontwarn kotlin.**
-keep class kotlin.** { *; }
#-keepclassmembers class kotlin.** { *; }
-keep class kotlin.reflect.jvm.internal.impl.** { *; }
#-keepclassmembers class kotlin.reflect.jvm.internal.impl.** { *; }

# 移除 DebugProbesKt 类及其所有成员
-dontwarn kotlin.io.*

# 移除 DebugProbesKt.bin 文件
-assumenosideeffects class kotlin.io.DebugProgenitorKt {
    public static void flush();
    public static void reset();
}
