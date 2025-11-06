package com.example.expertpreste.Api
data class ExpertResponse(
    val total: Int,
    val results: List<Prestataire>
)
data class Prestataire(
    val user: User,
    val recipient_type: String,
    val personal_description: String?,
    val photo: String?,
    val jobcategorie: JobCategorie?,
    val prestataire_besoins: List<PrestataireBesoin>,
    val charte: Charte,
    val tarif: String?,
    val is_visited: Long
)
data class User(
    val id: Int,
    val email: String,
    val first_name: String,
    val last_name: String,
    val language: String,
    val phone: String,
    val identity_type: String,
    val passport_number: String?,
    val cni_number: String?,
    val permis_number: String?,
    val province: Int?,
    val province_name: String?,
    val commune: Int?,
    val commune_name: String?,
    val zone: Int?,
    val zone_name: String?,
    val colline: Int?,
    val colline_name: String?,
    val province_loc: Int?,
    val commune_loc: Int?,
    val zone_loc: Int?,
    val colline_loc: Int?,
    val nif: String?,
    val rc: String?,
    val is_active: Boolean,
    val is_staff: Boolean
)
data class JobCategorie(
    val id: Int,
    val nom: String,
    val icone: String,
    val created_at: String
)
data class PrestataireBesoin(
    val besoin_nom: String,
    val description: String
)
data class Charte(
    val communication: Boolean,
    val pasEchangeInfo: Boolean,
    val paiement: Boolean
)
