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
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.widget.ImageView
import androidx.cardview.widget.CardView
import org.json.JSONObject
import java.net.URL



class MainActivity : AppCompatActivity() {

//___________________________________________FORMULA 1______________________________________________
    private lateinit var f1Text: TextView //deklarujemy zmienną, która jest prywatna i będzie zainicjalizowana później
    private lateinit var f1GP: TextView //deklarujemy zmienną, która jest prywatna i będzie zainicjalizowana później
    //___________________________________________FORMULA 1___________________________________________
    //__________________________________________INDY CAR_____________________________________________
    private lateinit var raceNameTextView: TextView //deklarujemy zmienną, która jest prywatna i będzie zainicjalizowana później
    private lateinit var countdownTextView: TextView //deklarujemy zmienną, która jest prywatna i będzie zainicjalizowana później
    //__________________________________________INDY CAR_____________________________________________
    //__________________________________________WEC_____________________________________________
    private lateinit var raceNameTextView2: TextView //deklarujemy zmienną, która jest prywatna i będzie zainicjalizowana później
    private lateinit var countdownTextView2: TextView //deklarujemy zmienną, która jest prywatna i będzie zainicjalizowana później
    //__________________________________________WEC_____________________________________________
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //___________________________________________FORMULA 1______________________________________________
        f1Text = findViewById(R.id.f1text) // Przypisujemy do zmiennej f1Text referencję do TextView o id f1text z layoutu
        f1GP = findViewById(R.id.f1GP) // Przypisujemy do zmiennej f1GP referencję do TextView o id f1GP z layoutu

        FetchRaceTask().execute() // Wykonujemy zadanie asynchroniczne FetchRaceTask

        //___________________________________________FORMULA 1______________________________________________

        //__________________________________________INDY CAR_____________________________________________
        raceNameTextView = findViewById(R.id.icGP) // Przypisujemy do zmiennej raceNameTextView referencję do TextView o id icGP z layoutu
        countdownTextView = findViewById(R.id.ictext) // Przypisujemy do zmiennej countdownTextView referencję do TextView o id ictext z layoutu

        val races = readRacesFromExcelFile() // Odczytujemy dane dotyczące wyścigów z pliku Excel

        val closestRace = findClosestRace(races) // Znajdujemy najbliższy wyścig

        if (closestRace != null) {
            raceNameTextView.text = closestRace.name // Ustawiamy nazwę najbliższego wyścigu w raceNameTextView
            countdownTextView.text = getCountdownString(closestRace.date, closestRace.time) // Ustawiamy tekst odliczania czasu w countdownTextView
        }
        //__________________________________________INDY CAR_____________________________________________

        //__________________________________________WEC_____________________________________________
        raceNameTextView2 = findViewById(R.id.wecGP) // Przypisujemy do zmiennej raceNameTextView2 referencję do TextView o id wecGP z layoutu
        countdownTextView2 = findViewById(R.id.wectext) // Przypisujemy do zmiennej

        val races2 = readRacesFromExcelFile2() // Odczytujemy dane dotyczące wyścigów z drugiego pliku Excel

        val closestRace2 = findClosestRace2(races2) // Znajdujemy najbliższy wyścig z drugiego zestawu danych

