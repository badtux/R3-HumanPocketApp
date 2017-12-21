package com.rype3.pocket_hrm.realm;


import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class RealmMigrations implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        final RealmSchema schema = realm.getSchema();

   //     if (oldVersion == 1) {
////            final RealmObjectSchema userSchema = schema.get("Location_object");
////            userSchema.addField("test", String.class);
  //      }
    }
}
