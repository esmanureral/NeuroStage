package com.esmanureral.neurostage.ui.doctor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.auth.AuthStatus
import com.esmanureral.neurostage.ui.theme.NeurostageBrandBlue
import com.esmanureral.neurostage.ui.theme.NsDoctorLoginFieldBgIdle
import com.esmanureral.neurostage.ui.theme.NsDoctorLoginFieldBorderIdle
import com.esmanureral.neurostage.ui.theme.NsDoctorLoginTrackBg
import com.esmanureral.neurostage.ui.theme.NsStatusError
import com.esmanureral.neurostage.ui.theme.NsStatusSuccess
import com.esmanureral.neurostage.ui.theme.NsTextMid

@Composable
fun DoctorLoginScreen(
    onAuthed: () -> Unit,
    onBackToRolePick: () -> Unit,
    viewModel: DoctorAuthViewModel = hiltViewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val status by viewModel.authStatus.collectAsStateWithLifecycle()
    val loadingContentDescription = stringResource(R.string.doctor_login_loading_cd)

    BackHandler(onBack = onBackToRolePick)

    LaunchedEffect(Unit) {
        viewModel.pickWorldDoctor()
    }

    LaunchedEffect(status, ui.justSignedUp) {
        if (status is AuthStatus.SignedIn && !ui.justSignedUp) onAuthed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeurostageBrandBlue),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    text = if (ui.isSignUp) {
                        stringResource(R.string.doctor_login_headline_sign_up)
                    } else {
                        stringResource(R.string.doctor_login_headline_sign_in)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (ui.isSignUp) {
                        stringResource(R.string.doctor_login_subtitle_sign_up)
                    } else {
                        stringResource(R.string.doctor_login_subtitle_sign_in)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(NsDoctorLoginTrackBg, RoundedCornerShape(28.dp))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (!ui.isSignUp) Color.White else Color.Transparent)
                                .clickable { if (ui.isSignUp) viewModel.toggleMode() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.doctor_login_tab_sign_in),
                                fontWeight = if (!ui.isSignUp) FontWeight.Bold else FontWeight.Normal,
                                color = if (!ui.isSignUp) NeurostageBrandBlue else NsTextMid,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (ui.isSignUp) Color.White else Color.Transparent)
                                .clickable { if (!ui.isSignUp) viewModel.toggleMode() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.doctor_login_tab_sign_up),
                                fontWeight = if (ui.isSignUp) FontWeight.Bold else FontWeight.Normal,
                                color = if (ui.isSignUp) NeurostageBrandBlue else NsTextMid,
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    AnimatedVisibility(
                        visible = ui.isSignUp,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CustomTextField(
                                value = ui.firstName,
                                onValueChange = viewModel::onFirstNameChange,
                                label = stringResource(R.string.doctor_login_label_first_name),
                                modifier = Modifier.weight(1f),
                            )
                            CustomTextField(
                                value = ui.lastName,
                                onValueChange = viewModel::onLastNameChange,
                                label = stringResource(R.string.doctor_login_label_last_name),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    if (ui.isSignUp) Spacer(Modifier.height(16.dp))

                    CustomTextField(
                        value = ui.email,
                        onValueChange = viewModel::onEmailChange,
                        label = stringResource(R.string.doctor_login_label_email),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(16.dp))

                    CustomTextField(
                        value = ui.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = stringResource(R.string.doctor_login_label_password),
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(12.dp))

                    ui.info?.let { msg ->
                        Text(
                            text = msg,
                            color = NsStatusSuccess,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                    ui.error?.let { msg ->
                        Text(
                            text = msg,
                            color = NsStatusError,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::signIn,
                        enabled = !ui.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeurostageBrandBlue),
                    ) {
                        if (ui.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .height(24.dp)
                                    .semantics {
                                        contentDescription = loadingContentDescription
                                    },
                            )
                        } else {
                            Text(
                                text = if (ui.isSignUp) {
                                    stringResource(R.string.doctor_login_button_sign_up)
                                } else {
                                    stringResource(R.string.doctor_login_button_sign_in)
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (ui.isSignUp) {
                                stringResource(R.string.doctor_login_footer_has_account)
                            } else {
                                stringResource(R.string.doctor_login_footer_no_account)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = NsTextMid,
                        )
                        Text(
                            text = if (ui.isSignUp) {
                                stringResource(R.string.doctor_login_tab_sign_in)
                            } else {
                                stringResource(R.string.doctor_login_tab_sign_up)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeurostageBrandBlue,
                            modifier = Modifier.clickable { viewModel.toggleMode() },
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.doctor_login_back_to_role_pick),
                        color = NeurostageBrandBlue,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable(onClick = onBackToRolePick)
                            .padding(8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPassword) {
                {
                    val showPwd = stringResource(R.string.doctor_login_cd_password_show)
                    val hidePwd = stringResource(R.string.doctor_login_cd_password_hide)
                    val image =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) hidePwd else showPwd
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = description,
                            tint = NsTextMid
                        )
                    }
                }
            } else {
                null
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = NsDoctorLoginFieldBorderIdle,
                focusedBorderColor = NeurostageBrandBlue,
                unfocusedContainerColor = NsDoctorLoginFieldBgIdle,
                focusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = NeurostageBrandBlue,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}