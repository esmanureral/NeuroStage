package com.esmanureral.neurostage.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.esmanureral.neurostage.navigation.clearUnchangedScanResult
import com.esmanureral.neurostage.navigation.HubScanNav
import com.esmanureral.neurostage.navigation.publishUnchangedScanResult
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
import com.esmanureral.neurostage.ui.patient.PatientHomeViewModel
import com.esmanureral.neurostage.ui.patient.StageAwarePatientHomeScreen
import com.esmanureral.neurostage.ui.patient.games.ExerciseListScreen
import com.esmanureral.neurostage.ui.patient.hub.HubUnchangedScanResultSheetHost
import com.esmanureral.neurostage.ui.patient.hub.PatientProgramHubScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.esmanureral.neurostage.ui.patient.reminders.PatientRemindersScreen
import com.esmanureral.neurostage.ui.patient.scan.PatientScanDetailScreen
import com.esmanureral.neurostage.ui.patient.scan.PatientScanHistoryScreen
import com.esmanureral.neurostage.ui.patient.games.colormatch.ColorMatchGameScreen
import com.esmanureral.neurostage.ui.patient.games.MemoryGameScreen
import com.esmanureral.neurostage.ui.patient.games.memorymatch.MemoryMatchGameScreen
import com.esmanureral.neurostage.ui.patient.games.RoutineOrderGameScreen
import com.esmanureral.neurostage.domain.patient.PatientStage
import com.esmanureral.neurostage.ui.patient.navigation.BrainExerciseRouteGuard
import com.esmanureral.neurostage.ui.patient.navigation.ColorMatchRouteGuard
import com.esmanureral.neurostage.ui.patient.navigation.ReminderRouteGuard
import com.esmanureral.neurostage.ui.patient.navigation.MemoryMatchRouteGuard
import com.esmanureral.neurostage.ui.patient.navigation.MildHomePuzzleRouteGuard
import com.esmanureral.neurostage.ui.patient.navigation.MriModeratePuzzleRouteGuard
import com.esmanureral.neurostage.ui.patient.games.puzzle.PuzzleGameScreen
import com.esmanureral.neurostage.ui.patient.puzzle.core.PuzzleProgressTrack
import com.esmanureral.neurostage.ui.theme.NeuroStagePatientTheme

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

    fun popBackToExerciseList(): Boolean =
        nav.popBackStack(Routes.PATIENT_EXERCISE_LIST, inclusive = false)

    fun popBackToExerciseHub(): Boolean =
        popBackToExerciseList() || nav.popBackStack(Routes.PATIENT_GAMES, inclusive = false)

    fun navigateToExerciseHub() {
        nav.navigate(Routes.PATIENT_GAMES) {
            launchSingleTop = true
        }
    }

    fun exitToRolePick() {
        nav.navigate(Routes.ROLE_PICK) {
            popUpTo(nav.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun doctorPopBack() {
        if (!nav.popBackStack()) {
            nav.navigate(Routes.DOCTOR_HOME) {
                launchSingleTop = true
            }
        }
    }

    fun gameScreenBack() {
        if (!nav.popBackStack()) {
            navigateToExerciseHub()
        }
    }

    fun programHubBack(stage: Int?) {
        if (PatientStage.usesExerciseHubAsHome(stage)) {
            exitToRolePick()
        } else if (!nav.popBackStack()) {
            popBackToPatientHome()
        }
    }

    fun exitPatientHubToRolePick(clearSession: () -> Unit) {
        exitToRolePick()
        clearSession()
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
                    exitToRolePick()
                },
            )
        }

        composable(Routes.DOCTOR_PATIENTS) {
            PatientListScreen(
                onBack = { doctorPopBack() },
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
                onBack = { doctorPopBack() },
                onStartScan = { patientId ->
                    nav.navigate("${Routes.DOCTOR_SCAN}/$patientId")
                },
            )
        }

        composable(Routes.DOCTOR_PATIENT_INTAKE) {
            PatientIntakeScreen(
                onBack = { doctorPopBack() },
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
            MainScreen(patientId = patientId, onBack = { doctorPopBack() })
        }

        composable(Routes.DOCTOR_HISTORY) {
            DoctorHistoryScreen(
                onBack = { doctorPopBack() },
                onOpenDetail = { ts ->
                    nav.navigate("${Routes.DOCTOR_RESULT_DETAIL}/$ts")
                },
            )
        }

        composable(
            route = "${Routes.DOCTOR_RESULT_DETAIL}/{${RouteArgs.SCAN_TS}}",
            arguments = listOf(navArgument(RouteArgs.SCAN_TS) { type = NavType.LongType }),
        ) {
            DoctorResultDetailScreen(onBack = { doctorPopBack() })
        }

        composable(Routes.PATIENT_HOME) {
            NeuroStagePatientTheme {
                val stageVm: RolePickViewModel = hiltViewModel()
                val stage by stageVm.patientStage.collectAsStateWithLifecycle()

                val backToRolePick: () -> Unit = {
                    exitToRolePick()
                }

                val startScan: () -> Unit = {
                    nav.navigate(Routes.PATIENT_SCAN) { launchSingleTop = true }
                }

                LaunchedEffect(stage) {
                    when {
                        stage == null -> {
                            nav.navigate(Routes.PATIENT_SCAN) {
                                popUpTo(Routes.PATIENT_HOME) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        PatientStage.usesExerciseHubAsHome(stage) -> {
                            nav.navigate(Routes.PATIENT_GAMES) {
                                popUpTo(Routes.PATIENT_HOME) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }

                if (stage == null) {
                    Box(Modifier.fillMaxSize())
                } else {
                    StageAwarePatientHomeScreen(
                        onStartScan = startScan,
                        onOpenGames = {
                            if (PatientStage.canAccessPatientExerciseHub(stage)) {
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

        composable(Routes.PATIENT_SCAN_HISTORY) {
            NeuroStagePatientTheme {
                PatientScanHistoryScreen(
                    onBack = { nav.popBackStack() },
                    onOpenDetail = { ts ->
                        nav.navigate("${Routes.PATIENT_SCAN_DETAIL}/$ts")
                    },
                )
            }
        }

        composable(
            route = "${Routes.PATIENT_SCAN_DETAIL}/{${RouteArgs.SCAN_TS}}",
            arguments = listOf(navArgument(RouteArgs.SCAN_TS) { type = NavType.LongType }),
        ) {
            NeuroStagePatientTheme {
                PatientScanDetailScreen(onBack = { nav.popBackStack() })
            }
        }

        composable(
            route = Routes.PATIENT_SCAN,
            popExitTransition = { ExitTransition.None },
        ) {
            val vm: RolePickViewModel = hiltViewModel()
            val stage by vm.patientStage.collectAsStateWithLifecycle()
            val hubEntry = nav.previousBackStackEntry
                ?.takeIf { entry ->
                    entry.destination.route == Routes.PATIENT_GAMES &&
                        entry.savedStateHandle.get<Boolean>(HubScanNav.RETURN_TO_HUB) == true
                }
            val returnToHub = hubEntry != null
            val stageBeforeScan = hubEntry?.savedStateHandle?.get<Int>(HubScanNav.STAGE_BEFORE_SCAN)

            fun clearHubScanFlags() {
                hubEntry?.savedStateHandle?.remove<Boolean>(HubScanNav.RETURN_TO_HUB)
                hubEntry?.savedStateHandle?.remove<Int>(HubScanNav.STAGE_BEFORE_SCAN)
            }

            MainScreen(
                patientId = null,
                isPatient = true,
                returnToHub = returnToHub,
                stageBeforeScan = stageBeforeScan,
                onHubUnchangedResult = hubEntry?.let { hub ->
                    { stageIndex, confidence, scores ->
                        hub.savedStateHandle.publishUnchangedScanResult(
                            stageIndex = stageIndex,
                            confidence = confidence,
                            scores = scores,
                        )
                        clearHubScanFlags()
                        nav.popBackStack()
                    }
                },
                onBack = {
                    if (hubEntry != null) {
                        clearHubScanFlags()
                        nav.popBackStack()
                    } else if (!nav.popBackStack()) {
                        nav.navigate(Routes.ROLE_PICK) {
                            popUpTo(Routes.PATIENT_SCAN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onOpenGames = {
                    when (stage) {
                        PatientStage.MILD_DEMENTIA,
                        PatientStage.VERY_MILD_DEMENTIA,
                        PatientStage.MODERATE_DEMENTIA,
                            -> {
                            if (hubEntry != null) {
                                clearHubScanFlags()
                                nav.popBackStack()
                            } else {
                                nav.navigate(Routes.PATIENT_GAMES) {
                                    popUpTo(Routes.PATIENT_SCAN) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }

                        else -> {
                            if (hubEntry != null) {
                                clearHubScanFlags()
                                nav.popBackStack()
                            }
                        }
                    }
                },
            )
        }

        composable(
            route = Routes.PATIENT_GAMES,
            popEnterTransition = { EnterTransition.None },
        ) { hubEntry ->
            NeuroStagePatientTheme {
                val roleVm: RolePickViewModel = hiltViewModel()
                val homeVm: PatientHomeViewModel = hiltViewModel()
                val stage by roleVm.patientStage.collectAsStateWithLifecycle()
                val startHubScan: () -> Unit = {
                    hubEntry.savedStateHandle.apply {
                        set(HubScanNav.RETURN_TO_HUB, true)
                        set(HubScanNav.STAGE_BEFORE_SCAN, stage)
                    }
                    nav.navigate(Routes.PATIENT_SCAN) { launchSingleTop = true }
                }
                BrainExerciseRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    Box(Modifier.fillMaxSize()) {
                        PatientProgramHubScreen(
                            onOpenExercises = {
                                nav.navigate(Routes.PATIENT_EXERCISE_LIST) { launchSingleTop = true }
                            },
                            onOpenReminders = {
                                nav.navigate(Routes.PATIENT_REMINDERS) { launchSingleTop = true }
                            },
                            onStartNewScan = startHubScan,
                            onBack = { programHubBack(stage) },
                            onExitToRolePick = {
                                hubEntry.savedStateHandle.clearUnchangedScanResult()
                                exitPatientHubToRolePick(homeVm::clearSession)
                            },
                            onDismissOverlay = {
                                hubEntry.savedStateHandle.clearUnchangedScanResult()
                            },
                        )
                        HubUnchangedScanResultSheetHost(
                            hubBackStackEntry = hubEntry,
                            onStartNewScan = startHubScan,
                        )
                    }
                }
            }
        }

        composable(Routes.PATIENT_EXERCISE_LIST) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                BrainExerciseRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    ExerciseListScreen(
                        stageIndex = stage,
                        onStartPuzzleGame = {
                            if (stage == PatientStage.MODERATE_DEMENTIA) {
                                nav.navigate(Routes.PATIENT_GAME_PUZZLE_MRI_MODERATE)
                            } else {
                                nav.navigate(Routes.PATIENT_GAME_PUZZLE)
                            }
                        },
                        onStartMemoryMatchGame = {
                            nav.navigate(Routes.PATIENT_GAME_MEMORY_MATCH)
                        },
                        onStartColorMatchGame = {
                            nav.navigate(Routes.PATIENT_GAME_COLOR_MATCH)
                        },
                        onBack = { nav.popBackStack() },
                    )
                }
            }
        }

        composable(Routes.PATIENT_REMINDERS) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                ReminderRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    PatientRemindersScreen(onBack = { nav.popBackStack() })
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
                        onBack = { gameScreenBack() },
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
                        onBack = { gameScreenBack() },
                    )
                }
            }
        }

        composable(Routes.PATIENT_GAME_MEMORY_MATCH) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                MemoryMatchRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    MemoryMatchGameScreen(
                        stageIndex = stage,
                        onBack = { gameScreenBack() },
                    )
                }
            }
        }

        composable(Routes.PATIENT_GAME_COLOR_MATCH) {
            NeuroStagePatientTheme {
                val vm: RolePickViewModel = hiltViewModel()
                val stage by vm.patientStage.collectAsStateWithLifecycle()
                ColorMatchRouteGuard(
                    stageIndex = stage,
                    onBlocked = { nav.popBackStack() },
                ) {
                    ColorMatchGameScreen(
                        stageIndex = stage,
                        onBack = { gameScreenBack() },
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
                        onBack = { gameScreenBack() },
                        onBackToHome = { gameScreenBack() },
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
                        onBack = { gameScreenBack() },
                        onBackToHome = { gameScreenBack() },
                        progressTrack = PuzzleProgressTrack.MriModerateCatalog,
                    )
                }
            }
        }
    }
}