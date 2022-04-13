package com.example.foragingapplicationv2.roomdatabase

import android.app.Application

class ForageSpotApp: Application() {
    val db by lazy {
        ForageSpotDatabase.getInstance(this)
    }
}