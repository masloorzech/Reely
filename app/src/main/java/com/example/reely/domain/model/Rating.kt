package com.example.reely.domain.model

@JvmInline
value class Rating(
    val value: Int,
){
    init {
        require(value in MIN_VALUE..MAX_VALUE){
            "Rating must be between $MIN_VALUE and $MAX_VALUE"
        }
    }
    companion object {
        const val MIN_VALUE = 1
        const val MAX_VALUE = 10
    }
}