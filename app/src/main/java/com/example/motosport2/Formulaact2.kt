package com.example.motosport2

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.json.JSONObject
import java.net.URL

class Formulaact2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulaact2)

        FetchRaceTask().execute()
    }

    private inner class FetchRaceTask : AsyncTask<Void, Void, Pair<List<String>, List<String>>>() {
        override fun doInBackground(vararg params: Void): Pair<List<String>, List<String>> {
            val dates = mutableListOf<String>() // Tablica przechowująca daty wyścigów
            val raceNames = mutableListOf<String>() // Tablica przechowująca nazwy wyścigów

            val url =
                "https://ergast.com/api/f1/current.json" // URL do zasobu API z danymi o najbliższym wyścigu

            try {
                val result =
                    URL(url).readText() // Wykonanie zapytania HTTP do API i odczytanie odpowiedzi jako tekst
                val races = JSONObject(result).getJSONObject("MRData")
                    .getJSONObject("RaceTable")
                    .getJSONArray("Races")

                for (i in 0 until races.length()) {
                    val currentRace = races.getJSONObject(i)
                    val raceDate = currentRace.getString("date")
                    val raceName = currentRace.getString("raceName")

                    dates.add(raceDate)
                    raceNames.add(raceName)
                }
            } catch (e: Exception) {
                // Obsługa błędów
            }

            return Pair(dates, raceNames) // Zwracamy parę tablic dat i nazw wyścigów
        }

        override fun onPostExecute(result: Pair<List<String>, List<String>>) {
            val dates = result.first // Przypisanie pierwszej tablicy z pary do zmiennej dates
            val raceNames = result.second // Przypisanie drugiej tablicy z pary do zmiennej raceNames

            for (i in 0 until dates.size) {
                val date = dates[i] // Przypisanie aktualnej daty do zmiennej date
                val raceName = raceNames[i] // Przypisanie aktualnej nazwy wyścigu do zmiennej raceName

                val texId = resources.getIdentifier("tex${i + 1}", "id", packageName)
                val tetId = resources.getIdentifier("tet${i + 1}", "id", packageName)

                if (texId != 0) {
                    val texTextView = findViewById<TextView>(texId)
                    texTextView.text = date
                }

                if (tetId != 0) {
                    val tetTextView = findViewById<TextView>(tetId)
                    tetTextView.text = raceName
                }
            }
        }
    }
}
