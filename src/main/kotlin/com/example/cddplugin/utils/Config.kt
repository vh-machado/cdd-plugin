package com.example.cddplugin.utils

import kotlinx.serialization.Serializable

@Serializable
data class Rule(
        val name: String,
        val cost: Int,
        val parameters: String? = null
)

@Serializable
data class Config(
        val limit: Int,
        val rules: List<Rule>
)