package com.example.expertpreste

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.expertpreste.Api.Prestataire

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertDetailScreen(
    expert: Prestataire,
    onNavigateBack: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var demandeEffectuee by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil de l’expert", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F9FC))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF7F9FC))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = expert.photo ?: "https://cdn-icons-png.flaticon.com/512/3135/3135715.png",
                contentDescription = "${expert.user.first_name} ${expert.user.last_name}",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${expert.user.first_name} ${expert.user.last_name}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = expert.jobcategorie?.nom ?: "Non spécifié",
                fontSize = 15.sp,
                color = Color.Gray
            )
            Text(
                text = expert.recipient_type,
                fontSize = 15.sp,
                color = Color(0xFF26A69A),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    InfoItem(
                        "Compétences / Domaines",
                        if (expert.prestataire_besoins.isNotEmpty())
                            expert.prestataire_besoins.joinToString { it.besoin_nom }
                        else "Non précisé"
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    InfoItem("Expérience", "Non précisé")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    InfoItem(
                        "Services proposés",
                        if (expert.prestataire_besoins.isNotEmpty())
                            expert.prestataire_besoins.joinToString { it.description }
                        else "Non spécifié"
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "⭐ Note estimée: ${(expert.is_visited % 5).toInt()}.0 / 5",
                        color = Color(0xFF26A69A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoItem("Tarif", expert.tarif ?: "Non précisé")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    InfoItem("Disponibilités", if (expert.user.is_active) "Disponible" else "Non disponible")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    InfoItem(
                        "Localisation",
                        "${expert.user.province_name} / ${expert.user.commune_name} / ${expert.user.zone_name} / ${expert.user.colline_name}"
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    InfoItem("Description", expert.personal_description ?: "Non spécifié")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

        }
    }
}
@Composable
fun InfoItem(title: String, content: String) {
    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    Text(
        text = content,
        fontSize = 14.sp,
        color = Color.DarkGray,
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
