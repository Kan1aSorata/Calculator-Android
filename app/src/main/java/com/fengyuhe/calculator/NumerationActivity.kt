package com.fengyuhe.calculator

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.fengyuhe.calculator.databinding.ActivityNumerationBinding
import java.lang.StringBuilder

class NumerationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var nBinding: ActivityNumerationBinding? = null
    private var originNumeration = ""
    private var afterNumeration = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nBinding = ActivityNumerationBinding.inflate(layoutInflater)
        setContentView(nBinding!!.root)

        //要换算的进制
        val originSpinner: Spinner = findViewById(R.id.origin_numeration)
        ArrayAdapter.createFromResource(
            this,
            R.array.numeration,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            originSpinner.adapter = adapter
        }
        originSpinner.onItemSelectedListener = this

        //换算后的进制
        val afterSpinner: Spinner = findViewById(R.id.after_numeration)
        ArrayAdapter.createFromResource(
            this,
            R.array.numeration,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            afterSpinner.adapter = adapter
        }
        afterSpinner.onItemSelectedListener = this

        nBinding!!.matrixing.setOnClickListener {
            var originNum = ""
            var originStr = nBinding!!.originNumerationNum.text.toString()
            var afterStr = ""
            when (originNumeration) {
                "二进制" -> {
                    originNum = decimal(originStr, 1)
                    println(originNum)
                }

                "八进制" -> {
                    originNum = decimal(originStr, 2)
                }

                "十进制" -> {
                    originNum = decimal(originStr, 3)
                }

                "十六进制" -> {
                    originNum = decimal(originStr, 4)
                }
            }

            when (afterNumeration) {
                "二进制" -> {
                    afterStr = binary(originNum)
                }

                "八进制" -> {
                    afterStr = octal(originNum)
                }

                "十进制" -> {
                    afterStr = originNum
                }

                "十六进制" -> {
                    afterStr = hexadecimal(originNum)
                    println(afterStr)
                }
            }
            nBinding!!.textViewAfterNumeration.text = afterStr
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0!!.id == R.id.origin_numeration) {
            originNumeration = p0.getItemAtPosition(p2).toString()
            println(originNumeration)
        } else if (p0!!.id == R.id.after_numeration) {
            afterNumeration = p0.getItemAtPosition(p2).toString()
            println(afterNumeration)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        return
    }

    //1: 二进制
    //2：八进制
    //3：十进制
    //4：十六进制
    private fun decimal(p0: String, p1: Int): String {
        var decimalNumber = 0

        when (p1) {
            1 -> {
                var num = p0.toInt()
                var i = 0
                var remainder: Int
                while (num != 0) {
                    remainder = num % 10
                    num /= 10
                    decimalNumber += (remainder * Math.pow(2.0, i.toDouble())).toInt()
                    ++i
                }
                return decimalNumber.toString()
            }

            2 -> {
                var num = p0.toInt()
                var i = 0
                while (num != 0) {
                    decimalNumber += (num % 10 * Math.pow(8.0, i.toDouble())).toInt()
                    ++i
                    num /= 10
                }
                println(decimalNumber)
                return decimalNumber.toString()
            }

            3 -> {
                var num = p0.toInt()
                return num.toString()
            }

            4 -> {
                val highLetter = charArrayOf('A', 'B', 'C', 'D', 'E', 'F')
                val map = mutableMapOf<String, Int>()
                for (index in 0 .. 9) {
                    map[index.toString()] = index
                }

                for (index in 10 until highLetter.size + 10) {
                    map[highLetter[index - 10].toString()] = index
                }

                var str = arrayOfNulls<String>(p0.length)

                for (index in str.indices) {
                    str[index] = p0.substring(index, index + 1)
                }

                for (index in str.indices) {
                    decimalNumber += (map.get(str[index])?.times(Math.pow(16.0, (str.size - 1 - index).toDouble())))!!.toInt()
                }

                return decimalNumber.toString()
            }
        }
        return decimalNumber.toString()
    }

    private fun binary(p0: String): String {
        var num = p0.toInt()
        val binaryStr = StringBuilder()
        while (num > 0) {
            val r = num % 2
            num /= 2
            binaryStr.append(r)
        }
        return binaryStr.reverse().toString()
    }

    private fun octal(p0: String): String {
        var num = p0.toInt()
        var octalNumber = 0
        var i = 1

        while (num > 0) {
            val r = num % 8
            octalNumber += r * i
            num /= 8
            i *= 10
        }

        return octalNumber.toString()
    }

    private fun hexadecimal(p0: String): String {
        var num = p0.toInt()
        return Integer.toHexString(num)
    }
}