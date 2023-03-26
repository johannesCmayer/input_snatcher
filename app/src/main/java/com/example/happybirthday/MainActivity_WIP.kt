package com.example.happybirthday

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.min
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class MainActivity : AppCompatActivity() {
    lateinit var editText : EditText
    lateinit var client: Socket

    fun triggerRestart(context: Activity) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        Thread.sleep(10_000)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        title = "Input Snatcher"
        editText = findViewById(R.id.editText)
        var previous_editText: CharSequence = ""

        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        var addresses = mutableListOf(
            // Motoko hotspot
            "192.168.209.225",
            // Trajan
            "192.168.5.250",
            "169.254.199.182",
        )

        try {
            var server_hostname = "Filo.local"
            val host_name_address: InetAddress = InetAddress.getByName(server_hostname)
            addresses.add(host_name_address.toString())
        }
        catch (e: java.lang.Exception) {}

        var START_PORT = 10940
        var END_PORT = START_PORT + 15

        var msg = ""
        var breaking = false
        for (port in START_PORT..END_PORT) {
            for (address in addresses) {
                try {
                    println("Checking if server is at: $address:$port")
                    client = Socket()
                    client.connect(InetSocketAddress(address, port), 100)
                    msg = "Server found at: $address:$port"
                    breaking = true
                    if (breaking) {break}
                } catch (e: java.lang.Exception) {
                    println(e.toString())
                    if (port == END_PORT) {
                        msg = "No server found. No input will be send."
                    }
                }
            }
            if (breaking) {break}
        }
        println(msg)
        var ip_status: TextView = findViewById(R.id.textView)
        ip_status.text = msg

        var restart_button: Button = findViewById(R.id.restart_button)
        restart_button.setOnClickListener {
            triggerRestart(this)
        }

        var context = this

        editText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                var diff: Int = previous_editText.length
                for (i in 0..min(previous_editText.length-1, s.length-1)) {
                    println(previous_editText.length.toString())
                    println(s[i].toString() + previous_editText[i].toString())
                    print((s[i] != previous_editText[i]).toString())
                    if (s[i] != previous_editText[i]) {
                        println("DIFFERENCE DETECTED")
                        diff = i
                        break
                    }
                }
// Hello there yo whot testing thes thing now you
                println("diff: " + diff.toString())


                var msg: String
                if (before < count) {
                    msg = "<<backspace:"+ (previous_editText.length - diff).toString() + ">>" + s.subSequence(diff, s.length).toString()
                }
                else {
                    msg = "<<backspace:"+ (before - count).toString() + ">>"
                }

                try {
                    client.outputStream.write(msg.toByteArray())
                }
                catch (e: java.lang.Error) {
                    triggerRestart(context)
                }
                previous_editText = s.toString()
            }
        })
    }
}
