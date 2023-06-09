package com.example.monarch.repository.dataClass.Experiments

data class Experiments(
    val idExperiment: Long,
    val name: String,
    val dateStart: String,
    val dateEnd: String,
    val timeLimit: Int,
)
