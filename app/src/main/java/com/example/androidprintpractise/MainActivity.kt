package com.example.androidprintpractise



import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.emh.thermalprinter.EscPosPrinter
import com.emh.thermalprinter.connection.tcp.TcpConnection
import com.emh.thermalprinter.exceptions.EscPosConnectionException
import com.emh.thermalprinter.textparser.PrinterTextParserImg
import com.example.androidprintpractise.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnPrint.setOnClickListener {
            val orderNumber = "1125"
            val items = listOf("Pizza" to 13.0, "Burger" to 10.0, "Drink" to 3.0)
            val customerInfo = "EM Haseeb\n14 Streets\nCantt, LHR\nTel: +923040017916"

            printOne(orderNumber, items, customerInfo)

        }
    }




    private fun print(){
        GlobalScope.launch {
            try {
                val printer = EscPosPrinter(TcpConnection("192.168.0.10",9100), 203, 80f, 42)
                printer.printFormattedTextAndCut(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        applicationContext.resources.getDrawableForDensity(
                            R.drawable.logo,
                            DisplayMetrics.DENSITY_MEDIUM
                        )
                    ) + "</img>\n" +
                            "[L]\n" +
                            "[C]<u><font size='big'>ORDER N°1125</font></u>\n[L]\n" +
                            "[L] _________________________________________\n" +
                            "[L] Description [R]Amount\n[L]\n" +
                            "[L] <b>Beef Burger [R]10.00\n" +
                            "[L] Sprite-200ml [R]3.00\n" +
                            "[L] _________________________________________\n" +
                            "[L] TOTAL [R]13.00 BD\n" +
                            "[L] Total Vat Collected [R]1.00 BD\n" +
                            "[L]\n" +
                            "[L] _________________________________________\n" +
                            "[L]\n" +
                            "[C]<font size='tall'>Customer Info</font>\n" +
                            "[L] EM Haseeb\n" +
                            "[L] 14 Streets\n" +
                            "[L] Cantt, LHR\n" +
                            "[L] Tel : +923040017916\n" +
                            "[L]\n" +
                            "[L] <barcode type='ean13' height='10'>831254784551</barcode>\n[L]\n" +
                            "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n[L]\n[L]\n[L]\n"
                )
                printer.disconnectPrinter()
            } catch (e: EscPosConnectionException) {
                e.printStackTrace()
            }
        }.start()
    }


    private fun printOne(orderNumber: String, items: List<Pair<String, Double>>, customerInfo: String) {
        GlobalScope.launch {
            try {
                val printer = EscPosPrinter(TcpConnection("192.168.0.10", 9100), 203, 65f, 42)

                val itemsText = items.joinToString("\n") { "[L] ${it.first} [R] ${it.second}" }

                val printText = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    applicationContext.resources.getDrawableForDensity(
                        R.drawable.logo,
                        DisplayMetrics.DENSITY_MEDIUM
                    )
                ) + "</img>\n" +
                        "[L]\n" +
                        "[L] <u><font size='big'>ORDER N°$orderNumber</font></u>\n[L]\n" +
                        "[L] _________________________________________\n" +
                        "[L] Description [R] Amount\n[L]\n" +
                        itemsText +
                        "\n[L] _________________________________________\n" +
                        "[L] TOTAL [R] ${items.sumByDouble { it.second }} BD\n" +
                        "[L]\n" +
                        "[L] _________________________________________\n" +
                        "[L]\n" +
                        "[C]<font size='tall'>Customer Info</font>\n" +
                        customerInfo +
                        "\n[L]\n" +
                        "[L]\n[L]\n[L]\n"

                printer.printFormattedTextAndCut(printText)
                printer.disconnectPrinter()
            } catch (e: EscPosConnectionException) {
                e.printStackTrace()
            }
        }
    }

}
