package model

import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal

class Calculator {
    fun Evaluate(expression: String?): BigDecimal {
        return ExpressionBuilder(expression).build().evaluate().toBigDecimal()
    }
}