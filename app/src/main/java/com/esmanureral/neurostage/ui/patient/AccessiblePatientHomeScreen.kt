package com.esmanureral.neurostage.ui.patient

import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.esmanureral.neurostage.R

@Composable
fun AccessiblePatientHomeScreen(
    onStartScan: () -> Unit,
    onBackToRolePick: () -> Unit,
    viewModel: PatientHomeViewModel = hiltViewModel(),
) {
    fun exitToRolePick() {
        viewModel.clearSession()
        onBackToRolePick()
    }

    Scaffold(
        containerColor = PatientColors.accessibleBackground,
        bottomBar = {
            AccessibleExitBottomBar(onExit = ::exitToRolePick)
        },
    ) { innerPadding ->
        AccessibleHomeContent(
            onStartScan = onStartScan,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(PatientDimens.accessibleScreenPadding),
        )
    }
}

@Composable
private fun AccessibleHomeContent(
    onStartScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AccessibleWelcomeHeader()
        Spacer(Modifier.height(PatientDimens.accessibleSectionSpacing))
        AccessibleScanInstruction()
        Spacer(Modifier.height(PatientDimens.accessibleSectionSpacing))
        AccessibleStartScanButton(onClick = onStartScan)
    }
}

@Composable
private fun AccessibleWelcomeHeader() {
    Text(
        text = stringResource(R.string.accessible_home_welcome),
        fontSize = PatientDimens.accessibleWelcomeTitleSize,
        fontWeight = FontWeight.Black,
        color = PatientColors.accessibleTextPrimary,
        textAlign = TextAlign.Center,
        lineHeight = PatientDimens.accessibleWelcomeLineHeight,
    )
}

@Composable
private fun AccessibleScanInstruction() {
    Text(
        text = stringResource(R.string.accessible_home_scan_instruction),
        fontSize = PatientDimens.accessibleInstructionSize,
        fontWeight = FontWeight.Bold,
        color = PatientColors.accessibleTextSecondary,
        textAlign = TextAlign.Center,
        lineHeight = PatientDimens.accessibleInstructionLineHeight,
    )
}

@Composable
private fun AccessibleStartScanButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = PatientColors.accessibleScanAction,
        ),
        shape = RoundedCornerShape(PatientDimens.accessibleScanButtonCorner),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = PatientDimens.accessibleScanButtonElevation,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(PatientDimens.accessibleScanButtonHeight),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = stringResource(R.string.accessible_cd_start_scan),
                modifier = Modifier.size(PatientDimens.accessibleScanIconSize),
                tint = PatientColors.onSurface,
            )
            Spacer(Modifier.height(PatientDimens.accessibleScanIconLabelGap))
            Text(
                text = stringResource(R.string.accessible_home_start_scan),
                fontSize = PatientDimens.accessibleScanLabelSize,
                fontWeight = FontWeight.Black,
                color = PatientColors.onSurface,
            )
        }
    }
}

@Composable
private fun AccessibleExitBottomBar(onExit: () -> Unit) {
    Button(
        onClick = onExit,
        colors = ButtonDefaults.buttonColors(
            containerColor = PatientColors.accessibleExitAction,
        ),
        shape = RoundedCornerShape(PatientDimens.cornerNone),
        modifier = Modifier
            .fillMaxWidth()
            .height(PatientDimens.accessibleExitBarHeight),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = stringResource(R.string.patient_home_cd_logout),
                modifier = Modifier.size(PatientDimens.accessibleExitIconSize),
                tint = PatientColors.onSurface,
            )
            Spacer(Modifier.width(PatientDimens.accessibleExitIconTextGap))
            Text(
                text = stringResource(R.string.accessible_home_exit),
                fontSize = PatientDimens.accessibleExitLabelSize,
                fontWeight = FontWeight.Black,
                color = PatientColors.onSurface,
            )
        }
    }
}
