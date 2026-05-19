package com.esmanureral.neurostage.ui.doctor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Size as GeometrySize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.ui.theme.NeurostageBrandBlue
import com.esmanureral.neurostage.ui.theme.NsDoctorAccentBlue
import com.esmanureral.neurostage.ui.theme.NsDoctorScaffoldBg
import com.esmanureral.neurostage.ui.theme.NsGray300
import com.esmanureral.neurostage.ui.theme.NsGray400
import com.esmanureral.neurostage.ui.theme.NsGray600
import com.esmanureral.neurostage.ui.theme.NsGray700
import com.esmanureral.neurostage.ui.theme.NsGray900
import com.esmanureral.neurostage.ui.theme.NsOrangeHot
import com.esmanureral.neurostage.ui.theme.NsStatusError
import com.esmanureral.neurostage.ui.theme.NsStatusSuccess
import com.esmanureral.neurostage.ui.theme.NsWhite
private val waveBottomShape: Shape = object : Shape {
    override fun createOutline(
        size: GeometrySize,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val waveDepth = with(density) { 28.dp.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - waveDepth)
            quadraticBezierTo(size.width / 2f, size.height + waveDepth, 0f, size.height - waveDepth)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun DoctorShellScreen(
    onOpenHistory: () -> Unit,
    onStartNewScan: () -> Unit,
    onOpenPatients: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: DoctorHomeViewModel = hiltViewModel(),
) {
    val status by viewModel.authStatus.collectAsStateWithLifecycle()
    val header by viewModel.header.collectAsStateWithLifecycle()
    val dashboard by viewModel.dashboard.collectAsStateWithLifecycle()
    var showProfileDrawer by remember { mutableStateOf(false) }
    var showSignOutConfirm by remember { mutableStateOf(false) }

    val initialsFallback = stringResource(R.string.doctor_ui_initials_unknown)

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(status) {
        when (status) {
            is AuthStatus.SignedIn -> viewModel.onHomeVisible()
            is AuthStatus.SignedOut -> onSignedOut()
            else -> Unit
        }
    }

    DisposableEffect(lifecycleOwner, status) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && status is AuthStatus.SignedIn) {
                viewModel.onHomeVisible()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val showHomeLoading = dashboard.isLoading && header.displayName.isNullOrBlank()

    BackHandler(enabled = showSignOutConfirm || showProfileDrawer) {
        when {
            showSignOutConfirm -> showSignOutConfirm = false
            showProfileDrawer -> showProfileDrawer = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize(), color = NsDoctorScaffoldBg) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                val initials = (header.displayName ?: "")
                    .split(" ")
                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                    .take(2)
                    .joinToString("")
                    .ifBlank { initialsFallback }

                DoctorHomeHeader(
                    initials = initials,
                    displayName = header.displayName,
                    onProfileClick = { showProfileDrawer = true },
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    DoctorStatsRow(
                        patientCount = dashboard.patientCount,
                        scanCount = dashboard.scanCount,
                        scansThisWeek = dashboard.scansThisWeek,
                    )

                    if (dashboard.patientCount == 0 && dashboard.scanCount == 0) {
                        Text(
                            text = stringResource(R.string.doctor_shell_get_started_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = NsGray400,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }

                    Text(
                        text = stringResource(R.string.doctor_shell_quick_access),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NsGray600,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                    )

                    ActionCard(
                        icon = Icons.Outlined.Upload,
                        title = stringResource(R.string.doctor_shell_action_mri_title),
                        subtitle = stringResource(R.string.doctor_shell_action_mri_subtitle),
                        gradient = Brush.horizontalGradient(
                            listOf(NeurostageBrandBlue, NsDoctorAccentBlue),
                        ),
                        onClick = onStartNewScan,
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmallActionCard(
                            icon = Icons.Outlined.People,
                            title = stringResource(R.string.doctor_shell_patients_title),
                            subtitle = stringResource(R.string.doctor_shell_patients_subtitle),
                            color = NsStatusSuccess,
                            modifier = Modifier.weight(1f),
                            onClick = onOpenPatients,
                        )
                        SmallActionCard(
                            icon = Icons.Outlined.History,
                            title = stringResource(R.string.doctor_shell_history_title),
                            subtitle = stringResource(R.string.doctor_shell_history_subtitle),
                            color = NsOrangeHot,
                            modifier = Modifier.weight(1f),
                            onClick = onOpenHistory,
                        )
                    }

                }

                Spacer(Modifier.height(32.dp))
            }
        }

        AnimatedVisibility(
            visible = showProfileDrawer,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showProfileDrawer = false },
            )
        }

        AnimatedVisibility(
            visible = showProfileDrawer,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            ProfileDrawer(
                displayName = header.displayName,
                email = header.email,
                initialsFallback = initialsFallback,
                onClose = { showProfileDrawer = false },
                onSignOut = {
                    showProfileDrawer = false
                    showSignOutConfirm = true
                },
                onOpenPatients = {
                    showProfileDrawer = false
                    onOpenPatients()
                },
                onOpenHistory = {
                    showProfileDrawer = false
                    onOpenHistory()
                },
                onStartNewScan = {
                    showProfileDrawer = false
                    onStartNewScan()
                },
            )
        }

        DoctorLoadingOverlay(visible = showHomeLoading)

        if (showSignOutConfirm) {
            AlertDialog(
                onDismissRequest = { showSignOutConfirm = false },
                title = {
                    Text(
                        text = stringResource(R.string.doctor_shell_sign_out),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.doctor_shell_sign_out_confirm),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSignOutConfirm = false
                            viewModel.signOut()
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.doctor_shell_yes),
                            color = NsStatusError,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSignOutConfirm = false }) {
                        Text(
                            text = stringResource(R.string.doctor_shell_cancel),
                            color = NeurostageBrandBlue,
                        )
                    }
                },
                containerColor = NsWhite,
                shape = RoundedCornerShape(20.dp),
            )
        }
    }
}