        if (closestRace2 != null) {
            raceNameTextView2.text = closestRace2.name // Ustawiamy nazwę najbliższego wyścigu w raceNameTextView2
            countdownTextView2.text = getCountdownString2(closestRace2.date, closestRace2.time) // Ustawiamy tekst odliczania czasu w countdownTextView2
        }
        //__________________________________________WEC_____________________________________________
    }

    //___________________________________________FORMULA 1______________________________________________
    private inner class FetchRaceTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void): String {
            // Pobranie danych o najbliższym wyścigu z Ergast Developer API



            val result: String // Deklaracja zmiennej result typu String, która będzie przechowywać rezultat zapytania
            val url = "https://ergast.com/api/f1/current.json" // URL do zasobu API z danymi o najbliższym wyścigu
            try {
                result = URL(url).readText() // Wykonanie zapytania HTTP do API i odczytanie odpowiedzi jako tekst
            } catch (e: Exception) {
                return "" // Jeśli wystąpił błąd podczas pobierania danych, zwracamy pusty tekst
            }
            return result // Zwracamy otrzymany rezultat zapytania

        }


        @SuppressLint("StringFormatMatches")
        override fun onPostExecute(result: String) {
            super.onPostExecute(result) // Wywołanie metody onPostExecute. Super wywołanie używane jest do wywołania metody z klasy nadrzędnej (AsyncTask).
            if (result.isNotEmpty()) { // Sprawdzenie, czy otrzymany rezultat nie jest pusty.
                val races = JSONObject(result).getJSONObject("MRData")   // Analiza danych JSON i przypisanie wyników do zmiennej "races".
                    .getJSONObject("RaceTable")
                    .getJSONArray("Races")

                var nextRaceIndex = 0 // Inicjalizacja zmiennych "nextRaceIndex" i "nextRace"
                var nextRace: JSONObject? = null

                // Szukanie najbliższego nieodbytego wyścigu
                while (nextRaceIndex < races.length()) { //Rozpoczęcie pętli while, która szuka najbliższego nieodbytego wyścigu
                    val currentRace = races.getJSONObject(nextRaceIndex) // Pobranie obiektu wyścigu o indeksie "nextRaceIndex" z tablicy "races"
                    val raceDate = currentRace.getString("date") // Pobranie daty wyścigu i przypisanie jej do zmiennej "raceDate"
                    val raceDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // Inicjalizacja obiektu "raceDateFormat" klasy SimpleDateFormat, który będzie służył do parsowania daty.
                    val raceDateObj = raceDateFormat.parse(raceDate)  // Przekształcenie łańcucha znaków "raceDate" na obiekt daty "raceDateObj" za pomocą metody "parse" z obiektu "raceDateFormat".
                    val currentDate = Date() // Inicjalizacja obiektu "currentDate" klasy Date, reprezentującego bieżącą datę i czas.

                    if (raceDateObj.after(currentDate)) { // Sprawdzenie, czy data wyścigu "raceDateObj" jest po bieżącej dacie "currentDate".
                        nextRace = currentRace // Jeśli tak, przypisanie obiektu wyścigu "currentRace" do zmiennej "nextRace" i przerwanie pętli.
                        break
                    }

                    nextRaceIndex++ // Inkrementacja wartości "nextRaceIndex" o 1, aby przejść do następnego wyścigu.
                }// Koniec pętli while.

                if (nextRace != null) { // Sprawdzenie, czy zmienna "nextRace" zawiera wyścig.
                    // Pobieranie informacji o najbliższym nieodbytym wyścigu.
                    val raceDate = nextRace.getString("date") // Pobranie daty wyścigu.
                    val raceName = nextRace.getString("raceName") // Pobranie nazwy wyścigu.

                    // Obliczanie czasu pozostałego do wyścigu.
                    val raceDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Inicjalizacja obiektu "raceDateFormat" klasy SimpleDateFormat, który będzie służył do parsowania daty.
                    val raceDateObj = raceDateFormat.parse(raceDate) // Przekształcenie łańcucha znaków "raceDate" na obiekt daty "raceDateObj" za pomocą metody "parse" z obiektu "raceDateFormat".

                    val currentDate = Date() // Inicjalizacja obiektu "currentDate" klasy Date, reprezentującego bieżącą datę i czas.
                    val diffInMs = raceDateObj.time - currentDate.time // Obliczenie różnicy czasu między datą wyścigu a bieżącą datą w milisekundach.
                    val diffInDays = diffInMs / (24 * 60 * 60 * 1000) // Obliczenie różnicy czasu w dniach.
                    val diffInHours = (diffInMs / (60 * 60 * 1000) % 24) // Obliczenie różnicy czasu w godzinach.
                    val diffInMins = (diffInMs / (60 * 1000) % 60) // Obliczenie różnicy czasu w minutach.

                    // Aktualizacja napisu z informacją o czasie pozostałym do wyścigu.
                    val countDownString = getString(
                        R.string.countdown_string,
                        diffInDays,
                        diffInHours,
                        diffInMins
                    ) // Przygotowanie łańcucha znaków z formatowaniem, z wykorzystaniem wartości różnicy czasu.
                    val raceLocation = getString(
                        R.string.locationf1,
                        raceName,
                    ) // Przygotowanie łańcucha znaków z formatowaniem, z wykorzystaniem nazwy wyścigu.
                    f1Text.text = countDownString // Ustawienie aktualizowanego tekstu informującego o czasie pozostałym do wyścigu.
                    f1GP.text = raceLocation // Ustawienie aktualizowanego tekstu z nazwą wyścigu.
                } else {
                    // Wyświetlenie informacji, gdy nie znaleziono żadnego wyścigu.
                    f1Text.text = "Brak informacji o wyścigu"
                    f1GP.text = ""
                } // Koniec bloku warunkowego "if-else".
            }
            else {
                // Wyświetlenie informacji o błędzie pobierania danych.
                f1Text.text = "Błąd pobierania danych"
                f1GP.text = ""
            } // Koniec bloku warunkowego "if-

            val imageView = findViewById<ImageView>(R.id.imageView2)
            imageView.setOnClickListener {
                val intent = Intent(this@MainActivity, Indycaract1::class.java)
                startActivity(intent)
            }
        }

    }
    //__________________________________________INDY CAR_____________________________________________
    private fun readRacesFromExcelFile(): List<Race> {
        val races = mutableListOf<Race>() // Inicjalizacja listy "races" do przechowywania obiektów klasy Race.

        //val file = File(filesDir, "IndyCarShe.xlsx") // Plik Excela do odczytu.
        val inputStream = assets.open("IndyCarShe.xlsx") // Otwarcie strumienia wejściowego dla pliku Excela.

        //val inputStream = FileInputStream(file) // Utworzenie strumienia wejściowego z pliku.

        val workbook = XSSFWorkbook(inputStream) // Utworzenie obiektu klasy XSSFWorkbook na podstawie strumienia wejściowego.
        val sheet = workbook.getSheetAt(0) // Pobranie arkusza o indeksie 0 z pliku Excela.

        for (rowIndex in 1..sheet.lastRowNum) { // Przetwarzanie wierszy arkusza, rozpoczynając od indeksu 1.
            val row = sheet.getRow(rowIndex) // Pobranie wiersza o danym indeksie.
            val dateCell = row.getCell(0) // Pobranie komórki w kolumnie 0 (daty).
            val timeCell = row.getCell(1) // Pobranie komórki w kolumnie 1 (czasu).
            val nameCell = row.getCell(3) // Pobranie komórki w kolumnie 3 (nazwy).

            if (dateCell != null && timeCell != null && nameCell != null) { // Sprawdzenie, czy wszystkie komórki są niepuste.
                val date = dateCell.localDateTimeCellValue.toLocalDate() // Pobranie wartości daty z komórki i przekształcenie jej na obiekt LocalDate.
                val time = timeCell.localDateTimeCellValue.toLocalTime() // Pobranie wartości czasu z komórki i przekształcenie jej na obiekt LocalTime.
                val name = nameCell.stringCellValue // Pobranie wartości napisowej z komórki.

                races.add(Race(date, time, name)) // Dodanie nowego obiektu Race do listy "races" na podstawie odczytanych wartości.
            }
        }

        workbook.close() // Zamknięcie pliku Excela.
        inputStream.close() // Zamknięcie strumienia wejściowego.

        return races // Zwrócenie listy "races" zawierającej odczytane wyścigi.
    }


    private fun findClosestRace(races: List<Race>): Race? {
        val now = LocalDateTime.now() // Pobranie bieżącego czasu i daty.

        return races.filter { it.dateTime.isAfter(now) } // Filtracja listy wyścigów, zostawiając tylko te, które są po bieżącym czasie.
            .minByOrNull { it.dateTime } // Znalezienie wyścigu o najbliższym czasie z listy za pomocą porównania czasu wyścigu.
    }

    private fun getCountdownString(date: LocalDate, time: LocalTime): String {
        val dateTime = LocalDateTime.of(date, time) // Tworzenie obiektu LocalDateTime na podstawie daty i czasu.
        val now = LocalDateTime.now() // Pobranie bieżącego czasu i daty.

        val days = Duration.between(now, dateTime).toDays() // Obliczenie liczby dni między bieżącym czasem a czasem wyścigu.
        val hours = Duration.between(now, dateTime).toHours() % 24 // Obliczenie liczby godzin między bieżącym czasem a czasem wyścigu, modulo 24.
        val minutes = Duration.between(now, dateTime).toMinutes() % 60 // Obliczenie liczby minut między bieżącym czasem a czasem wyścigu, modulo 60.

        return "Do wyścigu pozostało: $days dni, $hours godzin i $minutes minut" // Zwrócenie napisu informującego o pozostałym czasie do wyścigu.
    }

    private data class Race(val date: LocalDate, val time: LocalTime, val name: String) {
        val dateTime: LocalDateTime
            get() = LocalDateTime.of(date, time) // Obliczenie pełnego czasu wyścigu na podstawie daty i czasu.
    }

    //__________________________________________INDY CAR_____________________________________________
    //__________________________________________WEC_____________________________________________
    private fun readRacesFromExcelFile2(): List<Race> {
        val races2 = mutableListOf<Race>() // Inicjalizacja pustej listy "races2" dla wyścigów.

        val inputStream = assets.open("WECShe.xlsx") // Otwarcie strumienia wejściowego do pliku Excel "WECShe.xlsx" z zasobów aplikacji.

        //val inputStream = FileInputStream(file)

        val workbook = XSSFWorkbook(inputStream) // Utworzenie obiektu klasy XSSFWorkbook na podstawie strumienia wejściowego.
        val sheet = workbook.getSheetAt(0) // Pobranie arkusza o indeksie 0 (pierwszy arkusz) z workbooka.

        for (rowIndex in 1..sheet.lastRowNum) { // Iteracja po wierszach arkusza, rozpoczynając od indeksu 1 (pomijając nagłówki).
            val row = sheet.getRow(rowIndex) // Pobranie wiersza o danym indeksie.
            val dateCell = row.getCell(0) // Pobranie komórki o indeksie 0 (kolumna daty).
            val timeCell = row.getCell(1) // Pobranie komórki o indeksie 1 (kolumna czasu).
            val nameCell = row.getCell(3) // Pobranie komórki o indeksie 3 (kolumna nazwy wyścigu).

            if (dateCell != null && timeCell != null && nameCell != null) { // Sprawdzenie, czy komórki nie są puste.
                val date = dateCell.localDateTimeCellValue.toLocalDate() // Pobranie wartości daty z komórki i przekształcenie jej na LocalDate.
                val time = timeCell.localDateTimeCellValue.toLocalTime() // Pobranie wartości czasu z komórki i przekształcenie jej na LocalTime.
                val name = nameCell.stringCellValue // Pobranie wartości napisu z komórki.

                races2.add(Race(date, time, name)) // Dodanie nowego obiektu Race do listy races2 na podstawie pobranych danych.
            }
        }

        workbook.close() // Zamknięcie workbooka.
        inputStream.close() // Zamknięcie strumienia wejściowego.

        return races2 // Zwrócenie listy races2 zawierającej wczytane wyścigi.
    }


    private fun findClosestRace2(races2: List<Race>): Race? {
        val now = LocalDateTime.now() // Pobranie aktualnej daty i czasu.

        return races2.filter { it.dateTime.isAfter(now) } // Filtrowanie listy races2, pozostawiając tylko wyścigi przyszłe (po aktualnej dacie i czasie).
            .minByOrNull { it.dateTime } // Znalezienie najbliższego wyścigu spośród wyfiltrowanych wyścigów na podstawie daty i czasu.
    }

    private fun getCountdownString2(date: LocalDate, time: LocalTime): String {
        val dateTime = LocalDateTime.of(date, time) // Tworzenie obiektu LocalDateTime na podstawie podanej daty i czasu.
        val now = LocalDateTime.now() // Pobranie aktualnej daty i czasu.

        val days = Duration.between(now, dateTime).toDays() // Obliczenie różnicy w dniach między aktualną datą i czasem a podaną datą i czasem.
        val hours = Duration.between(now, dateTime).toHours() % 24 // Obliczenie różnicy w godzinach między aktualną datą i czasem a podaną datą i czasem, z uwzględnieniem tylko godzin (reszta z dzielenia przez 24).
        val minutes = Duration.between(now, dateTime).toMinutes() % 60 // Obliczenie różnicy w minutach między aktualną datą i czasem a podaną datą i czasem, z uwzględnieniem tylko minut (reszta z dzielenia przez 60).

        return "Do wyścigu pozostało: $days dni, $hours godzin i $minutes minut" // Zwrócenie sformatowanego napisu z informacją o pozostałym czasie do wyścigu.
    }

    private data class Race2(val date: LocalDate, val time: LocalTime, val name: String) {
        val dateTime: LocalDateTime
            get() = LocalDateTime.of(date, time) // Obliczanie daty i czasu wyścigu na podstawie podanej daty i czasu.
    }

    //__________________________________________WEC_____________________________________________
}
