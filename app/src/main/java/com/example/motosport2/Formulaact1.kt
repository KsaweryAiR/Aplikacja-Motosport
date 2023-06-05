package com.example.motosport2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class Formulaact1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulaact1)


        val homeCardView = findViewById<CardView>(R.id.website)
        homeCardView.setOnClickListener {
            val url = "https://www.formula1.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val homeCardView1 = findViewById<CardView>(R.id.calendar)
        homeCardView1.setOnClickListener {
            val intent = Intent(this@Formulaact1, Formulaact2::class.java)
            startActivity(intent)

        }



        val homeCardView2 = findViewById<CardView>(R.id.klasyfikacja)
        homeCardView2.setOnClickListener {
            val url = "https://www.formula1.com/en/results.html/2023/drivers.html"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)

        }

        val homeCardView3 = findViewById<CardView>(R.id.info)
        homeCardView3.setOnClickListener {
            val intent = Intent(this@Formulaact1, Formulaact3::class.java)
            startActivity(intent)

        }


    }
}