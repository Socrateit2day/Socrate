package com.example.expertpreste

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherExpertsScreen(navController: NavController) {
    var experts by remember { mutableStateOf(expertList.filter { it.estDisponible }.map { it.copy() }) }
    var searchText by remember { mutableStateOf("") }
    var showFilter by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<String?>(null) }
    var selectedCategorieService by remember { mutableStateOf<String?>(null) }
    var selectedSousCategorie by remember { mutableStateOf<String?>(null) }
    var selectedProvince by remember { mutableStateOf<String?>(null) }
    var selectedCommune by remember { mutableStateOf<String?>(null) }
    var selectedZone by remember { mutableStateOf<String?>(null) }
    var selectedColline by remember { mutableStateOf<String?>(null) }
    var dateDebut by remember { mutableStateOf<String?>(null) }
    var dateFin by remember { mutableStateOf<String?>(null) }
    var heureDebut by remember { mutableStateOf<String?>(null) }
    var heureFin by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    fun resetAllFilters() {
        selectedService = null
        selectedCategorieService = null
        selectedSousCategorie = null
        selectedProvince = null
        selectedCommune = null
        selectedZone = null
        selectedColline = null
        dateDebut = null
        dateFin = null
        heureDebut = null
        heureFin = null
        description = ""
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
            // Barre de recherche
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Rechercher par nom ou travail") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        resetAllFilters()
                        showFilter = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("Tout", color = Color.Black) }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = {
                        showFilter = !showFilter
                        if (showFilter) {
                            resetAllFilters()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (showFilter) Color(0xFFB0BEC5) else Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtrer", tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Filtre", color = Color.Black)
                }
            }
            AnimatedVisibility(
                visible = showFilter,
                enter = androidx.compose.animation.expandVertically(animationSpec = tween(400)),
                exit = androidx.compose.animation.shrinkVertically(animationSpec = tween(400))
            ) {
                Column(
                    modifier = Modifier
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    val services = listOf("Plomberie", "Électricité", "Informatique", "Mécanicien", "Cuisinier", "Couturier")
                    AnimatedVisibility(visible = selectedService == null) {
                        FilterColumn(
                            title = "Services",
                            options = services,
                            selectedOption = null,
                            onOptionSelected = { service ->
                                selectedService = service
                            },
                            modifier = Modifier.width(IntrinsicSize.Max)
                        )
                    }

                    // --- Colonne 2: Sous categorie ---
                    val categories = when (selectedService) {
                        "Plomberie" -> listOf("Installation", "Réparation", "Fuites")
                        "Électricité" -> listOf("Domestique", "Industrielle", "Dépannage")
                        "Informatique" -> listOf("Maintenance", "Développement", "Réseau")
                        "Mécanicien" -> listOf("Auto", "Moto", "Diesel")
                        "Cuisinier" -> listOf("Chef", "Assistant", "Pâtissier")
                        "Couturier" -> listOf("Homme", "Femme", "Retouches")
                        else -> emptyList()
                    }

                    AnimatedVisibility(
                        visible = selectedService != null && selectedCategorieService == null && categories.isNotEmpty()
                    ) {
                        FilterColumn(
                            title = "Sous categorie",
                            options = categories,
                            selectedOption = null,
                            onOptionSelected = { categorie ->
                                selectedCategorieService = categorie
                            },
                            modifier = Modifier.width(IntrinsicSize.Max)
                        )
                    }

                    // --- Colonne 3: Type d'expert ---
                    val expertTypes = listOf("Entreprise", "Particulier")
                    AnimatedVisibility(
                        visible = selectedCategorieService != null && selectedSousCategorie == null
                    ) {
                        FilterColumn(
                            title = "Type d'expert",
                            options = expertTypes,
                            selectedOption = null,
                            onOptionSelected = { type ->
                                selectedSousCategorie = type
                            },
                            modifier = Modifier.width(IntrinsicSize.Max)
                        )
                    }
                    AnimatedVisibility(visible = selectedSousCategorie != null) {
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max)
                        ) {
                            Spacer(modifier = Modifier.height(24.dp))
                            val provinces = listOf("Bujumbura Mairie", "Gitega", "Muramvya", "Ngozi")
                            val communes = when(selectedProvince) {
                                "Bujumbura Mairie" -> listOf("Muha", "Mukaza", "Ntahangwa")
                                "Gitega" -> listOf("Gitega", "Bugendana", "Itaba")
                                else -> emptyList()
                            }
                            val zones = when(selectedCommune) {
                                "Muha" -> listOf("Kanyosha", "Musaga")
                                "Mukaza" -> listOf("Rohero", "Bwiza")
                                else -> emptyList()
                            }
                            val collines = when(selectedZone) {
                                "Kanyosha" -> listOf("Kanyosha 1", "Kanyosha 2")
                                "Rohero" -> listOf("Rohero 1", "Rohero 2")
                                else -> emptyList()
                            }
                            AnimatedVisibility(visible = selectedProvince == null) {
                                FilterColumn("Province", provinces, null, { selectedProvince = it })
                            }
                            AnimatedVisibility(visible = selectedProvince != null && selectedCommune == null && communes.isNotEmpty()) {
                                FilterColumn("Commune", communes, null, { selectedCommune = it })
                            }
                            AnimatedVisibility(visible = selectedCommune != null && selectedZone == null && zones.isNotEmpty()) {
                                FilterColumn("Zone", zones, null, { selectedZone = it })
                            }
                            AnimatedVisibility(visible = selectedZone != null && selectedColline == null && collines.isNotEmpty()) {
                                FilterColumn("Colline/Quartier", collines, null, { selectedColline = it })
                            }
                        }
                    }
                    AnimatedVisibility(visible = selectedProvince != null) {

                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ClickableTextField(
                                    value = dateDebut ?: "Date de début",
                                    icon = Icons.Default.CalendarToday,
                                    onClick = { /* TODO: Afficher un DatePickerDialog ici */ },
                                    modifier = Modifier.weight(1f)
                                )
                                ClickableTextField(
                                    value = dateFin ?: "Date de fin",
                                    icon = Icons.Default.CalendarToday,
                                    onClick = { /* TODO: Afficher un DatePickerDialog ici */ },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ClickableTextField(
                                    value = heureDebut ?: "Heure de début",
                                    icon = Icons.Default.Schedule,
                                    onClick = { /* TODO: Afficher un TimePickerDialog ici */ },
                                    modifier = Modifier.weight(1f)
                                )
                                ClickableTextField(
                                    value = heureFin ?: "Heure de fin",
                                    icon = Icons.Default.Schedule,
                                    onClick = { /* TODO: Afficher un TimePickerDialog ici */ },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    AnimatedVisibility(visible = selectedProvince != null) {
                        Column {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Description", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Précisez le lieu et le contexte...") },
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showFilter = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Appliquer les filtres", color = Color.White) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val filteredExperts = experts.filter { expert ->
                val matchService = selectedService == null || expert.job.equals(selectedService, ignoreCase = true)
                val matchCategorie = selectedCategorieService == null || expert.titre.equals(selectedCategorieService, ignoreCase = true)
                val matchSearch = searchText.isBlank() || expert.name.contains(searchText, true) || expert.job.contains(searchText, true)
                matchService && matchCategorie && matchSearch
            }

            if (filteredExperts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun expert ne correspond aux filtres.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredExperts) { expert ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { visible = true }

                        AnimatedVisibility(
                            visible = visible,
                            enter = androidx.compose.animation.fadeIn(animationSpec = tween(500)),
                            exit = androidx.compose.animation.fadeOut(animationSpec = tween(300))
                        ) {
                            ExpertCard(
                                expert = expert,
                                onClick = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set("expert", expert)
                                    navController.navigate("expert_detail_other")
                                },
                                onRatingChange = { newRating, newCount ->
                                    experts = experts.map {
                                        if (it.name == expert.name) it.copy(rating = newRating, nbRatings = newCount)
                                        else it
                                    }
                                },
                                showDate = true,
                                enableRatingDialog = true
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun FilterColumn(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            Column {
                options.forEach { option ->
                    FilterOptionItem(
                        text = option,
                        isSelected = option == selectedOption,
                        onClick = { onOptionSelected(option) }
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
@Composable
fun FilterOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFE0F2F1) else Color.Transparent
    val contentColor = if (isSelected) Color(0xFF00796B) else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = contentColor.copy(alpha = 0.7f)
        )
    }
}
@Composable
fun FilterBreadcrumb(
    text: String,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE0F2F1))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = Color(0xFF00796B),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Réinitialiser ce filtre",
            tint = Color(0xFF00796B),
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .clickable(onClick = onClear)
        )
    }
}
@Composable
fun ClickableTextField(
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(value, color = if (value.contains("Date") || value.contains("Heure")) Color.Gray else Color.Black)
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
    }
}