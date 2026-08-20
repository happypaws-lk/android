package lk.happypaws.app.data.remote.api

import lk.happypaws.app.data.remote.model.AuthResponse
import lk.happypaws.app.data.remote.model.ChangePasswordRequest
import lk.happypaws.app.data.remote.model.ForgotPasswordRequest
import lk.happypaws.app.data.remote.model.LoginRequest
import lk.happypaws.app.data.remote.model.OtpRequest
import lk.happypaws.app.data.remote.model.OtpVerifyRequest
import lk.happypaws.app.data.remote.model.RefreshRequest
import lk.happypaws.app.data.remote.model.ResetPasswordRequest
import lk.happypaws.app.data.remote.model.RevokeRequest
import lk.happypaws.app.data.remote.model.SignUpCompleteRequest
import lk.happypaws.app.data.remote.model.SignUpVerifyCodeResponse
import lk.happypaws.app.data.remote.model.VerifyResetCodeRequest
import lk.happypaws.app.data.remote.model.VerifyResetCodeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @Headers("No-Authentication: true")
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @Headers("No-Authentication: true")
    @POST("api/v1/auth/signup/send-code")
    suspend fun sendOtp(
        @Body request: OtpRequest
    ): Response<Unit>

    @Headers("No-Authentication: true")
    @POST("api/v1/auth/signup/verify-code")
    suspend fun verifySignUpCode(
        @Body request: OtpVerifyRequest
    ): Response<SignUpVerifyCodeResponse>

    @Headers("No-Authentication: true")
    @POST("api/v1/auth/signup/complete")
    suspend fun completeSignUp(
        @Body request: SignUpCompleteRequest
    ): Response<AuthResponse>

    @Headers("No-Authentication: true")
    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<Unit>

    @Headers("No-Authentication: true")
    @POST("api/v1/auth/verify-reset-code")
    suspend fun verifyResetCode(
        @Body request: VerifyResetCodeRequest
    ): Response<VerifyResetCodeResponse>

    @Headers("No-Authentication: true")
    @POST("api/v1/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<Unit>

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshRequest
    ): Response<AuthResponse>

    @POST("api/v1/auth/revoke")
    suspend fun revokeToken(
        @Body request: RevokeRequest
    ): Response<Unit>

    @POST("api/v1/auth/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<Unit>

}
