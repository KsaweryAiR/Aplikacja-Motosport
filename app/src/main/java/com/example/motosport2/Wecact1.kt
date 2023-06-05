package com.example.motosport2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class Wecact1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wecact1)


        val homeCardView = findViewById<CardView>(R.id.website)
        homeCardView.setOnClickListener {
            val url = "https://www.fiawec.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val homeCardView1 = findViewById<CardView>(R.id.calendar)
        homeCardView1.setOnClickListener {
            val intent = Intent(this@Wecact1, Wecact2::class.java)
            startActivity(intent)

        }

        val homeCardView2 = findViewById<CardView>(R.id.klasyfikacja)
        homeCardView2.setOnClickListener {
            val url = "https://www.fiawec.com/en/season/result/4153"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)

        }
        val homeCardView3 = findViewById<CardView>(R.id.info)
        homeCardView3.setOnClickListener {
            val intent = Intent(this@Wecact1, Wecact3::class.java)
            startActivity(intent)

        }

    }
}