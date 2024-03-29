package com.example.calculator

import android.os.Bundle
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import viewModel.CalculatorViewModel


class MainActivity : ComponentActivity() {
    private var resultField: TextView? = null
    private var operationField: TextView? = null
    private var viewModel: CalculatorViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultField = findViewById(R.id.resultField)
        operationField = findViewById(R.id.operationField)

        setupListeners();
    }

    private fun setupListeners(){
        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]
        viewModel!!.operation.observe(this) { operation -> operationField!!.text = operation }
        viewModel!!.result.observe(this) { result -> resultField!!.text = result }

        val gridLayout = findViewById<GridLayout>(R.id.buttonGrid)
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i)
            if (button is AppCompatButton) {
                button.setOnClickListener { onButtonClick(button) }
            }
        }
    }

    private fun onButtonClick(button: AppCompatButton) {
        val text = button.getText().toString()
        if (operationField?.length() == 30) {
            when (text) {
                "C" -> viewModel!!.Clear()
                "" -> viewModel!!.RemoveLast()
                "=" -> viewModel!!.Evaluate()
            }
            return
        }

        when (text) {
            "sqrt", "ln", "log2", "sin", "cos", "tan" -> viewModel!!.Func(text)
            "( )" -> viewModel!!.Brackets()
            "x^y", "e^x" -> viewModel!!.Pow(text)
            "C" -> viewModel!!.Clear()
            "" -> viewModel!!.RemoveLast()
            "=" -> viewModel!!.Evaluate()
            "." -> viewModel!!.Point()
            "+/-" -> viewModel!!.Invert()
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"  -> viewModel!!.Digit(text)
            "e", "pi" -> viewModel!!.Constants(text)
            else -> viewModel!!.Append(text)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("result", resultField!!.getText().toString())
        outState.putString("operation", operationField!!.getText().toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val result = savedInstanceState.getString("result")
        val operation = savedInstanceState.getString("operation")
        resultField!!.text = result
        operationField!!.text = operation
    }
}
