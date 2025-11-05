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

            Spacer(modifier = Modifier.height(25.dp))

            if (!demandeEffectuee) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Expert disponible",
                        color = Color(0xFF00897B),
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sélectionner cet expert")
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Cet expert n’est plus disponible",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sélectionner cet expert")
                    }
                }
            }

            if (showDialog) {
                DemandeServiceDialog(
                    expert = expert,
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        showDialog = false
                        demandeEffectuee = true
                    }
                )
            }
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
                    modifier = Modifier.size(55.dp).clip(CircleShape)
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
