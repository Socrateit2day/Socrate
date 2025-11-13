package com.example.expertpreste.Api

data class ExpertResponse(
    val total: Int,
    val results: List<Prestataire>
)
// data class pour GET
data class Prestataire(
    val user: User,
    val recipient_type: String,
    val personal_description: String?,
    val photo: String?,
    val jobcategorie: JobCategorie?,
    val prestataire_besoins: List<PrestataireBesoin>,
    val charte: Charte,
    val tarif: String?,
    val is_visited: Long,
    val login: LoginResponse
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
data class LoginResponse(
    val email: String,
    val password: String,
    val remember: Boolean
)
//FIN Data class GET

//Data class pour le demande de service pour un demandeur une fois on lui selectionne POST
data class DemandeService(
    val id: Int = 0,
    val user_id: Int = 0,
    val titre: String,
    val description: String,
    val categorie_detail: String,
    val besoin_detail: String,
    val type_besoin_detail: TypeBesoinDetail,
    val recipient_type: String, // Exemple : "Particulier" ou "Entreprise"
    val date_debut: String,
    val date_fin: String,
    val province_nom: String,
    val commune_nom: String,
    val zone_nom: String,
    val colline_nom: String,
    val photo1: String? = null,
    val photo2: String? = null,
    val photo3: String? = null,
    val province_loc: Int,
    val commune_loc: Int,
    val zone_loc: Int,
    val colline_loc: Int,
    val heure_debut: String,
    val heure_fin: String,
    val expert_cible_id: Int?,
)

data class CategorieDetail(
    val id: Int,
    val nom: String,
    val icone: String,
    val created_at: String
)

data class BesoinDetail(
    val id: Int,
    val nom: String
)

data class TypeBesoinDetail(
    val id: Int,
    val nom: String
)

//FIN Data class POST

// Code de data class pour le Lieu de prestation

data class ProvinceResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Province>
)

data class Province(
    val id: Int,
    val nom: String,
    val communes: List<Commune>
)

data class Commune(
    val id: Int,
    val nom: String,
    val province_id: Int,
    val zones: List<Zone>
)

data class Zone(
    val id: Int,
    val nom: String,
    val commune_id: Int,
    val collines: List<Colline>
)

data class Colline(
    val id: Int,
    val nom: String,
    val zone_id: Int
)
//FIN Data class Lieu de prestation

