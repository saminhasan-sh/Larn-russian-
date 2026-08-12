package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.VocabEntity
import com.example.ui.MainViewModel
import com.example.ui.components.EditVocabDialog
import com.example.ui.components.VocabCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val filteredVocab by viewModel.filteredVocab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val isFavoriteFilterOnly by viewModel.isFavoriteFilterOnly.collectAsState()

    var isAddCardExpanded by remember { mutableStateOf(false) }

    // Add Word Form State
    var russianInput by remember { mutableStateOf("") }
    var bengaliInput by remember { mutableStateOf("") }
    var selectedCategoryInput by remember { mutableStateOf("Nouns") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Edit Word Dialog State
    var editingVocabItem by remember { mutableStateOf<VocabEntity?>(null) }

    val categories = listOf("Nouns", "Verbs", "Adjectives", "Food", "Animals", "Travel", "Family", "Greetings", "Custom Category")

    if (editingVocabItem != null) {
        EditVocabDialog(
            vocab = editingVocabItem!!,
            categories = categories,
            onDismiss = { editingVocabItem = null },
            onSave = { updated ->
                viewModel.updateVocab(updated)
                editingVocabItem = null
                Toast.makeText(context, "Word updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Add Vocabulary Card Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_vocab_section")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isAddCardExpanded = !isAddCardExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Add New Vocabulary",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Text(
                            text = if (isAddCardExpanded) "Hide" else "Expand",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(visible = isAddCardExpanded) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            // Russian Word Input
                            OutlinedTextField(
                                value = russianInput,
                                onValueChange = { russianInput = it },
                                label = { Text("Russian Word") },
                                placeholder = { Text("e.g., Здравствуйте") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("russian_word_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bengali Meaning Input
                            OutlinedTextField(
                                value = bengaliInput,
                                onValueChange = { bengaliInput = it },
                                label = { Text("Bengali Meaning") },
                                placeholder = { Text("e.g., হ্যালো / আসসালামু আলাইকুম") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bengali_meaning_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Category Selector
                            ExposedDropdownMenuBox(
                                expanded = categoryDropdownExpanded,
                                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedCategoryInput,
                                    onValueChange = { selectedCategoryInput = it },
                                    label = { Text("Category") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    singleLine = true
                                )
                                ExposedDropdownMenu(
                                    expanded = categoryDropdownExpanded,
                                    onDismissRequest = { categoryDropdownExpanded = false }
                                ) {
                                    categories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat) },
                                            onClick = {
                                                selectedCategoryInput = cat
                                                categoryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (russianInput.isNotBlank() && bengaliInput.isNotBlank()) {
                                        viewModel.addVocab(
                                            russian = russianInput,
                                            bengali = bengaliInput,
                                            category = selectedCategoryInput
                                        )
                                        russianInput = ""
                                        bengaliInput = ""
                                        Toast.makeText(context, "Saved to Local Storage!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Please enter both Russian & Bengali words", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("save_vocab_button")
                            ) {
                                Text("Save Vocabulary Word")
                            }
                        }
                    }
                }
            }
        }

        // Live Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Search Russian word or Bengali meaning...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vocab_search_bar")
            )
        }

        // Filter Chips Row (All Words vs Favorites) + Category Filter
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = !isFavoriteFilterOnly,
                        onClick = { viewModel.isFavoriteFilterOnly.value = false },
                        label = { Text("All Words") },
                        colors = FilterChipDefaults.filterChipColors()
                    )

                    FilterChip(
                        selected = isFavoriteFilterOnly,
                        onClick = { viewModel.isFavoriteFilterOnly.value = true },
                        label = { Text("⭐ Favorite Words") },
                        leadingIcon = {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal Category filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allCats = listOf("All") + categories
                    items(allCats) { cat ->
                        val isSelected = (selectedCategoryFilter == cat) || (cat == "All" && selectedCategoryFilter == null)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.selectedCategoryFilter.value = if (cat == "All") null else cat
                            },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        }

        // Showing count badge
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vocabulary List (${filteredVocab.size} words)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Empty state
        if (filteredVocab.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Vocabulary Words Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your search query or add a new Russian word above.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredVocab, key = { it.id }) { vocab ->
                VocabCard(
                    vocab = vocab,
                    onSpeak = { viewModel.speakRussian(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onEdit = { editingVocabItem = it },
                    onDelete = { viewModel.deleteVocab(it) }
                )
            }
        }
    }
}
