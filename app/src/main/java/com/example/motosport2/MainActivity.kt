package com.example.motosport2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

import android.annotation.SuppressLint
import android.os.AsyncTask
import org.json.JSONObject
import java.net.URL



class MainActivity : AppCompatActivity() {

//___________________________________________FORMULA 1______________________________________________
    private lateinit var f1Text: TextView
    private lateinit var f1GP: TextView
    //___________________________________________FORMULA 1___________________________________________
    //__________________________________________INDY CAR_____________________________________________
    private lateinit var raceNameTextView: TextView
    private lateinit var countdownTextView: TextView
    //__________________________________________INDY CAR_____________________________________________
    //__________________________________________WEC_____________________________________________
    private lateinit var raceNameTextView2: TextView
    private lateinit var countdownTextView2: TextView
    //__________________________________________WEC_____________________________________________
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//___________________________________________FORMULA 1______________________________________________
        f1Text = findViewById(R.id.f1text)
        f1GP = findViewById(R.id.f1GP)

        FetchRaceTask().execute()

     //___________________________________________FORMULA 1______________________________________________
    //__________________________________________INDY CAR_____________________________________________
        raceNameTextView = findViewById(R.id.icGP)
        countdownTextView = findViewById(R.id.ictext)

        val races = readRacesFromExcelFile()

        val closestRace = findClosestRace(races)

        if (closestRace != null) {
            raceNameTextView.text = closestRace.name
            countdownTextView.text = getCountdownString(closestRace.date, closestRace.time)
        }
    //__________________________________________INDY CAR_____________________________________________
        //__________________________________________WEC_____________________________________________
        raceNameTextView2 = findViewById(R.id.wecGP)
        countdownTextView2 = findViewById(R.id.wectext)

        val races2 = readRacesFromExcelFile2()

        val closestRace2 = findClosestRace2(races2)

