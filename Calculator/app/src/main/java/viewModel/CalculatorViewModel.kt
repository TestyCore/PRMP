package viewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Calculator
import net.objecthunter.exp4j.*

import net.objecthunter.exp4j.*
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {
    private val maxExpressionLength = 30
    var operation = MutableLiveData<String>()
    var result = MutableLiveData<String>()

    init {
        Clear()
    }

    fun Brackets() {
        val operationString = operation.getValue()

        if (!operationString.isNullOrEmpty()) {
            var index = operationString.length - 1
            while (index >= 0 && operationString[index] != '(' && operationString[index] != ')') {
                index--
            }

            val lastSymbol= operation.value?.lastOrNull().toString()
            if (lastSymbol.compareTo(".") == 0){
                return
            }
            if (lastSymbol.compareTo("(") == 0) {
                operation.setValue("$operationString(")
            }
            else if (lastSymbol.compareTo(")") == 0){
                val (opened, closed) = CountBrackets()

                if (opened > closed) {
                    operation.setValue("$operationString)")
                }
                else{
                    operation.setValue("$operationString*(")
                }
            }
            else if (index >= 0) {
                val (opened, closed) = CountBrackets();
                val mainOperations = listOf('+', '-', '/', '*', '%', '^')

                if (opened > closed && lastSymbol.toIntOrNull() in 0..9 || lastSymbol[0] == 'e' || lastSymbol[0] == 'i') {
                    operation.setValue("$operationString)")
                }
                else if (lastSymbol[0] in mainOperations) {
                    operation.setValue("$operationString(")
                }
                else if (operationString[index] == '(') {
                    if (lastSymbol.toIntOrNull() in 0..9) {
                        operation.setValue("$operationString)")
                    }

                }
                else if (operationString[index] == ')'){
                    if (lastSymbol.toIntOrNull() in 0..9 || lastSymbol.compareTo(")") == 0) {
                        operation.setValue("$operationString*(")
                    }
                    else{
                        operation.setValue("$operationString(")
                    }
                }
            } else {
                if (lastSymbol.toIntOrNull() in 0..9) {
                    operation.setValue("$operationString*(")
                }
                else{
                    operation.setValue("$operationString(")
                }
            }
        } else {
            operation.setValue("(");
        }


    }

    fun Point() {
        val operationString = operation.getValue().toString();
        val lastSymbol = operationString.lastOrNull()

        if (operationString.isNotEmpty()) {
            var pointIndex = operationString.length - 1

            while (pointIndex >= 0 && operationString[pointIndex] != '.') {
                pointIndex--
            }

            if (pointIndex >= 0) {
                var allowed = false // Is it correct to use a point symbol in an expression

                for (i in operationString.length - 1 downTo pointIndex) {
                    when (operationString[i]) {
                        '+', '-', '*', '/', '(', '^', '%' -> allowed = true
                    }
                }
                if (allowed) {
                    when(lastSymbol) {
                        '+', '-', '*', '/', '(', '^', '%' ->
                            operation.setValue("${operationString}0.")
                        'e', 'i' ->
                            operation.setValue(operationString)
                        else ->
                            operation.setValue("${operationString}.")
                    }
                }

            } else {
                when(lastSymbol) {
                    '+', '-', '*', '/', '(', '^', '%' ->
                        operation.setValue("${operationString}0.")
                    'e', 'i' ->
                        operation.setValue(operationString)
                    ')' ->
                        operation.setValue("${operationString}*0.")
                    else ->
                        operation.setValue("${operationString}.")
                }
            }
        } else {
            operation.setValue("${operationString}0.")
        }

    }

    fun Invert() {
        val operationString = operation.getValue().toString();
        val lastSymbol = operationString.lastOrNull()
        val specSymbols = listOf('+', '-', '*', '/', '(', '^')

        if (operationString.isEmpty()){
            operation.value = "(-"
        }
        else if (operationString.compareTo("(-") == 0) {
            operation.value = ""
        }
        else if (operationString.length >= 2 && operationString[operationString.length - 2] == '(' && lastSymbol == '-'){
            operation.setValue(operationString.substring(0, operationString.length - 2))
        }
        else if (lastSymbol in specSymbols) {
            operation.value = "${operationString}(-"
        }
        else if (lastSymbol == ')') {
            operation.value = "${operationString}*(-"
        }
        else if (lastSymbol.toString().toIntOrNull() in 0..9 || lastSymbol == 'e' || lastSymbol == 'i') {
            var index = operationString.length - 1

            while (index >= 0 && operationString[index] !in specSymbols) {
                index--
            }

            if (index >= 0) {
                if (operationString[index] == '-' && operationString[index - 1] == '(') {
                    operation.value = operationString.substring(0, index - 1) + operationString.substring(index + 1)
                }
                else {
                    operation.value = operationString.substring(0, index + 1) + "(-" + operationString.substring(index + 1)
                }
            }
            else {
                operation.value = "(-$operationString"
            }
        }

    }

    fun Digit(digit: String){
        val operationString = operation.getValue().toString();
        val lastSymbol = operationString.lastOrNull()

        val last12Characters = if (operationString.length >= 12) {
            operationString.substring(operationString.length - 12)
        } else {
            operationString
        }

        var allDigits = true
        for (char in last12Characters) {
            if (!char.isDigit()) {
                allDigits = false
                break
            }
        }

        if (last12Characters != "" && allDigits && last12Characters.length == 12) {
            return
        }

        if(lastSymbol == 'e' || lastSymbol == 'i' || lastSymbol == ')'){
            operation.value = "${operationString}*${digit}"
        }
        else{
            operation.value = "${operationString}${digit}"
        }
    }

    fun Func(func: String) {
        var function = func
        val operationString = operation.getValue().toString();
        val lastSymbol = operationString.lastOrNull()

        if (func.compareTo("ln") == 0) {
            function = "log"
        }

        if (lastSymbol.toString().compareTo(".") == 0) {
            return
        }
        else if (lastSymbol == 'e' || lastSymbol == 'i' || lastSymbol.toString().toIntOrNull() in 0..9 || lastSymbol == ')') {
            operation.value = "${operationString}*${function}("
        }
        else {
            operation.value = "${operationString}${function}("
        }

    }

    fun Pow(pow: String) {
        val operationString = operation.getValue().toString();
        val lastSymbol = operationString.lastOrNull()

        var degree = pow

        if (lastSymbol.toString().compareTo(".") == 0) {
            return
        }
        if (degree.compareTo("x^y") == 0) {
            degree = "^"

            if (lastSymbol == 'e' || lastSymbol == 'i' || lastSymbol.toString()
                    .toIntOrNull() in 0..9 || lastSymbol == ')'
            ) {
                operation.value = "${operationString}${degree}"
            }
        }

        if (degree.compareTo("e^x") == 0) {
            degree = "e^"

            if (lastSymbol == 'e' || lastSymbol == 'i' || lastSymbol.toString()
                    .toIntOrNull() in 0..9 || lastSymbol == ')'
            ) {
                operation.value = "${operationString}*${degree}"
            } else {
                operation.value = "${operationString}${degree}"
            }
        }
    }

    fun Constants(constant: String) {
        val operationString = operation.getValue().toString();
        val lastSymbol = operationString.lastOrNull()

        if (lastSymbol.toString().compareTo(".") == 0) {
            return
        }
        else if (lastSymbol == 'e' || lastSymbol == 'i' || lastSymbol.toString().toIntOrNull() in 0..9 || lastSymbol == ')') {
            operation.value = "${operationString}*${constant}"
        }
        else {
            operation.value = "${operationString}${constant}"
        }

    }

    fun Clear() {
        operation.value = ""
        result.value = ""
    }

    fun Append(value: String) {
        val lastSymbol= operation.value?.lastOrNull().toString()
        val specSymbols = listOf("+", "-", "*", "/", "%")
        if ((lastSymbol == "(" || operation.value == "") && value != "-") {
            return
        }
        if (lastSymbol == "." && value in specSymbols) {
            return
        }
        if (lastSymbol in specSymbols && value in specSymbols){
            return
        }
        val currentOperation = operation.getValue()
        operation.setValue(currentOperation + value)
    }

    fun RemoveLast() {
        val currentOperation = operation.getValue()
        if (!currentOperation!!.isEmpty()) {
            operation.setValue(currentOperation.substring(0, currentOperation.length - 1))
        }
    }

    fun Evaluate() {
        val currentOperation = operation.getValue()
        if (!currentOperation!!.isEmpty()) {
            try {
                val result_number = Calculator().Evaluate(currentOperation)
                val resultInteger = result_number.toBigInteger().toBigDecimal()

                if (result_number.compareTo(resultInteger) == 0) {
                    result.setValue(result_number.toBigInteger().toString())
                } else {
                    result.setValue(result_number.toString())
                }
            } catch (e: Exception) {
                result.setValue("Invalid input")
            }
        }
    }

    fun CountBrackets() : Pair<Int, Int> {
        var opened = 0
        var closed = 0
        val operationString: String = operation.getValue().toString()

        for (char in operationString) {
            if (char == '(') {
                opened++
            }
            if (char == ')') {
                closed++
            }
        }

        return Pair(opened, closed)
    }
}