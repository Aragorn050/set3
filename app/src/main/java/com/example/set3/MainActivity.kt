package com.example.set3

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

import com.example.set3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var canAddOperation = false
    private var canAddDecimal = true
    private var isFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            // If a calculation just finished, start fresh
            if (isFinished) {
                binding.workingTV.text = ""
                binding.resultsTV.text = ""
                isFinished = false // Reset the flag
                canAddDecimal = true
            }

            if (view.text == ".") {
                if (canAddDecimal) {
                    binding.workingTV.append(view.text)
                    canAddDecimal = false
                }
            } else {
                binding.workingTV.append(view.text)
            }
            canAddOperation = true
        }
    }

    fun operatorAction(view: View) {
        if (view is Button) {
            if (isFinished) {
                val lastResult = binding.resultsTV.text.toString()
                if (lastResult.isNotEmpty() && lastResult != "Error") {
                    binding.workingTV.text = lastResult
                    binding.resultsTV.text = ""
                }
                isFinished = false
            }
            val operator = view.text.toString()
            val currentWorkings = binding.workingTV.text.toString()

            if (currentWorkings.isNotEmpty() && (currentWorkings.last().isDigit())) {
                binding.workingTV.append(operator)
                canAddDecimal = true
                canAddOperation = false
            }
        }
    }

    fun allClearAction(view: View) {
        binding.workingTV.text = ""
        binding.resultsTV.text = ""
        canAddOperation = false
        canAddDecimal = true
    }

    fun backSpaceAction(view: View) {
        val length = binding.workingTV.length()
        if (length > 0) {
            binding.workingTV.text = binding.workingTV.text.subSequence(0, length - 1)
        }
    }

    fun equalsAction(view: View) {
        val result = calculateResults()
        binding.resultsTV.text = result
        isFinished = true
    }

    private fun calculateResults(): String {
        val workings = binding.workingTV.text.toString()
        if (workings.isEmpty()) return ""

        try {

            val numberStrings = workings.split(Regex("[x/+-]|num")).filter { it.isNotEmpty() }

            val operators = mutableListOf<String>()
            val matcher = java.util.regex.Pattern.compile("[x/+-]|num").matcher(workings)
            while (matcher.find()) {
                operators.add(matcher.group())
            }

            if (numberStrings.isEmpty()) return ""

            var result = numberStrings[0].toDouble()

            for (i in operators.indices) {
                val nextNumber = numberStrings[i + 1].toDouble()
                val operator = operators[i]

                result = when (operator) {
                    "+" -> result + nextNumber
                    "-" -> result - nextNumber
                    "x" -> result * nextNumber
                    "/" -> {
                        if (nextNumber == 0.0) return "Error"
                        result / nextNumber
                    }

                    "num" -> Math.pow(result, nextNumber)
                    else -> result
                }
            }

            return formatResult(result)

        } catch (e: Exception) {
            return "Error"
        }
    }

    private fun formatResult(result: Double): String {
        return if (result % 1 == 0.0) {
            result.toLong().toString()
        } else {
            val df = java.text.DecimalFormat("0.########")
            df.format(result)
        }
    }

}