package com.example.happybirthday

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.net.InetAddress
import java.net.Socket

class MainActivity : AppCompatActivity() {
    lateinit var editText : EditText
    lateinit var client: Socket
    override fun onCreate(savedInstanceState: Bundle?) {
        // Thread.sleep(10_000)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var server_hostname = "Filo.local"
        val address: InetAddress = InetAddress.getByName(server_hostname)
        println("Resorved $server_hostname to $address.hostAddress")

        var START_PORT = 10940
        var END_PORT = START_PORT + 100
        for (i in START_PORT..END_PORT) {
            try {
                println("Checking port: $i")
                client = Socket(address, i)
                println("Using port: $i")
                break
            }
            catch (e: java.lang.Exception) {
                if (i == END_PORT) {
                    throw java.lang.Exception("No port found")
                }
            }
        }

        title = "Input Snatcher"
        editText = findViewById(R.id.editText)
        var previous_editText: CharSequence = ""
        editText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                var diff: Int = previous_editText.length
                for (i in 0..(previous_editText.length-1)) {
                    println(previous_editText.length.toString())
                    println(s[i].toString() + previous_editText[i].toString())
                    print((s[i] != previous_editText[i]).toString())
                    if (s[i] != previous_editText[i]) {
                        println("DIFFERENCE DETECTED")
                        diff = i
                        break
                    }
                }

                println("diff: " + diff.toString())


                var msg: String
                if (before < count) {
                    msg = "<<backspace:"+ (previous_editText.length - diff).toString() + ">>" + s.subSequence(diff, s.length).toString()
                }
                else {
                    msg = "<<backspace:"+ (before - count).toString() + ">>"
                }

                client.outputStream.write(msg.toByteArray())
                previous_editText = s.toString()
            }
        })
    }
}
