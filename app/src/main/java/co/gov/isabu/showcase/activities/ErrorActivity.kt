package co.gov.isabu.showcase.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import co.gov.isabu.showcase.R

class ErrorActivity : AppCompatActivity() {

    /**
     * Create a new activity and assign a simple button switcher to a new activity.
     */

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        val button = findViewById<Button>(R.id.error_button)
        button.setOnClickListener {

            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)

        }

    }

}
