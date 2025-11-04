package com.example.expertpreste

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertDetailScreen(
    expert: Expert,
    onNavigateBack: () -> Unit,
    showHistory: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }
    var demandeEffectuee by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profil de l’expert",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = expert.imageRes),
                contentDescription = expert.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(expert.name, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text(expert.job, fontSize = 17.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = expert.titre,
                color = Color(0xFF26A69A),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    InfoItem("Description", expert.description)
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    InfoItem("Compétences", expert.competences)
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    if (showHistory) {
                        InfoItemWithStatus("Dernière prestation", SimpleDateFormat("dd/MM/yyyy").format(expert.dernierePrestation))
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider()
                        InfoItemWithStatus("Statut de la prestation", expert.statutPrestation)
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider()
                    }
                    InfoItem("Adresse", expert.localite.afficherAdresse())
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    InfoItem("Expérience", expert.experience)
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    InfoItem("⭐ Note moyenne", "%.1f / 5  (%d avis)".format(expert.rating, expert.nbRatings))
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    InfoItem("Services proposés", expert.services)
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    InfoItem("Tarif", "${expert.montant} Fbu")
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    InfoItem("Gmail", expert.gmail)
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    InfoItem("Disponibilités", expert.disponibilites)
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            if (expert.estDisponible && !demandeEffectuee) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Expert disponible (${expert.disponibilites})",
                        color = Color(0xFF00897B),
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (showHistory) "Redemander un service" else "Demander un service")
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (demandeEffectuee)
                            "Cet expert n’est plus disponible"
                        else
                            "Expert non disponible pour le moment",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (showHistory) "Redemander un service" else "Demander un service")
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
    expert: Expert,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var commentaire by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = expert.imageRes),
                    contentDescription = expert.name,
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(expert.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(expert.job, fontSize = 14.sp, color = Color.Gray)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Service demandé : ${expert.services}",
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF26A69A),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = commentaire,
                    onValueChange = {
                        commentaire = it
                        showError = false
                    },
                    label = { Text("Votre commentaire *") },
                    placeholder = { Text("Ajoutez un message pour cet expert") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 4
                )
                if (showError) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "⚠️ Le commentaire est obligatoire.",
                        color = Color.Red,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (commentaire.isNotBlank()) onConfirm() else showError = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Demander")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Color.Gray)
            }
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

@Composable
fun InfoItemWithStatus(title: String, status: String) {
    val statusColor =
        if (status == "Terminé") Color(0xFF388E3C) else Color(0xFFF57C00)
    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    Text(
        text = status,
        fontSize = 14.sp,
        color = statusColor,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
