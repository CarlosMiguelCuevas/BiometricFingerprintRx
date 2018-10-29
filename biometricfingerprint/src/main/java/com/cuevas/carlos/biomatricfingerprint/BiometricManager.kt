package com.cuevas.carlos.biomatricfingerprint

import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class BiometricManager(
    val context: Context,
    val title: String,
    val subtitle: String?,
    val description: String?,
    val negativeButtonText: String
) {

    fun authenticate(): Flowable<FingerprintResponse> {
        //TODO:here comes the validations to see if teh devise has sensor and all that stuff this is for prior PIE
        return displayBiometricDialog();
    }

    @TargetApi(Build.VERSION_CODES.P)
    private fun displayBiometricDialog(): Flowable<FingerprintResponse> {
        return Flowable.create({ fingerprintEventEmitter ->

            //TODO: here we have to check if it is PIE or grater, for now we are just addressing PIE
            BiometricPrompt.Builder(context)
                .setTitle(title)
                .apply {
                    subtitle?.let {
                        setSubtitle(subtitle)
                    }
                    description?.let {
                        setDescription(description)
                    }
                }
                .setNegativeButton(negativeButtonText, context.mainExecutor,
                    DialogInterface.OnClickListener { _, _ ->
                        fingerprintEventEmitter.onNext(FingerprintResponse.CANCELLED)
                    })
                .build()
                .authenticate(
                    CancellationSignal(),
                    context.mainExecutor,
                    object : BiometricPrompt.AuthenticationCallback() {

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            fingerprintEventEmitter.onNext(FingerprintResponse.SUCCESS)
                        }

                        override fun onAuthenticationHelp(
                            helpCode: Int,
                            helpString: CharSequence
                        ) {
                            super.onAuthenticationHelp(helpCode, helpString)
                            fingerprintEventEmitter.onNext(FingerprintResponse.HELP.apply {
                                code = helpCode
                                message = helpString
                            })

                        }

                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            fingerprintEventEmitter.onNext(FingerprintResponse.ERROR.apply {
                                code = errorCode
                                message = errString
                            })

                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            fingerprintEventEmitter.onNext(FingerprintResponse.FAILED)

                        }
                    })//TODO:Crypto im missing for now

        }, BackpressureStrategy.LATEST)
    }

    class BiometricBuilder(private var mContext: Context) {
        private lateinit var mTitle: String
        private var mSubtitle: String? = null
        private var mDescription: String? = null
        private lateinit var mNegativeButtonText: String

        fun setTitle(title: String): BiometricBuilder = apply { mTitle = title }

        fun setSubtitle(subtitle: String): BiometricBuilder = apply { mSubtitle = subtitle }

        fun setDescription(description: String): BiometricBuilder =
            apply { mDescription = description }

        fun setNegativeButtonText(negativeButtonText: String): BiometricBuilder =
            apply { mNegativeButtonText = negativeButtonText }

        fun build(): BiometricManager =
            BiometricManager(
                mContext,
                mTitle ?: error("title is required"),
                mSubtitle,
                mDescription,
                mNegativeButtonText ?: error("negative button is required")
            )
    }
}