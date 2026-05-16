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
import com.esmanureral.neurostage.ui.onboarding.RolePickViewModel
import com.esmanureral.neurostage.ui.patient.StageAwarePatientHomeScreen
import com.esmanureral.neurostage.ui.patient.games.GameHubScreen
import com.esmanureral.neurostage.ui.patient.games.MemoryGameScreen
import com.esmanureral.neurostage.ui.patient.games.memorymatch.MemoryMatchGameScreen
import com.esmanureral.neurostage.ui.patient.games.RoutineOrderGameScreen
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.ui.patient.navigation.BrainExerciseRouteGuard
import com.esmanureral.neurostage.ui.patient.navigation.MildHomePuzzleRouteGuard
import com.esmanureral.neurostage.ui.patient.navigation.MriModeratePuzzleRouteGuard
import com.esmanureral.neurostage.ui.patient.games.puzzle.PuzzleGameScreen
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleProgressTrack
import com.esmanureral.neurostage.ui.theme.NeuroStagePatientTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

@Composable
fun AppRoot() {
    val nav = rememberNavController()

    fun navigateToPatientHome() {
        nav.navigate(Routes.PATIENT_HOME) {
            popUpTo(Routes.PATIENT_HOME) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun popBackToPatientHome() {
        if (!nav.popBackStack(Routes.PATIENT_HOME, inclusive = false)) {
            navigateToPatientHome()
        }
    }

    fun navigateIfBrainExerciseEligible(stage: Int?, block: () -> Unit) {
        if (PatientStage.isBrainExerciseEligible(stage)) block()
    }

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
                    nav.navigate(Routes.PATIENT_SCAN) {
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
            NeuroStagePatientTheme {
                val stageVm: RolePickViewModel = hiltViewModel()
                val stage by stageVm.patientStage.collectAsStateWithLifecycle()

                val backToRolePick: () -> Unit = {
                    nav.navigate(Routes.ROLE_PICK) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                val startScan: () -> Unit = {
                    nav.navigate(Routes.PATIENT_SCAN) { launchSingleTop = true }
                }

                LaunchedEffect(stage) {
                    if (stage == null) {
                        nav.navigate(Routes.PATIENT_SCAN) {
                            popUpTo(Routes.PATIENT_HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                if (stage == null) {
                    Box(Modifier.fillMaxSize())
                } else {
                    StageAwarePatientHomeScreen(
                        onStartScan = startScan,
                        onOpenGames = {
                            navigateIfBrainExerciseEligible(stage) {
                                nav.navigate(Routes.PATIENT_GAMES) { launchSingleTop = true }
                            }
                        },
                        onStartRoutineGame = {
                            navigateIfBrainExerciseEligible(stage) {
                                nav.navigate(Routes.PATIENT_GAME_ROUTINE) { launchSingleTop = true }
                            }
                        },
                        onStartMemoryGame = {
                            navigateIfBrainExerciseEligible(stage) {
                                nav.navigate(Routes.PATIENT_GAME_MEMORY) { launchSingleTop = true }
                            }
                        },
                        onStartPuzzleGame = {
                            navigateIfBrainExerciseEligible(stage) {
                                nav.navigate(Routes.PATIENT_GAME_PUZZLE) { launchSingleTop = true }
                            }
                        },
                        onStartMemoryMatchGame = {
                            navigateIfBrainExerciseEligible(stage) {
                                nav.navigate(Routes.PATIENT_GAME_MEMORY_MATCH) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        onBackToRolePick = backToRolePick,
                    )
                }
            }
        }

        composable(Routes.PATIENT_SCAN) {
            val vm: RolePickViewModel = hiltViewModel()
            val stage by vm.patientStage.collectAsStateWithLifecycle()

            MainScreen(
                patientId = null,
                isPatient = true,
                onBack = {
                    nav.navigate(Routes.ROLE_PICK) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenGames = {
                    when (stage) {
                        PatientStage.MILD_DEMENTIA,
                        PatientStage.VERY_MILD_DEMENTIA,
                            -> {
                            nav.navigate(Routes.PATIENT_HOME) {
                                popUpTo(Routes.PATIENT_SCAN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        PatientStage.MODERATE_DEMENTIA -> {
                            nav.navigate(Routes.PATIENT_GAMES) {
                                popUpTo(Routes.PATIENT_SCAN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        else -> Unit
                    }
                },
            )
        }

        composable(Routes.PATIENT_GAMES) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                BrainExerciseRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    GameHubScreen(
                        stageIndex = stage,
                        onStartRoutineGame = { nav.navigate(Routes.PATIENT_GAME_ROUTINE) },
                        onStartMemoryGame = { nav.navigate(Routes.PATIENT_GAME_MEMORY) },
                        onStartPuzzleGame = {
                            if (stage == PatientStage.MODERATE_DEMENTIA) {
                                nav.navigate(Routes.PATIENT_GAME_PUZZLE_MRI_MODERATE)
                            } else {
                                nav.navigate(Routes.PATIENT_GAME_PUZZLE)
                            }
                        },
                        onStartMemoryMatchGame = {
                            if (PatientStage.isBrainExerciseEligible(stage)) {
                                nav.navigate(Routes.PATIENT_GAME_MEMORY_MATCH)
                            }
                        },
                        onBack = { nav.popBackStack() },
                    )
                }
            }
        }

        composable(Routes.PATIENT_GAME_ROUTINE) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                BrainExerciseRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    RoutineOrderGameScreen(
                        stageIndex = stage,
                        onBack = { nav.popBackStack() },
                    )
                }
            }
        }

        composable(Routes.PATIENT_GAME_MEMORY) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                BrainExerciseRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    MemoryGameScreen(
                        stageIndex = stage,
                        onBack = { nav.popBackStack() },
                    )
                }
            }
        }

        composable(Routes.PATIENT_GAME_MEMORY_MATCH) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                MildHomePuzzleRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    MemoryMatchGameScreen(
                        onBack = { nav.popBackStack() },
                    )
                }
            }
        }

        composable(Routes.PATIENT_GAME_PUZZLE) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                MildHomePuzzleRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    PuzzleGameScreen(
                        stageIndex = stage,
                        onBack = { nav.popBackStack() },
                        onBackToHome = { popBackToPatientHome() },
                    )
                }
            }
        }

        composable(Routes.PATIENT_GAME_PUZZLE_MRI_MODERATE) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                MriModeratePuzzleRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    PuzzleGameScreen(
                        stageIndex = stage,
                        onBack = { nav.popBackStack() },
                        onBackToHome = { popBackToPatientHome() },
                        progressTrack = PuzzleProgressTrack.MriModerateCatalog,
                    )
                }
            }
        }
    }
}