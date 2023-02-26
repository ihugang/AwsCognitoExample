package com.example.awscognitosignup
import android.util.Log
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.*
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AuthenticationResultType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.InitiateAuthRequest


class CognitoHelper {

    private val USER_POOL_ID = "ap-northeast-1_9ueKZdA3w"
    private val CLIENT_ID = "5c8ddr2qudu8gfdloq7nf2k2eo"
    private val IDENTITY_POOL_ID = "ap-northeast-1:f8b8737f-051d-45c1-bf61-53a39e85b68c"
    private val REGION_ID = "ap-northeast-1"

    enum class AwsCognitoSignUpResult {
        Success,
        Need_Verify_Code,
        Already_Registered
    }

    /**
     * 注册
     * @param userNameVal 用户昵称
     * @param passwordVal 密码
     * @param emailVal 电子邮件地址
     * @return <用户Id,状态（成功，需要验证码，已经存在的用户）>
     */
    suspend fun signUp(userNameVal: String?, passwordVal: String?, emailVal: String?) : Pair<String,AwsCognitoSignUpResult> {
        Log.d("register",".....")
        val userAttrs = AttributeType {
            name = "email"
            value = emailVal
        }

        val userNameAttrs = AttributeType {
            name = "nickname"
            value = userNameVal
        }

        val userAttrsList = mutableListOf<AttributeType>()
        userAttrsList.add(userAttrs)
        userAttrsList.add(userNameAttrs)

        val signUpRequest = SignUpRequest {
            userAttributes = userAttrsList
            username = emailVal
            clientId = CLIENT_ID
            password = passwordVal
        }

        var userid = ""

        CognitoIdentityProviderClient { region = REGION_ID}.use { identityProviderClient ->
           val result =
               try {
                   val r = identityProviderClient.signUp(signUpRequest)
                   userid = r.userSub.toString()
                   AwsCognitoSignUpResult.Success
               } catch (ex :CognitoIdentityProviderException) {
                   when (ex.sdkErrorMetadata.errorCode.toString()) {
                       "UsernameExistsException" -> AwsCognitoSignUpResult.Already_Registered
                       "InvalidParameterException" -> AwsCognitoSignUpResult.Need_Verify_Code
                       else -> throw ex
                   }
               }

            Log.d("register","signup result userid:${userid} state:${result}")
            return Pair(userid,result)
        }
    }

    /**
     * 确认注册，验证电子邮件验证码
     * @param codeVal 验证码
     * @param userNameVal 电子邮件地址
     * @return <成功失败，错误原因>
     */
    suspend fun confirmSignUp(codeVal: String?, userNameVal: String?) : Pair<Boolean,String> {
        Log.d("register","confirm.....${codeVal} ${userNameVal}")
        val signUpRequest = ConfirmSignUpRequest {
            clientId = CLIENT_ID
            confirmationCode = codeVal
            username = userNameVal
        }

        CognitoIdentityProviderClient { region = REGION_ID }.use { identityProviderClient ->
            val r = try {
                identityProviderClient.confirmSignUp(signUpRequest)
                Pair(true,"")
                } catch (ex :CognitoIdentityProviderException) {
                Pair(false,ex.message.toString())
                }
            Log.d("register","signup result state:${r}")
            return r
            }
    }

    /**
     * 重新发送验证码
     * @param userNameVal 电子邮件地址
     */
    suspend fun resendConfirmationCode(userNameVal: String?) {
        val codeRequest = ResendConfirmationCodeRequest {
            clientId = CLIENT_ID
            username = userNameVal
        }

        CognitoIdentityProviderClient { region = REGION_ID }.use { identityProviderClient ->
            val response = identityProviderClient.resendConfirmationCode(codeRequest)
            println("Method of delivery is " + (response.codeDeliveryDetails?.deliveryMedium))
        }
    }

    /**
     * 登录
     * @param userNameVal 电子邮件
     * @param passwordVal 密码
     * @return 成功/失败，成功的AccessToken / 失败的原因
     */
    suspend fun login(userNameVal: String, passwordVal: String): Pair<Boolean,String> {
        val cognito = CognitoIdentityProviderClient{ region = REGION_ID}

        val authParameters0: MutableMap<String, String> = HashMap()
        authParameters0["USERNAME"] = userNameVal // 用户名/邮箱
        authParameters0["PASSWORD"] = passwordVal // 密码

        val request = InitiateAuthRequest {
            authFlow = AuthFlowType.UserPasswordAuth // 认证流程
            clientId = CLIENT_ID // 用户池客户端ID
            authParameters = authParameters0
        }

        val response = cognito.initiateAuth(request)
        val result: AuthenticationResultType? = response.authenticationResult

        if (result != null) {
            println(result.accessToken) // 认证成功，返回认证结果中的访问令牌
            return Pair(true,result.accessToken.toString())
        } else {
            println(response.challengeName) // 返回需要进行其他步骤的认证挑战名称
            return Pair(true,response.challengeName.toString())
        }
    }
}
