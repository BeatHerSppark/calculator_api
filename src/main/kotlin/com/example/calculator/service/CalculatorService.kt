package com.example.calculator.service

import org.springframework.stereotype.Service
import java.util.Stack

@Service
class CalculatorService {
    fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0

        while (i < expr.length) {
            val ch = expr[i]
            when {
                ch.isWhitespace() -> i++

                ch == '=' -> throw IllegalArgumentException("Don't use '=' in the expression")

                ch in "+*/()" -> {
                    tokens.add(ch.toString())
                    i++
                }

                ch == '-' -> {
                    if (tokens.isEmpty() || tokens.last() in "+-*/(") {
                        tokens.add("0")
                    }
                    tokens.add("-")
                    i++
                }

                ch.isDigit() || ch == '.' -> {
                    val start = i
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        i++
                    }
                    tokens.add(expr.substring(start, i))
                }

                else -> throw IllegalArgumentException("Invalid character: $ch")
            }
        }

        return tokens
    }

    fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()
        val operatorPriority = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token in "+-*/" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(" &&
                        operatorPriority[stack.peek()]!! >= operatorPriority[token]!!
                    ) {
                        output.add(stack.pop())
                    }
                    stack.push(token)
                }
                token == "(" -> stack.push(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        output.add(stack.pop())
                    }
                    if (stack.isEmpty() || stack.pop() != "(") {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }
                }
                else -> throw IllegalArgumentException("Invalid token: $token")
            }
        }

        while (stack.isNotEmpty()) {
            val op = stack.pop()
            if (op == "(") throw IllegalArgumentException("Mismatched parentheses")
            output.add(op)
        }

        return output
    }

    fun evaluatePostfix(postfix: List<String>): Double {
        val stack = Stack<Double>()

        for (token in postfix) {
            when {
                token.toDoubleOrNull() != null -> stack.push(token.toDouble())
                token in "+-*/" -> {
                    if (stack.size < 2) throw IllegalStateException("Invalid postfix expression")
                    val b = stack.pop()
                    val a = stack.pop()
                    val result = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        else -> if (b != 0.0) a / b else throw ArithmeticException("Zero division is not allowed")
                    }
                    stack.push(result)
                }
                else -> throw IllegalArgumentException("Invalid token: $token")
            }
        }

        if (stack.size != 1) throw IllegalStateException("Invalid postfix expression")

        return stack.pop()
    }
}