@Composable
private fun DoctorHomeHeader(
    initials: String,
    displayName: String?,
    onProfileClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(waveBottomShape)
            .background(NeurostageBrandBlue)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = stringResource(R.string.app_name),
                color = NsWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onProfileClick)
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NsWhite.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initials,
                        color = NsWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                    )
                }
                Text(
                    text = displayName ?: stringResource(R.string.doctor_shell_welcome),
                    modifier = Modifier.weight(1f),
                    color = NsWhite,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.doctor_shell_profile_title),
                    tint = NsWhite.copy(alpha = 0.85f),
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun DoctorStatsRow(
    patientCount: Int,
    scanCount: Int,
    scansThisWeek: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StatCard(
            value = patientCount.toString(),
            label = stringResource(R.string.doctor_shell_stat_patients),
            accent = NsDoctorAccentBlue,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            value = scanCount.toString(),
            label = stringResource(R.string.doctor_shell_stat_scans),
            accent = NeurostageBrandBlue,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            value = scansThisWeek.toString(),
            label = stringResource(R.string.doctor_shell_stat_week),
            accent = NsStatusSuccess,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NsWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = accent,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = NsGray600,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun ProfileDrawer(
    displayName: String?,
    email: String?,
    initialsFallback: String,
    onClose: () -> Unit,
    onSignOut: () -> Unit,
    onOpenPatients: () -> Unit,
    onOpenHistory: () -> Unit,
    onStartNewScan: () -> Unit,
) {
    val initials = (displayName ?: "")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifBlank { initialsFallback }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(270.dp),
        color = NsWhite,
        shadowElevation = 20.dp,
        shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .align(Alignment.TopStart)
                        .clip(waveBottomShape)
                        .background(NeurostageBrandBlue),
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NsWhite.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.doctor_shell_cd_close),
                                tint = NsWhite,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .align(Alignment.BottomCenter)
                        .clip(CircleShape)
                        .background(NsWhite),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(NeurostageBrandBlue),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = initials,
                            color = NsWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (displayName != null) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = NsGray900,
                    )
                }
                if (email != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = NsGray600,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                DrawerMenuItem(
                    icon = Icons.Outlined.People,
                    title = stringResource(R.string.doctor_drawer_patients),
                    isSelected = false,
                    onClick = onOpenPatients,
                )
                DrawerMenuItem(
                    icon = Icons.Outlined.History,
                    title = stringResource(R.string.doctor_drawer_history),
                    isSelected = false,
                    onClick = onOpenHistory,
                )
                DrawerMenuItem(
                    icon = Icons.Outlined.Upload,
                    title = stringResource(R.string.doctor_drawer_mri),
                    isSelected = false,
                    onClick = onStartNewScan,
                )
            }

            HorizontalDivider(
                color = NsGray300,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            DrawerMenuItem(
                icon = Icons.Outlined.Logout,
                title = stringResource(R.string.doctor_drawer_sign_out),
                isSelected = false,
                tint = NsStatusError,
                onClick = onSignOut,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean = false,
    tint: Color = NsGray700,
    onClick: () -> Unit = {},
) {
    val bgColor = if (isSelected) NeurostageBrandBlue.copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (isSelected) NeurostageBrandBlue else tint
    val iconTint = if (isSelected) NeurostageBrandBlue else tint

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
        )
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: Brush,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(NsWhite.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = NsWhite,
                    modifier = Modifier.size(26.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp),
            ) {
                Text(
                    title,
                    color = NsWhite,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    subtitle,
                    color = NsWhite.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = NsWhite.copy(alpha = 0.9f),
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun SmallActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NsWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(subtitle, color = NsGray400, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
