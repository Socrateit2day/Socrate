package com.example.expertpreste

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
                label = { Text("Rechercher un expert ou service") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Liste filtrée
            val filteredExperts = experts.filter {
                val nameMatch = it.user.first_name.contains(searchText, ignoreCase = true) ||
                        it.user.last_name.contains(searchText, ignoreCase = true)
                val jobMatch = it.jobcategorie?.nom?.contains(searchText, ignoreCase = true) ?: false
                nameMatch || jobMatch
            }

            if (filteredExperts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun expert trouvé.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    itemsIndexed(filteredExperts) { index, expert ->
                        ExpertItem(expert = expert, navController = navController, index = index)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpertItem(expert: Prestataire, navController: NavController, index: Int) {
    val buttonColor = Color(0xFF26A69A)
    val rating = (expert.is_visited % 5).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = expert.photo ?: "https://cdn-icons-png.flaticon.com/512/3135/3135715.png",
                contentDescription = "${expert.user.first_name} ${expert.user.last_name}",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${expert.user.first_name} ${expert.user.last_name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        tint = Color(0xFF26A69A),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = expert.jobcategorie?.nom ?: "Non spécifié",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = expert.tarif ?: "Tarif non précisé",
                    color = Color(0xFF26A69A),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
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
                    Text(
                        text = if (rating == 0) "Non noté" else "$rating.0 / 5",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 🔘 Bouton Détail
                OutlinedButton(
                    onClick = { navController.navigate("expert_detail/$index") }, // ✅ Passe l’index correctement
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = buttonColor),
                    border = BorderStroke(1.dp, buttonColor),
                    modifier = Modifier.width(120.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "Détail",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Détail", fontSize = 14.sp)
                }

            }
        }
    }
}
