import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.google.gson.annotations.SerializedName

data class AnimalDetails(
    @SerializedName("Species") val species: String,
    @SerializedName("Scientific Name") val scientificName: String,
    @SerializedName("Habitat") val habitat: String,
    @SerializedName("Diet") val diet: String,
    @SerializedName("Lifespan") val lifespan: String,
    @SerializedName("Size & Weight") val sizeWeight: String,
    @SerializedName("Reproduction") val reproduction: String,
    @SerializedName("Behavior") val behavior: String,
    @SerializedName("Conservation Status") val conservationStatus: String,
    @SerializedName("Special Adaptations") val specialAdaptations: String
)

sealed class AnimalDetailTile {
    data class FullWidthTile(
        val icon: ImageVector,
        val title: String,
        val value: String,
        val secondaryValue: String? = null,
        val color: Color
    ) : AnimalDetailTile()

    data class HalfWidthTile(
        val icon: ImageVector,
        val title: String,
        val value: String,
        val color: Color
    ) : AnimalDetailTile()

    companion object {
        @Composable
        fun createFullWidth(
            icon: ImageVector,
            title: String,
            value: String,
            secondaryValue: String? = null,
            color: Color = MaterialTheme.colorScheme.primary
        ): FullWidthTile = FullWidthTile(icon, title, value, secondaryValue, color)

        @Composable
        fun createHalfWidth(
            icon: ImageVector,
            title: String,
            value: String,
            color: Color = MaterialTheme.colorScheme.primary
        ): HalfWidthTile = HalfWidthTile(icon, title, value, color)
    }
}

@Composable
fun HalfWidthTilesRow(
    leftTile: AnimalDetailTile.HalfWidthTile,
    rightTile: AnimalDetailTile.HalfWidthTile,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(IntrinsicSize.Max)
        ) {
            DetailTile(tile = leftTile)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(IntrinsicSize.Max)
        ) {
            DetailTile(tile = rightTile)
        }
    }
}

@Composable
fun AnimalDetailGrid(animalDetails: AnimalDetails, imageUrl: String) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp,
                shadowElevation = 2.dp
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = "Image of ${animalDetails.species}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                strokeWidth = 4.dp
                            )
                        }
                    }
                )
            }
        }

        // Header tiles (Species and Scientific Name) in a row
        item {
            HalfWidthTilesRow(
                leftTile = AnimalDetailTile.createHalfWidth(
                    icon = Icons.Default.Pets,
                    title = "Species",
                    value = animalDetails.species,
                    color = MaterialTheme.colorScheme.primary
                ),
                rightTile = AnimalDetailTile.createHalfWidth(
                    icon = Icons.Default.Science,
                    title = "Scientific Name",
                    value = animalDetails.scientificName,
                    color = MaterialTheme.colorScheme.secondary
                )
            )
        }

        // Full-width tiles
        item {
            DetailTile(
                tile = AnimalDetailTile.createFullWidth(
                    icon = Icons.Default.Park,
                    title = "Habitat",
                    value = animalDetails.habitat,
                    color = MaterialTheme.colorScheme.tertiary
                )
            )
        }

        // Diet and Lifespan in a row
        item {
            HalfWidthTilesRow(
                leftTile = AnimalDetailTile.createHalfWidth(
                    icon = Icons.Default.Restaurant,
                    title = "Diet",
                    value = animalDetails.diet,
                    color = MaterialTheme.colorScheme.secondary
                ),
                rightTile = AnimalDetailTile.createHalfWidth(
                    icon = Icons.Default.Cake,
                    title = "Lifespan",
                    value = animalDetails.lifespan,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        // Full-width tiles for the rest
        item {
            DetailTile(
                tile = AnimalDetailTile.createFullWidth(
                    icon = Icons.Default.Straighten,
                    title = "Size & Weight",
                    value = animalDetails.sizeWeight
                )
            )
        }

        item {
            DetailTile(
                tile = AnimalDetailTile.createFullWidth(
                    icon = Icons.Default.FamilyRestroom,
                    title = "Reproduction",
                    value = animalDetails.reproduction
                )
            )
        }

        item {
            DetailTile(
                tile = AnimalDetailTile.createFullWidth(
                    icon = Icons.Default.Psychology,
                    title = "Behavior",
                    value = animalDetails.behavior
                )
            )
        }

        item {
            DetailTile(
                tile = AnimalDetailTile.createFullWidth(
                    icon = Icons.Default.HealthAndSafety,
                    title = "Conservation",
                    value = animalDetails.conservationStatus,
                    color = if (animalDetails.conservationStatus == "Vulnerable")
                        Color(0xFFF4D03F) else Color(0xFF58D68D)
                )
            )
        }

        item {
            DetailTile(
                tile = AnimalDetailTile.createFullWidth(
                    icon = Icons.Default.Construction,
                    title = "Adaptations",
                    value = animalDetails.specialAdaptations
                )
            )
        }
    }
}

@Composable
fun DetailTile(tile: AnimalDetailTile) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .heightIn(min = 120.dp), // Minimum height for consistency
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(color = when(tile) {
                        is AnimalDetailTile.FullWidthTile -> tile.color
                        is AnimalDetailTile.HalfWidthTile -> tile.color
                    }.copy(alpha = 0.2f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = when(tile) {
                        is AnimalDetailTile.FullWidthTile -> tile.icon
                        is AnimalDetailTile.HalfWidthTile -> tile.icon
                    },
                    contentDescription = null,
                    tint = when(tile) {
                        is AnimalDetailTile.FullWidthTile -> tile.color
                        is AnimalDetailTile.HalfWidthTile -> tile.color
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = when(tile) {
                    is AnimalDetailTile.FullWidthTile -> tile.title
                    is AnimalDetailTile.HalfWidthTile -> tile.title
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = when(tile) {
                    is AnimalDetailTile.FullWidthTile -> tile.value
                    is AnimalDetailTile.HalfWidthTile -> tile.value
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (tile is AnimalDetailTile.FullWidthTile) {
                tile.secondaryValue?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun AnimalDetailGridPreview() {
    MaterialTheme {
        val sampleData = AnimalDetails(
            species = "Giant Panda",
            scientificName = "Ailuropoda melanoleuca",
            habitat = "Mountain forests in central China",
            diet = "Herbivorous, primarily bamboo",
            lifespan = "20-30 years",
            sizeWeight = "1.5m long, 80-140 kg",
            reproduction = "3-5 month gestation",
            behavior = "Solitary animals",
            conservationStatus = "Vulnerable",
            specialAdaptations = "Pseudo-thumbs for bamboo"
        )

        AnimalDetailGrid(animalDetails = sampleData, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Aptenodytes_forsteri_-Snow_Hill_Island%2C_Antarctica_-adults_and_juvenile-8.jpg/800px-Aptenodytes_forsteri_-Snow_Hill_Island%2C_Antarctica_-adults_and_juvenile-8.jpg")
    }
}