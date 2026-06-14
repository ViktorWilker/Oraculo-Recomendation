package br.com.oraculo_recomendation

import android.app.Application
import br.com.oraculo_recomendation.data.MockMediaRepository

class OraculoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MockMediaRepository.initialize(this)
    }
}