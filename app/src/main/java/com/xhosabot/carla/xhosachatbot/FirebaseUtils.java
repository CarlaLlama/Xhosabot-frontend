package com.xhosabot.carla.xhosachatbot;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseUtils {
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    private FirebaseFirestore mFirestore;

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