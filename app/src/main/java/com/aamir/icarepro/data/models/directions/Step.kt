package com.codebew.deliveryagent.data.models.directions

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: StartLocation,
    val html_instructions: String,
    val maneuver: String,
    val polyline: Polyline,
    val start_location: StartLocation,
    val travel_mode: String
)