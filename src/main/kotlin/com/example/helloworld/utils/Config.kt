package com.example.helloworld.utils

data class Rule(
        val name: String,
        val cost: Int,
        val parameters: String? = null // `parameters` é opcional
)

data class Config(
        val limit: Int,
        val rules: List<Rule>
)