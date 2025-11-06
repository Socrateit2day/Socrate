package com.example.expertpreste

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.expertpreste.Api.Prestataire

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherExpertsScreen(
    navController: NavController,
    experts: List<Prestataire>
) {
    var searchText by remember { mutableStateOf("") }
    var showFilterPanel by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<String?>(null) }
    var selectedCompetence by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }

    fun resetAllFilters() {
        selectedService = null
        selectedCompetence = null
        selectedType = null
    }

    val allServices = remember { experts.mapNotNull { it.jobcategorie?.nom }.distinct() }
    val allTypes = remember { experts.map { it.recipient_type }.distinct() }
    val competencesForSelectedService = remember(selectedService, experts) {
        if (selectedService == null) emptyList()
        else experts
            .filter { it.jobcategorie?.nom == selectedService }
            .flatMap { it.prestataire_besoins }
            .map { it.besoin_nom }
            .distinct()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tous les experts", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F9FC),
                    titleContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FC))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // --- Barre de recherche ---
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Rechercher un expert ou service") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Boutons "Tout" et "Filtre" ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        resetAllFilters()
                        showFilterPanel = false
                        searchText = ""
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Tout", color = Color.Black) }

                Button(
                    onClick = {
                        showFilterPanel = !showFilterPanel
                        if (showFilterPanel) resetAllFilters()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showFilterPanel) Color(0xFFB0BEC5) else Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtre", tint = Color.Black)
                    Spacer(Modifier.width(4.dp))
                    Text("Filtre", color = Color.Black)
                }
            }

            // --- Panneau des filtres ---
            AnimatedVisibility(
                visible = showFilterPanel,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .padding(vertical = 8.dp)
                ) {
                    when {
                        selectedService == null -> {
                            FilterColumn(
                                title = "Choisir un service",
                                items = allServices,
                                selectedItem = null,
                                onItemClick = { selectedService = it },
                                onBack = { showFilterPanel = false }
                            )
                        }
                        selectedCompetence == null -> {
                            if (competencesForSelectedService.isNotEmpty()) {
                                FilterColumn(
                                    title = "Compétence pour $selectedService",
                                    items = competencesForSelectedService,
                                    selectedItem = null,
                                    onItemClick = { selectedCompetence = it },
                                    onBack = { selectedService = null }
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text("Aucune compétence disponible.", color = Color.Gray)
                                    TextButton(onClick = { selectedService = null }) { Text("Retour") }
                                }
                            }
                        }
                        selectedType == null -> {
                            FilterColumn(
                                title = "Type d’expert",
                                items = allTypes,
                                selectedItem = null,
                                onItemClick = { selectedType = it },
                                onBack = { selectedCompetence = null }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Liste filtrée ---
            val filteredExperts = experts.filter { expert ->
                val matchesSearch =
                    expert.user.first_name.contains(searchText, true) ||
                            expert.user.last_name.contains(searchText, true) ||
                            (expert.jobcategorie?.nom?.contains(searchText, true) ?: false)
                val matchesService = selectedService == null || expert.jobcategorie?.nom == selectedService
                val matchesCompetence = selectedCompetence == null ||
                        expert.prestataire_besoins.any { it.besoin_nom == selectedCompetence }
                val matchesType = selectedType == null || expert.recipient_type == selectedType

                matchesSearch && matchesService && matchesCompetence && matchesType
            }

            if (filteredExperts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun expert ne correspond à ces critères. \n Essayez d’élargir votre recherche.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredExperts) { expert ->
                        ExpertItem(expert, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterColumn(
    title: String,
    items: List<String>,
    selectedItem: String?,
    onItemClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 220.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color(0xFF26A69A),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onBack() }
                )
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(Modifier.padding(horizontal = 4.dp)) {
                items.forEach { item -> FilterColumnItem(item, item == selectedItem) { onItemClick(item) } }
            }
        }
    }
}

@Composable
fun FilterColumnItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFE0F2F1) else Color.Transparent
    val contentColor = if (isSelected) Color(0xFF00796B) else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .background(backgroundColor)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, color = contentColor, fontSize = 14.sp)
        Icon(
            Icons.Default.ChevronRight,
            null,
            tint = contentColor.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun ExpertItem(expert: Prestataire, navController: NavController) {
    val buttonColor = Color(0xFF26A69A)
    val rating = (expert.is_visited % 5).toInt()
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = expert.photo ?: "https://cdn-icons-png.flaticon.com/512/3135/3135715.png",
                    contentDescription = "${expert.user.first_name} ${expert.user.last_name}",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("${expert.user.first_name} ${expert.user.last_name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Work, null, tint = Color(0xFF26A69A), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(expert.jobcategorie?.nom ?: "Non spécifié", fontSize = 15.sp, color = Color.Gray)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { indexStar ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (indexStar < rating) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(if (rating == 0) "Non noté" else "$rating.0 / 5", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { navController.navigate("expert_detail_id/${expert.user.id}") },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = buttonColor),
                    border = BorderStroke(1.dp, buttonColor),
                    modifier = Modifier.width(120.dp)
                ) {
                    Icon(Icons.Filled.Visibility, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Détail", fontSize = 14.sp)
                }

                Spacer(Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { showDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = buttonColor),
                    border = BorderStroke(1.dp, buttonColor),
                    modifier = Modifier.width(120.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Sélectionner", fontSize = 14.sp)
                }
            }
        }

        if (showDialog) {
            DemandeServiceDialog(
                expert = expert,
                onDismiss = { showDialog = false },
                onConfirm = { showDialog = false }
            )
        }
    }
}

@Composable
fun DemandeServiceDialog(
    expert: Prestataire,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var commentaire by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = expert.photo ?: "https://cdn-icons-png.flaticon.com/512/3135/3135715.png",
                    contentDescription = "${expert.user.first_name} ${expert.user.last_name}",
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("${expert.user.first_name} ${expert.user.last_name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = expert.jobcategorie?.nom ?: "Non spécifié", fontSize = 15.sp, color = Color.Gray)
                    Text(expert.recipient_type, fontSize = 14.sp, color = Color.Gray)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Service demandé : ${expert.prestataire_besoins.joinToString { it.besoin_nom }}",
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF26A69A),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = commentaire,
                    onValueChange = { commentaire = it; showError = false },
                    label = { Text("Votre commentaire *") },
                    placeholder = { Text("Ajoutez un message pour cet expert") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 4
                )
                if (showError) {
                    Spacer(Modifier.height(6.dp))
                    Text("⚠️ Le commentaire est obligatoire.", color = Color.Red, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (commentaire.isNotBlank()) onConfirm() else showError = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Demander") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", color = Color.Gray) }
        }
    )
}
