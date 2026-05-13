package com.esmanureral.neurostage.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.NeurostageBrandBlue
import com.esmanureral.neurostage.ui.theme.NsWhite

@Composable
fun RolePickScreen(
    onPickDoctor: () -> Unit,
    onPickPatient: () -> Unit,
    viewModel: RolePickViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.clear()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeurostageBrandBlue),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.role_pick_title),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = NsWhite,
                letterSpacing = 2.sp,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.role_pick_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = NsWhite.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
            )

            Spacer(Modifier.height(64.dp))

            Button(
                onClick = {
                    viewModel.pickDoctor()
                    onPickDoctor()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NsWhite),
            ) {
                Text(
                    text = stringResource(R.string.role_pick_doctor_button),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeurostageBrandBlue,
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    viewModel.pickPatient()
                    onPickPatient()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, NsWhite.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NsWhite),
            ) {
                Text(
                    text = stringResource(R.string.role_pick_patient_button),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}