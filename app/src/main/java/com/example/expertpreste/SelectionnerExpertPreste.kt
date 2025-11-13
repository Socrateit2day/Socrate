package com.example.expertpreste

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CancelScheduleSend
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expertpreste.Api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionnerExpertPreste(
    navController: NavController,
    expert: Prestataire,
    allExperts: List<Prestataire>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- États du formulaire ---
    var titre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateDebut by remember { mutableStateOf("") }
    var dateFin by remember { mutableStateOf("") }
    var heureDebut by remember { mutableStateOf("") }
    var heureFin by remember { mutableStateOf("") }
    var competence by remember { mutableStateOf<String?>(null) }

    // --- Pré-remplissage automatique ---
    var categorieService by remember { mutableStateOf(expert.jobcategorie?.nom ?: "") }
    var typePrestation by remember { mutableStateOf(expert.recipient_type) }

    // --- États pour la hiérarchie des lieux ---
    var provinces by remember { mutableStateOf<List<Province>>(emptyList()) }
    var selectedProvince by remember { mutableStateOf<Province?>(null) }

    var selectedCommune by remember { mutableStateOf<Commune?>(null) }
    var selectedZone by remember { mutableStateOf<Zone?>(null) }
    var selectedColline by remember { mutableStateOf<Colline?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // --- Chargement des provinces depuis l’API ---
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getProvinces() // suspend fun
            provinces = response.results
        } catch (e: Exception) {
            errorMessage = "Erreur de chargement des provinces : ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // --- Liste de compétences de l’expert ---
    val competencesExpert = expert.prestataire_besoins.map { it.besoin_nom }

    // --- Sélecteurs de date et heure ---
    val calendar = Calendar.getInstance()

    fun openDatePicker(onDateSelected: (String) -> Unit) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, { _, y, m, d ->
            onDateSelected("$d/${m + 1}/$y")
        }, year, month, day).show()
    }

    fun openTimePicker(onTimeSelected: (String) -> Unit) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        TimePickerDialog(context, { _, h, m ->
            onTimeSelected(String.format("%02d:%02d", h, m))
        }, hour, minute, true).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Demander un service",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE8F5E9))
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(errorMessage ?: "Erreur inconnue", color = Color.Red)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Informations Expert ---
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF7F9FC), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        InfoRow("Nom :", "${expert.user.first_name} ${expert.user.last_name}")
                        InfoRow("Catégorie :", expert.jobcategorie?.nom ?: "Non spécifiée")
                        InfoRow("Type :", expert.recipient_type)
                        InfoRow(
                            "Localité :",
                            "${expert.user.province_name}/${expert.user.commune_name}/${expert.user.zone_name}/${expert.user.colline_name}"
                        )
                    }
                }

                // --- Catégorie et type ---
                item {
                    OutlinedTextField(
                        value = categorieService,
                        onValueChange = { categorieService = it },
                        label = { Text("Catégorie de service *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        readOnly = true
                    )
                }

                item {
                    DropdownField(
                        label = "Compétence *",
                        options = competencesExpert,
                        selectedOption = competence,
                        onOptionSelected = { competence = it }
                    )
                }

                item {
                    OutlinedTextField(
                        value = typePrestation,
                        onValueChange = { typePrestation = it },
                        label = { Text("Type *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        readOnly = true
                    )
                }

                // --- Titre ---
                item {
                    OutlinedTextField(
                        value = titre,
                        onValueChange = { titre = it },
                        label = { Text("Titre de la demande *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // --- Description ---
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= 750) description = it },
                        label = { Text("Description du besoin *") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        supportingText = {
                            Text(
                                "${750 - description.length} caractères restants",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    )
                }

                // --- Dates ---
                item {
                    Text("Dates de la prestation", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dateDebut,
                            onValueChange = {},
                            label = { Text("Date de début *") },
                            trailingIcon = {
                                IconButton(onClick = { openDatePicker { dateDebut = it } }) {
                                    Icon(Icons.Default.CalendarToday, null)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            readOnly = true
                        )
                        OutlinedTextField(
                            value = dateFin,
                            onValueChange = {},
                            label = { Text("Date de fin *") },
                            trailingIcon = {
                                IconButton(onClick = { openDatePicker { dateFin = it } }) {
                                    Icon(Icons.Default.CalendarToday, null)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            readOnly = true
                        )
                    }
                }

                // --- Heures ---
                item {
                    Text("Heures de la prestation", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = heureDebut,
                            onValueChange = {},
                            label = { Text("Heure début *") },
                            trailingIcon = {
                                IconButton(onClick = { openTimePicker { heureDebut = it } }) {
                                    Icon(Icons.Default.CalendarToday, null)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            readOnly = true
                        )
                        OutlinedTextField(
                            value = heureFin,
                            onValueChange = {},
                            label = { Text("Heure fin *") },
                            trailingIcon = {
                                IconButton(onClick = { openTimePicker { heureFin = it } }) {
                                    Icon(Icons.Default.CalendarToday, null)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            readOnly = true
                        )
                    }
                }

                // --- Sélection du lieu ---
                item { Text("Lieu de la prestation *", fontWeight = FontWeight.Medium) }

                item {
                    DropdownField(
                        label = "Province",
                        options = provinces.map { it.nom },
                        selectedOption = selectedProvince?.nom,
                        onOptionSelected = { nom ->
                            selectedProvince = provinces.find { it.nom == nom }
                            selectedCommune = null
                            selectedZone = null
                            selectedColline = null
                        }
                    )
                }

                val communes = selectedProvince?.communes ?: emptyList()
                item {
                    DropdownField(
                        label = "Commune",
                        options = communes.map { it.nom },
                        selectedOption = selectedCommune?.nom,
                        onOptionSelected = { nom ->
                            selectedCommune = communes.find { it.nom == nom }
                            selectedZone = null
                            selectedColline = null
                        },
                        enabled = communes.isNotEmpty()
                    )
                }

                val zones = selectedCommune?.zones ?: emptyList()
                item {
                    DropdownField(
                        label = "Zone",
                        options = zones.map { it.nom },
                        selectedOption = selectedZone?.nom,
                        onOptionSelected = { nom ->
                            selectedZone = zones.find { it.nom == nom }
                            selectedColline = null
                        },
                        enabled = zones.isNotEmpty()
                    )
                }

                val collines = selectedZone?.collines ?: emptyList()
                item {
                    DropdownField(
                        label = "Colline",
                        options = collines.map { it.nom },
                        selectedOption = selectedColline?.nom,
                        onOptionSelected = { nom ->
                            selectedColline = collines.find { it.nom == nom }
                        },
                        enabled = collines.isNotEmpty()
                    )
                }

                // --- Bouton d’envoi ---
                item {
                    // Vérifie si tous les champs obligatoires sont remplis
                    val isFormValid = titre.isNotBlank() &&
                            description.isNotBlank() &&
                            competence != null &&
                            selectedProvince != null &&
                            dateDebut.isNotBlank() &&
                            dateFin.isNotBlank() &&
                            heureDebut.isNotBlank() &&
                            heureFin.isNotBlank()

                    Button(
                        onClick = {
                            if (isFormValid) {
                                val demande = DemandeService(

                                    titre = titre,
                                    description = description,
                                    categorie_detail = categorieService!!, // ton CategorieDetail
                                    besoin_detail = competence!!,        // ton BesoinDetail
                                    type_besoin_detail = TypeBesoinDetail(
                                        1,
                                        "Besoin"
                                    ), // ton TypeBesoinDetail
                                    recipient_type = typePrestation!!,  // ou "Entreprise"
                                    date_debut = dateDebut,
                                    date_fin = dateFin,
                                    province_nom = selectedProvince!!.nom,
                                    commune_nom = selectedCommune!!.nom,
                                    zone_nom = selectedZone!!.nom,
                                    colline_nom = selectedColline!!.nom,
                                    photo1 = null, photo2 = null, photo3 = null,
                                    province_loc = selectedProvince!!.id,
                                    commune_loc = selectedCommune!!.id,
                                    zone_loc = selectedZone!!.id,
                                    colline_loc = selectedColline!!.id,
                                    heure_debut = heureDebut,
                                    heure_fin = heureFin,
                                    expert_cible_id = expert.user.id

                                )

                                // Lancer la coroutine
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val token = "Bearer ${expert.login.email}" // récupéré après login
                                        val response = RetrofitInstance.api.createDemande(token, demande)

                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Demande envoyée avec succès ✅", Toast.LENGTH_LONG).show()
                                                navController.popBackStack()
                                            } else {
                                                Toast.makeText(context, "Erreur serveur : ${response.code()}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Erreur réseau : ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                            } else {
                                Toast.makeText(context, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_LONG).show()
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF26A69A),
                            disabledContainerColor = Color(0xFFB2DFDB),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.CancelScheduleSend, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Envoyer la demande", fontSize = 16.sp)
                    }

                }

            }
        }
    }
}

// --- Composables Utilitaires ---
@Composable
fun InfoRow(label: String, value: String) {
    Row {
        Text(label, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.width(90.dp))
        Text(value, color = Color.Black)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(8.dp),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
