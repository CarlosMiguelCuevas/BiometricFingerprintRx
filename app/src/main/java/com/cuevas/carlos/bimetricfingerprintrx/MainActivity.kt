package com.cuevas.carlos.bimetricfingerprintrx

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.cuevas.carlos.biomatricfingerprint.BiometricManager
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = "${AppCompatActivity::class.java.simpleName} respuesta"
    }

    private lateinit var subscription: Disposable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


    }

    override fun onResume() {
        super.onResume()
        fab.setOnClickListener { _ ->
            fab.setOnClickListener { _ ->
                subscription = BiometricManager.BiometricBuilder(this)
                    .setTitle("Huella")
                    .setDescription("escanea la huella si queires pasar")
                    .setNegativeButtonText("a me qeuivoque")
                    .setSubtitle("no se donde va esto")
                    .build()
                    .authenticate()
                    .subscribe({ response -> Log.v(TAG, "${response.name}: ${response.message}") },
                        { error -> Log.e(TAG, error.message) })
            }
        }

    }

    override fun onPause() {
        super.onPause()
        subscription.dispose()
    }

}


