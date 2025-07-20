package com.example.calculator.api

import com.example.calculator.service.CalculatorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CalculatorController(val calculatorService: CalculatorService) {

    @PostMapping("/calculate")
    fun calculate(@RequestBody body: CalculateRequest): ResponseEntity<CalculatorResponse> {
        try {
            val tokens = calculatorService.tokenize(body.expression)
            val postfix = calculatorService.infixToPostfix(tokens)
            val result = calculatorService.evaluatePostfix(postfix)

            return ResponseEntity.ok(CalculatorResponse(result = result))
        } catch (e: RuntimeException) {
            return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(CalculatorResponse(error = e.message ?: "Invalid expression!"))
        }

    }
}