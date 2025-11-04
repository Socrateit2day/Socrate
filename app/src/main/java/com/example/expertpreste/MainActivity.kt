package com.example.expertpreste

import android.R.attr.color
import android.R.attr.text
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expertpreste.ui.theme.ExpertPresteTheme
import kotlinx.parcelize.Parcelize

@Parcelize
data class Localite(
    val province: String,
    val commune: String,
    val zone: String,
    val colline: String
) : Parcelable {
    fun afficherAdresse(): String = "$province / $commune / $zone / $colline"
}

@Parcelize
data class Expert(
    val name: String,
    val job: String,
    val imageRes: Int,
    val titre: String,
    val description: String,
    val competences: String,
    val date: String,
    val experience: String,
    val services: String,
    val disponibilites: String,
    var rating: Float,
    var nbRatings: Int,
    val dernierePrestation: Long,
    val statutPrestation: String,
    val estDisponible: Boolean,
    val montant: Int,
    val localite: Localite,
    val gmail: String,
    val maxRating: Int = 5
) : Parcelable

val expertList = listOf(
    Expert("Socrate Augustin", "Informatique", R.drawable.moise, "Entreprise",
        "Développeur passionné.", "Kotlin, PHP, JS", "01/02/2025",
        "5 ans", "Apps Android", "Lundi-Vendredi", 4.2f, 5,
        System.currentTimeMillis(), "Terminé", true, 5000,
        Localite("Bujumbura Mairie", "Mukaza", "Rohero", "INSS"),
        "socrate.augustin@gmail.com"
    ),
    Expert("Sam Ruden", "Cuisinier", R.drawable.p22, "Entreprise",
        "Chef expérimenté.", "Cuisine, Pâtisserie", "12/01/2025",
        "10 ans", "Buffets, mariages", "Week-ends", 4.8f, 8,
        System.currentTimeMillis(), "En cours", false, 7000,
        Localite("Gitega", "Gitega", "Yoba", "Mungwa"),
        "sam.ruden@gmail.com"
    ),
    Expert("Alice Martin", "Couturier", R.drawable.p33, "Particulier",
        "Graphiste créative.", "Photoshop, Illustrator", "05/01/2025",
        "4 ans", "Logos, UI/UX", "Lundi-Vendredi", 4.5f, 3,
        System.currentTimeMillis(), "Terminé", true, 4000,
        Localite("Ngozi", "Ngozi", "Kiremba", "Nyamurenza"),
        "alice.martin@gmail.com"
    ),
    Expert("John Doe", "Photographe", R.drawable.p44, "Particulier",
        "Photographe professionnel.", "Portrait, Événements", "20/12/2024",
        "6 ans", "Mariages, événements", "Week-ends", 4.1f, 7,
        System.currentTimeMillis(), "Terminé", true, 6000,
        Localite("Rumonge", "Burambi", "Kigwena", "Kizuka"),
        "john.doe@gmail.com"
    ),
    Expert("Emma Brown", "Informatique", R.drawable.p55, "Entreprise",
        "Développeuse front-end.", "HTML, CSS, JS", "10/02/2025",
        "3 ans", "Sites Web", "Lundi-Vendredi", 4.0f, 2,
        System.currentTimeMillis(), "Terminé", false, 4500,
        Localite("Makamba", "Makamba", "Muyange", "Gisuru"),
        "emma.brown@gmail.com"
    ),
    Expert("Pierre Nkurunziza", "Plomberie", R.drawable.p66, "Entreprise",
        "Expert en plomberie.", "Installation, Réparation", "03/03/2025",
        "8 ans", "Rénovations", "Lundi-Vendredi", 4.6f, 6,
        System.currentTimeMillis(), "Terminé", true, 5200,
        Localite("Bujumbura", "Ntahangwa", "Rohero", "INSS"),
        "pierre.nkurunziza@gmail.com"
    ),
    Expert("Claire Uwimana", "Électricité", R.drawable.p77, "Particulier",
        "Électricienne qualifiée.", "Domestique, Industrielle", "15/02/2025",
        "7 ans", "Installations électriques", "Lundi-Vendredi", 4.3f, 4,
        System.currentTimeMillis(), "Terminé", true, 4800,
        Localite("Gitega", "Gitega", "Mungwa", "Centre"),
        "claire.uwimana@gmail.com"
    ),
    Expert("Socrate Augustin", "Informatique", R.drawable.moise, "Entreprise",
        "Développeur passionné.", "Kotlin, PHP, JS", "01/02/2025",
        "5 ans", "Apps Android", "Lundi-Vendredi", 4.2f, 5,
        System.currentTimeMillis(), "Terminé", true, 5000,
        Localite("Bujumbura Mairie", "Mukaza", "Rohero", "INSS"),
        "socrate.augustin@gmail.com"
    ),
    Expert("Sam Ruden", "Cuisinier", R.drawable.p22, "Entreprise",
        "Chef expérimenté.", "Cuisine, Pâtisserie", "12/01/2025",
        "10 ans", "Buffets, mariages", "Week-ends", 4.8f, 8,
        System.currentTimeMillis(), "En cours", false, 7000,
        Localite("Gitega", "Gitega", "Yoba", "Mungwa"),
        "sam.ruden@gmail.com"
    ),
    Expert("Alice Martin", "Couturier", R.drawable.p33, "Particulier",
        "Graphiste créative.", "Photoshop, Illustrator", "05/01/2025",
        "4 ans", "Logos, UI/UX", "Lundi-Vendredi", 4.5f, 3,
        System.currentTimeMillis(), "Terminé", true, 4000,
        Localite("Ngozi", "Ngozi", "Kiremba", "Nyamurenza"),
        "alice.martin@gmail.com"
    ),
    Expert("John Doe", "Photographe", R.drawable.p44, "Particulier",
        "Photographe professionnel.", "Portrait, Événements", "20/12/2024",
        "6 ans", "Mariages, événements", "Week-ends", 4.1f, 7,
        System.currentTimeMillis(), "Terminé", true, 6000,
        Localite("Rumonge", "Burambi", "Kigwena", "Kizuka"),
        "john.doe@gmail.com"
    ),
    Expert("Emma Brown", "Informatique", R.drawable.p55, "Entreprise",
        "Développeuse front-end.", "HTML, CSS, JS", "10/02/2025",
        "3 ans", "Sites Web", "Lundi-Vendredi", 4.0f, 2,
        System.currentTimeMillis(), "Terminé", false, 4500,
        Localite("Makamba", "Makamba", "Muyange", "Gisuru"),
        "emma.brown@gmail.com"
    ),
    Expert("Pierre Nkurunziza", "Plomberie", R.drawable.p66, "Entreprise",
        "Expert en plomberie.", "Installation, Réparation", "03/03/2025",
        "8 ans", "Rénovations", "Lundi-Vendredi", 4.6f, 6,
        System.currentTimeMillis(), "Terminé", true, 5200,
        Localite("Bujumbura", "Ntahangwa", "Rohero", "INSS"),
        "pierre.nkurunziza@gmail.com"
    ),
    Expert("Claire Uwimana", "Électricité", R.drawable.p77, "Particulier",
        "Électricienne qualifiée.", "Domestique, Industrielle", "15/02/2025",
        "7 ans", "Installations électriques", "Lundi-Vendredi", 4.3f, 4,
        System.currentTimeMillis(), "Terminé", true, 4800,
        Localite("Gitega", "Gitega", "Mungwa", "Centre"),
        "claire.uwimana@gmail.com"
    )
)


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
    NavHost(navController = navController, startDestination = "experts_list") {
        composable("experts_list") {
            ExpertsScreen(navController = navController) { expert ->
                navController.currentBackStackEntry?.savedStateHandle?.set("expert", expert)
                navController.navigate("expert_detail_history")
            }
        }

        composable("other_experts") {
            OtherExpertsScreen(navController = navController)
        }

        composable("expert_detail_history") {
            val expert = navController.previousBackStackEntry?.savedStateHandle?.get<Expert>("expert")
            if (expert != null) {
                ExpertDetailScreen(
                    expert = expert,
                    onNavigateBack = { navController.popBackStack() },
                    showHistory = true
                )
            }
        }

        composable("expert_detail_other") {
            val expert = navController.previousBackStackEntry?.savedStateHandle?.get<Expert>("expert")
            if (expert != null) {
                ExpertDetailScreen(
                    expert = expert,
                    onNavigateBack = { navController.popBackStack() },
                    showHistory = false
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertsScreen(navController: NavController, onExpertClick: (Expert) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var experts by remember { mutableStateOf(expertList.map { it.copy() }) }
    var expandedMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mes experts des services",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { /* Action de retoure */ },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { expandedMenu = !expandedMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color(0xFF333333)
                        )
                    }

                    DropdownMenu(
                        modifier = Modifier.background(Color.White),
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Autres Experts") },
                            onClick = {
                                expandedMenu = false
                                navController.navigate("other_experts")
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F9FC),
                    titleContentColor = Color.Black
                ),
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
                label = { Text("Rechercher par nom, date ou travail") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(Modifier.height(16.dp))
            val filteredExperts = experts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.date.contains(searchQuery, ignoreCase = true) ||
                        it.job.contains(searchQuery, ignoreCase = true)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredExperts) { expert ->
                    ExpertCard(
                        expert = expert,
                        onClick = { onExpertClick(expert) },
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

@Composable
fun ExpertCard(
    expert: Expert,
    onClick: () -> Unit,
    onRatingChange: (Float, Int) -> Unit,
    showDate: Boolean = true,
    enableRatingDialog: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempRating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.size(70.dp)) {
                    Image(
                        painter = painterResource(id = expert.imageRes),
                        contentDescription = expert.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (expert.estDisponible) Color(0xFF26A69A) else Color(0xFFFF5252))
                            .align(Alignment.BottomEnd)
                            .offset((-3).dp, 3.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(expert.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(expert.job, fontSize = 14.sp, color = Color.Black)
                    if (showDate) {
                        Text(text = expert.date,color = Color.Gray
                        )
                    }
                }

            }
            Spacer(Modifier.height(10.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(expert.maxRating) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < expert.rating.toInt()) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(enabled = enableRatingDialog) { if (enableRatingDialog) showDialog = true }
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f / %.0f", expert.rating, expert.maxRating.toFloat()),
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

            }
        }

        if (showDialog && enableRatingDialog) {
            var showError by remember { mutableStateOf(false) }
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = expert.imageRes),
                            contentDescription = expert.name,
                            modifier = Modifier.size(50.dp).clip(CircleShape)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(expert.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(expert.job, fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Donnez votre note (max 5) :", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(expert.maxRating) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < tempRating) Color(0xFFFFC107) else Color.Gray,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clickable { tempRating = index + 1; showError = false }
                                )
                            }
                        }
                        if (showError) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "⚠️ Vous devez donner une note avant de valider.",
                                color = Color.Red,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = { Text("Votre commentaire") },
                            placeholder = { Text("Ajoutez un commentaire") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempRating > 0) {
                                val total = expert.rating * expert.nbRatings + tempRating
                                val newCount = expert.nbRatings + 1
                                val newAverage = total / newCount
                                onRatingChange(newAverage, newCount)
                                showDialog = false
                            } else showError = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Valider") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Annuler", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppNavigator() {
    ExpertPresteTheme { AppNavigator() }
}