package com.duongnd.kytucxa.feature.splash

import com.duongnd.kytucxa.data.remote.dto.registration.draft.DraftResponse

sealed class SplashDestination {
    data object Idle : SplashDestination()
    data object CheckingNetwork : SplashDestination()
    data object CheckingPermissions : SplashDestination()
    data object Loading : SplashDestination()
    data object Login : SplashDestination()
    data object Home : SplashDestination()
    data object UpdateProfile : SplashDestination()
    data class Registration(val draft: DraftResponse? = null) : SplashDestination()
    data object OfflineInstructions : SplashDestination()
    data object Step1Residence : SplashDestination()
    data object Step2Temporary : SplashDestination()
    data object Step3Documents : SplashDestination()
    data object SubmitReady : SplashDestination()
    data class Pending(val registrationId: String?) : SplashDestination()
    data class RequiresSupplement(val registrationId: String?) : SplashDestination()
    data class Rejected(val reason: String?) : SplashDestination()
    data object NoInternet : SplashDestination()
    data object RequestPermissions : SplashDestination()
}