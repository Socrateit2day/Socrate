package com.example.expertpreste

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.expertpreste.Api.Prestataire
import com.example.expertpreste.Api.RetrofitInstance
import com.example.expertpreste.ui.theme.ExpertPresteTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpertPresteTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val expertsState = remember { mutableStateOf<List<Prestataire>>(emptyList()) }

    NavHost(navController = navController, startDestination = "experts_list") {
        composable("experts_list") {
            ExpertsScreen(navController = navController, expertsState = expertsState)
        }
        composable("expert_detail_id/{expertId}") { backStackEntry ->
            val expertId = backStackEntry.arguments?.getString("expertId")?.toIntOrNull()
            val expert = expertsState.value.find { it.user.id == expertId }

            if (expert != null) {
                ExpertDetailScreen(
                    expert = expert,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }

        composable("other_experts") {
            OtherExpertsScreen(navController = navController, experts = expertsState.value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertsScreen(navController: NavController, expertsState: MutableState<List<Prestataire>>) {
    var searchQuery by remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    var expandedMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading.value = true
        errorMessage.value = null
        val response = withContext(Dispatchers.IO) {
            try {
                RetrofitInstance.api.getPrestataires().execute()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}")
                null
            }
        }

        if (response?.isSuccessful == true) {
            expertsState.value = response.body()?.results ?: emptyList()
            Log.d("API_SUCCESS", "Données récupérées: ${expertsState.value}")
        } else {
            errorMessage.value = "Erreur: ${response?.code()} - ${response?.message() ?: "Échec de la connexion"}"
            Log.e("API_ERROR", errorMessage.value ?: "Erreur inconnue")
        }
        isLoading.value = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes experts des services", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { expandedMenu = !expandedMenu }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color(0xFF333333))
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Autres Experts") },
                                onClick = {
                                    navController.navigate("other_experts")
                                    expandedMenu = false
                                }
                            )
                        }
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Rechercher par nom ou catégorie") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(16.dp))

            val filteredExperts = expertsState.value.filter {
                it.user.first_name.contains(searchQuery, ignoreCase = true) ||
                        it.user.last_name.contains(searchQuery, ignoreCase = true) ||
                        (it.jobcategorie?.nom?.contains(searchQuery, ignoreCase = true) ?: false)
            }

            when {
                isLoading.value -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                errorMessage.value != null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage.value ?: "Erreur inconnue", color = Color.Red)
                }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredExperts) { expert ->
                        val index = filteredExperts.indexOf(expert)
                        ExpertCard(expert = expert, navController = navController, index = index)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpertCard(expert: Prestataire, navController: NavController, index: Int) {
    val buttonColor = Color(0xFF26A69A)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = expert.photo ?: "https://cdn-icons-png.flaticon.com/512/3135/3135715.png",
                contentDescription = "Photo Expert",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("${expert.user.first_name} ${expert.user.last_name}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Work, contentDescription = null, tint = Color(0xFF26A69A), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(expert.jobcategorie?.nom ?: "Non spécifié", fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val rating = (expert.is_visited % 5).toInt()
                    repeat(5) { i ->
                        Icon(Icons.Filled.Star, contentDescription = null, tint = if (i < rating) Color(0xFFFFC107) else Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(if (rating == 0) "Non noté" else "$rating.0 / 5", fontSize = 13.sp, color = Color.Gray)
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = buttonColor),
                        border = BorderStroke(1.dp, buttonColor),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = "Noter", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Noter", fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = { navController.navigate("expert_detail/$index") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = buttonColor),
                        border = BorderStroke(1.dp, buttonColor),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Filled.Visibility, contentDescription = "Détail", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Détail", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppNavigator() {
    ExpertPresteTheme { AppNavigator() }
}
