package com.esmanureral.neurostage.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.esmanureral.neurostage.navigation.RouteArgs
import com.esmanureral.neurostage.navigation.Routes
import com.esmanureral.neurostage.ui.doctor.DoctorHistoryScreen
import com.esmanureral.neurostage.ui.doctor.DoctorLoginScreen
import com.esmanureral.neurostage.ui.doctor.DoctorResultDetailScreen
import com.esmanureral.neurostage.ui.doctor.DoctorShellScreen
import com.esmanureral.neurostage.ui.doctor.PatientHistoryScreen
import com.esmanureral.neurostage.ui.doctor.PatientIntakeScreen
import com.esmanureral.neurostage.ui.doctor.PatientListScreen
import com.esmanureral.neurostage.ui.onboarding.RoleGateViewModel
import com.esmanureral.neurostage.ui.onboarding.RolePickScreen
import com.esmanureral.neurostage.ui.patient.PatientHomeScreen

@Composable
fun AppRoot() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.ROLE_GATE,
    ) {
        composable(Routes.ROLE_GATE) {
            val vm: RoleGateViewModel = hiltViewModel()
            val dest by vm.startDestination.collectAsStateWithLifecycle()

            LaunchedEffect(dest) {
                dest?.let { d ->
                    nav.navigate(d) {
                        popUpTo(Routes.ROLE_GATE) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }

        composable(Routes.ROLE_PICK) {
            RolePickScreen(
                onPickDoctor = {
                    nav.navigate(Routes.DOCTOR_LOGIN) {
                        popUpTo(Routes.ROLE_PICK) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onPickPatient = {
                    nav.navigate(Routes.PATIENT_HOME) {
                        popUpTo(Routes.ROLE_PICK) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.DOCTOR_LOGIN) {
            DoctorLoginScreen(
                onAuthed = {
                    nav.navigate(Routes.DOCTOR_HOME) {
                        popUpTo(Routes.DOCTOR_LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToRolePick = {
                    nav.navigate(Routes.ROLE_PICK) {
                        popUpTo(Routes.DOCTOR_LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.DOCTOR_HOME) {
            DoctorShellScreen(
                onOpenHistory = { nav.navigate(Routes.DOCTOR_HISTORY) },
                onStartNewScan = { nav.navigate(Routes.DOCTOR_PATIENT_INTAKE) },
                onOpenPatients = { nav.navigate(Routes.DOCTOR_PATIENTS) },
                onSignedOut = {
                    nav.navigate(Routes.ROLE_PICK) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.DOCTOR_PATIENTS) {
            PatientListScreen(
                onBack = { nav.popBackStack() },
                onPickPatient = { patientId ->
                    nav.navigate("${Routes.DOCTOR_PATIENT_HISTORY}/$patientId")
                },
            )
        }

        composable(
            route = "${Routes.DOCTOR_PATIENT_HISTORY}/{${RouteArgs.PATIENT_ID}}",
            arguments = listOf(navArgument(RouteArgs.PATIENT_ID) { type = NavType.StringType }),
        ) {
            PatientHistoryScreen(
                onBack = { nav.popBackStack() },
                onStartScan = { patientId ->
                    nav.navigate("${Routes.DOCTOR_SCAN}/$patientId")
                },
            )
        }

        composable(Routes.DOCTOR_PATIENT_INTAKE) {
            PatientIntakeScreen(
                onSaved = { patientId ->
                    nav.navigate("${Routes.DOCTOR_SCAN}/$patientId") {
                        popUpTo(Routes.DOCTOR_PATIENT_INTAKE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = "${Routes.DOCTOR_SCAN}/{${RouteArgs.PATIENT_ID}}",
            arguments = listOf(navArgument(RouteArgs.PATIENT_ID) { type = NavType.StringType }),
        ) { backStack ->
            val patientId = backStack.arguments?.getString(RouteArgs.PATIENT_ID)
            MainScreen(patientId = patientId, onBack = { nav.popBackStack() })
        }

        composable(Routes.DOCTOR_HISTORY) {
            DoctorHistoryScreen(
                onBack = { nav.popBackStack() },
                onOpenDetail = { ts ->
                    nav.navigate("${Routes.DOCTOR_RESULT_DETAIL}/$ts")
                },
            )
        }

        composable(
            route = "${Routes.DOCTOR_RESULT_DETAIL}/{${RouteArgs.SCAN_TS}}",
            arguments = listOf(navArgument(RouteArgs.SCAN_TS) { type = NavType.LongType }),
        ) {
            DoctorResultDetailScreen(onBack = { nav.popBackStack() })
        }

        composable(Routes.PATIENT_HOME) {
            PatientHomeScreen(
                onBackToRolePick = {
                    nav.navigate(Routes.ROLE_PICK) {
                        popUpTo(Routes.PATIENT_HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}