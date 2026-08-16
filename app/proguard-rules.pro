# Project-specific R8 rules for the release build.
#
# Most of the stack ships its own consumer rules (Compose, Glance, DataStore, Koin,
# kotlinx.serialization, Timber), so this file only covers what is specific to this app and
# genuinely at risk. Add a rule only with a comment explaining what breaks without it — an
# unexplained keep is impossible to retire later.

# Navigation 3 persists the back stack by serializing each NavKey. The default serial name is the
# fully-qualified class name, so obfuscating these route types can make a previously saved back
# stack fail to deserialize after an app update. Keep the types and their generated serializers.
-keep,allowobfuscation @kotlinx.serialization.Serializable class com.handysparksoft.shakelamp.**
-keepclassmembers class com.handysparksoft.shakelamp.** {
    *** Companion;
    <fields>;
}
-keepclasseswithmembers class com.handysparksoft.shakelamp.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Services, the Glance widget receiver, and the Quick Settings tile are instantiated by name from
# the merged manifest, never referenced from Kotlin. R8 cannot see those entry points.
-keep class com.handysparksoft.shakelamp.**.*Service { <init>(); }
-keep class com.handysparksoft.shakelamp.widget.QuickPhraseWidgetReceiver { <init>(); }

# Glance resolves ActionCallback implementations reflectively by class name when a widget is
# tapped, so TransmitAction has no compile-time reference from app code.
-keep class * implements androidx.glance.appwidget.action.ActionCallback { <init>(); }

# Room loads its generated database implementation reflectively:
# Class.forName("<Database>_Impl").newInstance(). Room ships
# `-keep class * extends androidx.room.RoomDatabase`, which preserves the class NAME but not its
# members, so R8 strips the no-arg constructor it cannot see being called. The class then loads
# and fails to instantiate, throwing InstantiationException at app startup:
#   Failed to create an instance of androidx.work.impl.WorkDatabase
# This crashed every launch in 2.0.0 (versionCode 20) via WorkManager's InitializationProvider.
-keep class * extends androidx.room.RoomDatabase { <init>(); }

# Same reflective pattern: Room resolves the auto-migration and callback specs by name too.
-keep class * extends androidx.room.migration.AutoMigrationSpec { <init>(); }

# WorkManager instantiates Workers reflectively from the class name stored in its database.
-keep class * extends androidx.work.ListenableWorker { <init>(...); }
