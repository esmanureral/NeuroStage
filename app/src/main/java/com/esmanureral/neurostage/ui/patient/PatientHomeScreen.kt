package com.esmanureral.neurostage.ui.patient

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.onboarding.RolePickViewModel
import com.esmanureral.neurostage.ui.theme.NsBlue700
import com.esmanureral.neurostage.ui.theme.NsIndigo500
import com.esmanureral.neurostage.ui.theme.NsLavender
import com.esmanureral.neurostage.ui.theme.NsRose
import com.esmanureral.neurostage.ui.theme.NsViolet500
import com.esmanureral.neurostage.ui.theme.NsWhite

private val PatientBgTop = Color(0xFF0F1B3C)
private val PatientBgMid = Color(0xFF162756)
private val PatientBgBottom = Color(0xFF1A3E8F)
private val GlassBg = Color(0x1AFFFFFF)
private val GlassBorder = Color(0x33FFFFFF)
private val ScanAccent = Color(0xFF4FC3F7)
private val HistoryAccent = Color(0xFF81C784)
private val InfoAccent = Color(0xFFFFB74D)
private val HeartPink = Color(0xFFE05A8A)

@Composable
fun PatientHomeScreen(
    onStartScan: () -> Unit,
    onBackToRolePick: () -> Unit,
    viewModel: RolePickViewModel = hiltViewModel(),
) {
    val infiniteTransition = rememberInfiniteTransition(label = "patient_home")

    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                1f at 0 using LinearEasing
                1.30f at 100 using FastOutSlowInEasing
                1f at 200 using FastOutSlowInEasing
                1.22f at 330 using FastOutSlowInEasing
                1f at 450 using FastOutSlowInEasing
                1f at 1200 using LinearEasing
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "heartbeat",
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.20f at 0 using LinearEasing
                0.55f at 200 using FastOutSlowInEasing
                0.20f at 500 using FastOutSlowInEasing
                0.20f at 1200 using LinearEasing
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "heartGlow",
    )

    Scaffold(
        containerColor = PatientBgTop,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.clear()
                    onBackToRolePick()
                },
                containerColor = NsRose,
                contentColor = NsWhite,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(R.string.patient_home_logout_button),
                    modifier = Modifier.size(22.dp),
                )
            }
        },
    ) { innerPad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(PatientBgTop, PatientBgMid, PatientBgBottom),
                    ),
                )
                .padding(innerPad),
        ) {

            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NsIndigo500.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset.Zero,
                            radius = 420f,
                        ),
                        CircleShape,
                    )
                    .blur(64.dp),
            )
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NsViolet500.copy(alpha = 0.20f), Color.Transparent),
                            center = Offset.Zero,
                            radius = 320f,
                        ),
                        CircleShape,
                    )
                    .blur(56.dp),
            )

            // ── Scrollable body ──────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Spacer(Modifier.height(36.dp))

                // ── Beating heart hero ───────────────────────────────────────
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp),
                ) {
                    // Ambient glow ring behind the heart
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                HeartPink.copy(alpha = glowAlpha),
                                CircleShape,
                            )
                            .blur(28.dp),
                    )
                    // Outer frosted circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(88.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFF2A1840),
                                        Color(0xFF1A1040),
                                    ),
                                ),
                                CircleShape,
                            ),
                    ) {
                        // Beating heart icon
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = HeartPink,
                            modifier = Modifier
                                .size(46.dp)
                                .scale(heartScale),
                        )
                    }
                }

                Spacer(Modifier.height(26.dp))

                Text(
                    text = stringResource(R.string.patient_home_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = NsWhite,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.patient_home_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NsWhite.copy(alpha = 0.68f),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                )

                Spacer(Modifier.height(44.dp))

                // ── Section label ────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 3.dp, height = 18.dp)
                            .background(ScanAccent, RoundedCornerShape(2.dp)),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.patient_home_actions_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = NsWhite.copy(alpha = 0.55f),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.4.sp,
                    )
                }

                Spacer(Modifier.height(14.dp))

                // ── MR Scan card (primary — functional) ──────────────────────
                PatientActionCard(
                    icon = Icons.Default.CameraAlt,
                    iconBrush = Brush.linearGradient(listOf(ScanAccent, NsBlue700)),
                    title = stringResource(R.string.patient_home_scan_button),
                    subtitle = stringResource(R.string.patient_home_scan_subtitle),
                    pill = stringResource(R.string.patient_home_scan_pill),
                    pillColor = ScanAccent,
                    onClick = onStartScan,
                )

                Spacer(Modifier.height(12.dp))

                PatientActionCard(
                    icon = Icons.Default.History,
                    iconBrush = Brush.linearGradient(listOf(HistoryAccent, Color(0xFF388E3C))),
                    title = stringResource(R.string.patient_home_history_button),
                    subtitle = stringResource(R.string.patient_home_history_subtitle),
                    pill = null,
                    pillColor = HistoryAccent,
                    onClick = { },
                )

                Spacer(Modifier.height(12.dp))

                PatientActionCard(
                    icon = Icons.Default.Info,
                    iconBrush = Brush.linearGradient(listOf(InfoAccent, Color(0xFFF57C00))),
                    title = stringResource(R.string.patient_home_help_button),
                    subtitle = stringResource(R.string.patient_home_help_subtitle),
                    pill = null,
                    pillColor = InfoAccent,
                    onClick = { },
                )

                Spacer(Modifier.height(28.dp))

                // ── Disclaimer strip ─────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(GlassBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = NsLavender,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.patient_home_info_disclaimer),
                        style = MaterialTheme.typography.bodySmall,
                        color = NsWhite.copy(alpha = 0.62f),
                        lineHeight = 18.sp,
                    )
                }

                // Extra padding so FAB doesn't overlap last card
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

// ─── Glassmorphism action card ─────────────────────────────────────────────────
@Composable
private fun PatientActionCard(
    icon: ImageVector,
    iconBrush: Brush,
    title: String,
    subtitle: String,
    pill: String?,
    pillColor: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick,
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBg),
        border = BorderStroke(1.dp, GlassBorder),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Gradient icon circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .background(iconBrush, CircleShape),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NsWhite,
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NsWhite,
                        fontSize = 15.sp,
                    )
                    if (pill != null) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(pillColor.copy(alpha = 0.22f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = pill,
                                color = pillColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = NsWhite.copy(alpha = 0.58f),
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = NsWhite.copy(alpha = 0.32f),
                modifier = Modifier.size(22.dp),
            )
        }
    }
}