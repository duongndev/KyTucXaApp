package com.duongnd.kytucxa.core.navigations.graph.registration

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.duongnd.kytucxa.core.navigations.Graphs
import com.duongnd.kytucxa.core.navigations.Screen
import com.duongnd.kytucxa.feature.registration.temporary.TemporaryFormScreen
import com.duongnd.kytucxa.feature.registration.temporary.TemporaryViewModel
import com.duongnd.kytucxa.feature.registration.uploadDocument.DocumentUploadScreen
import com.duongnd.kytucxa.feature.registration.HtmlPreviewScreen
import com.duongnd.kytucxa.feature.registration.RegistrationConfirmScreen
import com.duongnd.kytucxa.feature.registration.RegistrationFlowScreen
import com.duongnd.kytucxa.feature.registration.RegistrationViewModel
import com.duongnd.kytucxa.feature.registration.residence.ResidenceFormScreen
import com.duongnd.kytucxa.feature.registration.residence.ResidenceViewModel
import com.duongnd.kytucxa.feature.registration.submissionMethod.DirectSubmissionScreen
import com.duongnd.kytucxa.feature.registration.submissionMethod.SubmissionMethodScreen
import com.duongnd.kytucxa.feature.signature.SignatureScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

fun NavGraphBuilder.registrationNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SubmissionMethod.route,
        route = Graphs.REGISTRATION
    ) {
        composable(Screen.SubmissionMethod.route) { entry ->
            val viewModel = hiltViewModel<RegistrationViewModel>(entry)
            SubmissionMethodScreen(
                onOnlineSelected = {
                    navController.navigate(Screen.RegistrationForm.route)
                },
                onDirectSelected = {
                    navController.navigate(Screen.DirectSubmissionGuide.route)
                },
                onContinueExistingForm = { formId ->
                    // Bạn có thể truyền formId qua SavedStateHandle hoặc điều hướng thẳng
                    navController.navigate(Screen.RegistrationForm.route)
                },
                onExit = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.DirectSubmissionGuide.route) {
            DirectSubmissionScreen(
                onBack = {
                    navController.popBackStack()
                },
                onConfirm = {
                    navController.navigate(Screen.RegistrationForm.route)
                }
            )
        }

        composable(Screen.RegistrationForm.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val viewModel = hiltViewModel<ResidenceViewModel>(parentEntry)
            ResidenceFormScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Screen.TemporaryForm.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TemporaryForm.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val viewModel = hiltViewModel<TemporaryViewModel>(parentEntry)
            TemporaryFormScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Screen.DocumentUpload.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DocumentUpload.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val viewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
            DocumentUploadScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Screen.RegistrationConfirm.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RegistrationConfirm.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val viewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
            val draft = viewModel.draft.collectAsState().value
            val formId = draft?.registrationForm?.id ?: ""

            RegistrationConfirmScreen(
                viewModel = viewModel,
                onViewResidenceDetail = {
                    navController.navigate(Screen.HtmlPreview.createRoute(formId, "residence"))
                },
                onViewTemporaryDetail = {
                    navController.navigate(Screen.HtmlPreview.createRoute(formId, "temporary"))
                },
                onViewDocumentsDetail = { /* Điều hướng preview giấy tờ */ },
                onOpenSignature = { navController.navigate(Screen.Signature.route) },
                onBack = { navController.popBackStack() },
                onSuccess = { navController.navigate(Screen.RegistrationFlow.route) }
            )
        }

        composable(
            route = Screen.HtmlPreview.route,
            arguments = listOf(
                navArgument("formId") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val viewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
            val formId = entry.arguments?.getString("formId") ?: ""
            val type = entry.arguments?.getString("type") ?: ""

            HtmlPreviewScreen(
                viewModel = viewModel,
                formId = formId,
                type = type,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Signature.route) { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(Graphs.REGISTRATION)
            }
            val viewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
            SignatureScreen(
                onConfirm = { bitmap ->
                    viewModel.updateSignature(bitmap)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RegistrationFlow.route) {
            RegistrationFlowScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
