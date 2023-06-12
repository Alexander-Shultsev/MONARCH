package com.example.monarch.model.Experiments.ExperimentsData

data class Experiments(
    val idExperiment: Long,
    val name: String,
    val dateStart: String,
    val dateEnd: String,
    val timeLimit: Int,
)
