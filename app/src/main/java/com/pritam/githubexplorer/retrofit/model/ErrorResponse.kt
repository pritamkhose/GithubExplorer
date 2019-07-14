package com.pritam.githubexplorer.retrofit.model

data class ErrorResponse(
    val documentation_url: String,
    val errors: List<Error>,
    val message: String
)

data class Error(
    val `field`: String,
    val code: String,
    val resource: String
)