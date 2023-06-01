package com.example.motosport2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class Indycaract2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_indycaract2)

        setTextViewsFromExcel()
    }

    private fun setTextViewsFromExcel() {
        val texIds = arrayOf(
            R.id.tex1, R.id.tex2, R.id.tex3, R.id.tex4, R.id.tex5,
            R.id.tex6, R.id.tex7, R.id.tex8, R.id.tex9, R.id.tex10,
            R.id.tex11, R.id.tex12, R.id.tex13, R.id.tex14, R.id.tex15,
            R.id.tex16, R.id.tex17
        )
        val tetIds = arrayOf(
            R.id.tet1, R.id.tet2, R.id.tet3, R.id.tet4, R.id.tet5,
            R.id.tet6, R.id.tet7, R.id.tet8, R.id.tet9, R.id.tet10,
            R.id.tet11, R.id.tet12, R.id.tet13, R.id.tet14, R.id.tet15,
            R.id.tet16, R.id.tet17
        )

        val races = readRacesFromExcelFile()

        for (i in 0 until texIds.size) {
            val texTextView = findViewById<TextView>(texIds[i])
            val tetTextView = findViewById<TextView>(tetIds[i])

            if (i < races.size) {
                val race = races[i]
                texTextView.text = race.first // Ustawienie daty
                tetTextView.text = race.second // Ustawienie nazwy
            } else {
                texTextView.text = "" // Ustawienie pustego tekstu, jeśli brak danych
                tetTextView.text = "" // Ustawienie pustego tekstu, jeśli brak danych
            }
        }
    }

    private fun readRacesFromExcelFile(): List<Pair<String, String>> {
        val races = mutableListOf<Pair<String, String>>()

        val inputStream = assets.open("IndyCarShe.xlsx")
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        val dateFormatter: DateFormat = SimpleDateFormat("dd.MM.yyyy")

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex)
            val dateCell = row.getCell(0) // data
            val nameCell = row.getCell(3) // name

            if (dateCell != null && nameCell != null) {
                val date = if (dateCell.cellType == CellType.NUMERIC) {
                    val dateValue = dateCell.numericCellValue
                    val calendar = Calendar.getInstance()
                    calendar.time = DateUtil.getJavaDate(dateValue)
                    dateFormatter.format(calendar.time)
                } else {
                    dateCell.stringCellValue
                }

                val name = nameCell.stringCellValue

                races.add(Pair(date, name))
            }
        }

        workbook.close()
        inputStream.close()

        return races
    }
}




