package com.example.carla.xhosabot;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseUtils {
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    private static FirebaseDatabase mDatabase;

    private FirebaseFirestore mFirestore;


    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }

    public FirebaseFirestore getFirestore(){
        if(null == mFirestore){
            FirebaseFirestoreSettings firestoreSettings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            mFirestore = FirebaseFirestore.getInstance();
            mFirestore.setFirestoreSettings(firestoreSettings);
        }
        return mFirestore;
    }

    public static void sendIntroMessage() {

    }

}