package co.gov.isabu.showcase.activities

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.gov.isabu.showcase.R
import co.gov.isabu.showcase.helpers.StorageHelper

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash)

        Toast.makeText(
            this,
            "Inicializando recursos multimedia.",
            Toast.LENGTH_SHORT)
            .show()

        StorageHelper(this)

    }

}