        if (closestRace2 != null) {
            raceNameTextView2.text = closestRace2.name
            countdownTextView2.text = getCountdownString2(closestRace2.date, closestRace2.time)
        }
        //__________________________________________WEC_____________________________________________
    }


    //___________________________________________FORMULA 1______________________________________________
    private inner class FetchRaceTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void): String {
            // Pobranie danych o najbliższym wyścigu z Ergast Developer API
            val result: String
            val url = "https://ergast.com/api/f1/current.json"
            try {
                result = URL(url).readText()
            } catch (e: Exception) {
                return ""
            }
            return result
        }

        @SuppressLint("StringFormatMatches")
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (result.isNotEmpty()) {
                val races = JSONObject(result).getJSONObject("MRData")
                    .getJSONObject("RaceTable")
                    .getJSONArray("Races")

                var nextRaceIndex = 0
                var nextRace: JSONObject? = null

                // Szukanie najbliższego nieodbytego wyścigu
                while (nextRaceIndex < races.length()) {
                    val currentRace = races.getJSONObject(nextRaceIndex)
                    val raceDate = currentRace.getString("date")
                    val raceDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val raceDateObj = raceDateFormat.parse(raceDate)
                    val currentDate = Date()

                    if (raceDateObj.after(currentDate)) {
                        nextRace = currentRace
                        break
                    }

                    nextRaceIndex++
                }

                if (nextRace != null) {
                    // Pobieranie informacji o najbliższym nieodbytym wyścigu
                    val raceDate = nextRace.getString("date")
                    val raceName = nextRace.getString("raceName")

                    // Obliczanie czasu pozostałego do wyścigu
                    val raceDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val raceDateObj = raceDateFormat.parse(raceDate)

                    val currentDate = Date()
                    val diffInMs = raceDateObj.time - currentDate.time
                    val diffInDays = diffInMs / (24 * 60 * 60 * 1000)
                    val diffInHours = (diffInMs / (60 * 60 * 1000) % 24)
                    val diffInMins = (diffInMs / (60 * 1000) % 60)

                    // Aktualizacja napisu z informacją o czasie pozostałym do wyścigu
                    val countDownString = getString(
                        R.string.countdown_string,
                        diffInDays,
                        diffInHours,
                        diffInMins
                    )
                    val raceLocation = getString(
                        R.string.locationf1,
                        raceName,
                    )
                    f1Text.text = countDownString
                    f1GP.text = raceLocation
                } else {
                    // wyświetlenie informacji, gdy nie znaleziono żadnego wyścigu
                    f1Text.text = "Brak informacji o wyścigu"
                    f1GP.text = ""
                }
            } else {
                // wyświetlenie informacji o błędzie
                f1Text.text = "Błąd pobierania danych"
                f1GP.text = ""
            }
        }
    }
    //__________________________________________INDY CAR_____________________________________________
    private fun readRacesFromExcelFile(): List<Race> {
        val races = mutableListOf<Race>()

        //val file = File(filesDir, "IndyCarShe.xlsx")
        val inputStream = assets.open("IndyCarShe.xlsx")

        //val inputStream = FileInputStream(file)

        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex)
            val dateCell = row.getCell(0)
            val timeCell = row.getCell(1)
            val nameCell = row.getCell(3)

            if (dateCell != null && timeCell != null && nameCell != null) {
                val date = dateCell.localDateTimeCellValue.toLocalDate()
                val time = timeCell.localDateTimeCellValue.toLocalTime()
                val name = nameCell.stringCellValue

                races.add(Race(date, time, name))
            }
        }

        workbook.close()
        inputStream.close()

        return races
    }

    private fun findClosestRace(races: List<Race>): Race? {
        val now = LocalDateTime.now()

        return races.filter { it.dateTime.isAfter(now) }
            .minByOrNull { it.dateTime }
    }

    private fun getCountdownString(date: LocalDate, time: LocalTime): String {
        val dateTime = LocalDateTime.of(date, time)
        val now = LocalDateTime.now()

        val days = Duration.between(now, dateTime).toDays()
        val hours = Duration.between(now, dateTime).toHours() % 24
        val minutes = Duration.between(now, dateTime).toMinutes() % 60

        return "Do wyścigu pozostało: $days dni, $hours godzin i $minutes minut"
    }

    private data class Race(val date: LocalDate, val time: LocalTime, val name: String) {
        val dateTime: LocalDateTime
            get() = LocalDateTime.of(date, time)
    }
    //__________________________________________INDY CAR_____________________________________________
    //__________________________________________WEC_____________________________________________
    private fun readRacesFromExcelFile2(): List<Race> {
        val races2 = mutableListOf<Race>()

        //val file = File(filesDir, "IndyCarShe.xlsx")
        val inputStream = assets.open("WECShe.xlsx")

        //val inputStream = FileInputStream(file)

        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex)
            val dateCell = row.getCell(0)
            val timeCell = row.getCell(1)
            val nameCell = row.getCell(3)

            if (dateCell != null && timeCell != null && nameCell != null) {
                val date = dateCell.localDateTimeCellValue.toLocalDate()
                val time = timeCell.localDateTimeCellValue.toLocalTime()
                val name = nameCell.stringCellValue

                races2.add(Race(date, time, name))
            }
        }

        workbook.close()
        inputStream.close()

        return races2
    }

    private fun findClosestRace2(races2: List<Race>): Race? {
        val now = LocalDateTime.now()

        return races2.filter { it.dateTime.isAfter(now) }
            .minByOrNull { it.dateTime }
    }

    private fun getCountdownString2(date: LocalDate, time: LocalTime): String {
        val dateTime = LocalDateTime.of(date, time)
        val now = LocalDateTime.now()

        val days = Duration.between(now, dateTime).toDays()
        val hours = Duration.between(now, dateTime).toHours() % 24
        val minutes = Duration.between(now, dateTime).toMinutes() % 60

        return "Do wyścigu pozostało: $days dni, $hours godzin i $minutes minut"
    }

    private data class Race2(val date: LocalDate, val time: LocalTime, val name: String) {
        val dateTime: LocalDateTime
            get() = LocalDateTime.of(date, time)
    }
    //__________________________________________WEC_____________________________________________
}
