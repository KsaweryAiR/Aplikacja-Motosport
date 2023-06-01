package com.example.motosport2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.cardview.widget.CardView


class Indycaract1 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_indycaract1)

        val homeCardView = findViewById<CardView>(R.id.website)
        homeCardView.setOnClickListener {
            val url = "https://www.indycar.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        val homeCardView1 = findViewById<CardView>(R.id.calendar)
        homeCardView1.setOnClickListener {
                val intent = Intent(this@Indycaract1, Indycaract2::class.java)
                startActivity(intent)

        }

        val homeCardView2 = findViewById<CardView>(R.id.klasyfikacja)
        homeCardView2.setOnClickListener {
            val url = "https://www.indycar.com/Results"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)

        }
        val homeCardView3 = findViewById<CardView>(R.id.info)
        homeCardView3.setOnClickListener {
            val intent = Intent(this@Indycaract1, Indycaract3::class.java)
            startActivity(intent)

        }

    }
}