package com.example.calculator

import com.example.calculator.service.CalculatorService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CalculatorApplicationTests {

	private val calculatorService = CalculatorService()

	@Test
	fun `tokenize simple expression`() {
		val input = "3+4*2"
		val expected = listOf("3", "+", "4", "*", "2")
		val actual = calculatorService.tokenize(input)
		assertEquals(expected, actual)
	}

	@Test
	fun `tokenize with parentheses`() {
		val input = "(2 + 3) * 4"
		val expected = listOf("(", "2", "+", "3", ")", "*", "4")
		val actual = calculatorService.tokenize(input)
		assertEquals(expected, actual)
	}

	@Test
	fun `tokenize with unary minus and parentheses`() {
		val input = "-(1 + 2)"
		val expected = listOf("0", "-", "(", "1", "+", "2", ")")
		val actual = calculatorService.tokenize(input)
		assertEquals(expected, actual)
	}

	@Test
	fun `tokenize negative numbers with and without parentheses`() {
		val input = "-1 + (-2)"
		val expected = listOf("0", "-", "1", "+", "(", "0", "-", "2", ")")
		val actual = calculatorService.tokenize(input)
		assertEquals(expected, actual)
	}

	@Test
	fun `tokenize decimals`() {
		val input = "3.5 + 0.25"
		val expected = listOf("3.5", "+", "0.25")
		val actual = calculatorService.tokenize(input)
		assertEquals(expected, actual)
	}

	@Test
	fun `tokenize throws on equal sign`() {
		val exception = assertThrows(IllegalArgumentException::class.java) {
			calculatorService.tokenize("3+4=")
		}
		assertEquals("Don't use '=' in the expression", exception.message)
	}

	@Test
	fun `tokenize throws on invalid character`() {
		val exception = assertThrows(IllegalArgumentException::class.java) {
			calculatorService.tokenize("2a+d2")
		}
		assertEquals("Invalid character: a", exception.message)
	}


	@Test
	fun `infixToPostfix basic expression`() {
		val infix = listOf("3", "+", "4", "*", "2")
		val expected = listOf("3", "4", "2", "*", "+")
		val actual = calculatorService.infixToPostfix(infix)
		assertEquals(expected, actual)
	}

	@Test
	fun `infixToPostfix with parentheses`() {
		val infix = listOf("(", "2", "+", "3", ")", "*", "4")
		val expected = listOf("2", "3", "+", "4", "*")
		val actual = calculatorService.infixToPostfix(infix)
		assertEquals(expected, actual)
	}

	@Test
	fun `infixToPostfix with unary minus and nested parentheses`() {
		val infix = calculatorService.tokenize("-(1 + (-2))")
		val expected = listOf("0", "1", "0", "2", "-", "+", "-")
		val actual = calculatorService.infixToPostfix(infix)
		assertEquals(expected, actual)
	}

	@Test
	fun `infixToPostfix throws on mismatched parentheses`() {
		val infix = listOf("(", "2", "+", "3", "*", "4")
		val exception = assertThrows(IllegalArgumentException::class.java) {
			calculatorService.infixToPostfix(infix)
		}
		assertEquals("Mismatched parentheses", exception.message)
	}

	@Test
	fun `infixToPostfix throws invalid token`() {
		val infix = calculatorService.tokenize("33..2/5+1-(-1-1)")
		val exception = assertThrows(IllegalArgumentException::class.java) {
			calculatorService.infixToPostfix(infix)
		}
		assertEquals("Invalid token: 33..2", exception.message)
	}


	@Test
	fun `evaluatePostfix basic addition and multiplication`() {
		val postfix = listOf("2", "3", "+", "4", "*")
		val result = calculatorService.evaluatePostfix(postfix)
		assertEquals(20.0, result)
	}

	@Test
	fun `evaluatePostfix with division`() {
		val postfix = listOf("8", "4", "/")
		val result = calculatorService.evaluatePostfix(postfix)
		assertEquals(2.0, result)
	}

	@Test
	fun `evaluatePostfix with negative result`() {
		val postfix = listOf("1", "2", "-")
		val result = calculatorService.evaluatePostfix(postfix)
		assertEquals(-1.0, result)
	}

	@Test
	fun `evaluatePostfix with unary minus expression`() {
		val tokens = calculatorService.tokenize("-1 - (-1)")
		val postfix = calculatorService.infixToPostfix(tokens)
		val result = calculatorService.evaluatePostfix(postfix)
		assertEquals(0.0, result)
	}

	@Test
	fun `evaluatePostfix with nested minus`() {
		val tokens = calculatorService.tokenize("-(1 + 2)")
		val postfix = calculatorService.infixToPostfix(tokens)
		val result = calculatorService.evaluatePostfix(postfix)
		assertEquals(-3.0, result)
	}

	@Test
	fun `evaluatePostfix with decimals`() {
		val postfix = listOf("2.5", "0.5", "+")
		val result = calculatorService.evaluatePostfix(postfix)
		assertEquals(3.0, result)
	}

	@Test
	fun `evaluatePostfix with complex expression`() {
		val tokens = calculatorService.tokenize("33.2/5+1-(-1-1)*2")
		val postfix = calculatorService.infixToPostfix(tokens)
		val result = calculatorService.evaluatePostfix(postfix)
		assertEquals(11.64, result)
	}

	@Test
	fun `evaluatePostfix throws on invalid token`() {
		val postfix = listOf("2", "x", "+")
		val exception = assertThrows(IllegalArgumentException::class.java) {
			calculatorService.evaluatePostfix(postfix)
		}
		assertEquals("Invalid token: x", exception.message)
	}

	@Test
	fun `evaluatePostfix throws on invalid postfix expression`() {
		val postfix = listOf("2", "3", "+", "5")
		val exception = assertThrows(IllegalStateException::class.java) {
			calculatorService.evaluatePostfix(postfix)
		}
		assertEquals("Invalid postfix expression", exception.message)
	}

	@Test
	fun `evaluatePostfix throws on zero division`() {
		val postfix = listOf("5", "0", "/")
		val exception = assertThrows(ArithmeticException::class.java) {
			calculatorService.evaluatePostfix(postfix)
		}
		assertEquals("Zero division is not allowed", exception.message)
	}

	@Test
	fun `evaluatePostfix throws when not enough operands`() {
		val postfix = listOf("+")
		val exception = assertThrows(IllegalStateException::class.java) {
			calculatorService.evaluatePostfix(postfix)
		}
		assertEquals("Invalid postfix expression", exception.message)
	}
}