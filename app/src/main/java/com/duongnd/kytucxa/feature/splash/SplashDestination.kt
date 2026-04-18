package com.duongnd.kytucxa.feature.splash

import com.duongnd.kytucxa.data.remote.dto.registration.current.CurrentResponse
import com.duongnd.kytucxa.data.remote.dto.registration.current.draft.DraftRegistrationDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.draft.DraftResponse

sealed class SplashDestination {
    data object Idle : SplashDestination()
    data object CheckingNetwork : SplashDestination()
    data object CheckingPermissions : SplashDestination()
    data object Loading : SplashDestination()
    data object Login : SplashDestination()
    data object Home : SplashDestination()
    data object UpdateProfile : SplashDestination()
    data class Registration(val  currentResponse: CurrentResponse? = null) : SplashDestination()
    data class Tracking(val currentResponse: CurrentResponse? = null) : SplashDestination()
    data object OfflineInstructions : SplashDestination()
    data class Step1Residence(val draft: DraftRegistrationDTO? = null) : SplashDestination()
    data class Step2Temporary(val draft: DraftRegistrationDTO? = null) : SplashDestination()
    data class Step3Documents(val draft: DraftRegistrationDTO? = null) : SplashDestination()
    data class SubmitReady(val draft: DraftRegistrationDTO? = null) : SplashDestination()
    data class Pending(val registrationId: String?) : SplashDestination()
    data class RequiresSupplement(val registrationId: String?) : SplashDestination()
    data class Rejected(val reason: String?) : SplashDestination()
    data object NoInternet : SplashDestination()
    data object RequestPermissions : SplashDestination()
}
