package eam.edu.unieventos.model

public data class Cart (
    val id : String,
    val clientId: String,
    var items:MutableList<String> = mutableListOf()
)