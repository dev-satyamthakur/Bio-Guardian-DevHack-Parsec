package com.satyamthakur.bio_guardian.ui.composables

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satyamthakur.bio_guardian.R
import com.satyamthakur.bio_guardian.ui.theme.Montserrat
import com.satyamthakur.bio_guardian.ui.theme.md_theme_light_onTertiaryContainer
import com.satyamthakur.bio_guardian.ui.theme.md_theme_light_tertiaryContainer

private val exploreTitles = listOf(
    "Nearby\nReserves", "Conservation\nEfforts"
)

@Composable
fun ExploreMoreSection() {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Explore More",
            fontFamily = Montserrat,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = md_theme_light_onTertiaryContainer
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            exploreTitles.forEachIndexed { index, title ->
                ExploreMoreCard(
                    title = title,
                    index = index,
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = if (index == 0) 16.dp else 5.dp,
                            end = if (index == 1) 16.dp else 5.dp,
                            top = 10.dp
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreMoreCard(title: String, index: Int, modifier: Modifier) {
    val context = LocalContext.current
    val conservationEffortsIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.worldwildlife.org/initiatives/wildlife-conservation")
        )
    }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = md_theme_light_tertiaryContainer,
        ),
        onClick = {
            when (index) {
                0 -> {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=natural%20reserves")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    context.startActivity(mapIntent)
                }
                1 -> context.startActivity(conservationEffortsIntent)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = md_theme_light_onTertiaryContainer
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .align(Alignment.End)
                    .size(120.dp)
                    .offset(x = 10.dp, y = 12.dp),
                painter = painterResource(id = R.drawable.polypodium_leaves),
                contentDescription = null
            )
        }
    }
}
