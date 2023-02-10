package com.example.adv_test

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails

class Analytic {
    companion object {

        private lateinit var referrerClient: InstallReferrerClient

        fun checkWhereAppFrom(context: Context): HashMap<String, String>? {

            referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {

                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            // Connection established.
                        }
                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            // API not available on the current Play Store app.
                        }
                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            // Connection couldn't be established.
                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })

            val response: ReferrerDetails = referrerClient.installReferrer
            val referrerUrl: String = response.installReferrer

//            var referrerUrl = "https://play.google.com/store/apps/details?id=com.my.project&referrer=utm_source%3Dcompanyasbd%26utm_medium%3Dcqwfwefpc%26utm_term%3Dteswefweft%26utm_content%3Dfwefw1fwew%26utm_campaign%3Dapwefwefwefp%26anid%3Dadmob"
            val hashMap: HashMap<String, String> = HashMap()
            val source = """(utm_source)%3D(.*)%26""".toRegex()
            val medium = """(utm_medium)%3D(.*)%26""".toRegex()
            val campaign = """(utm_campaign)%3D(.*)%26""".toRegex()
            val content = """(utm_content)%3D(.*)%26""".toRegex()
            val term = """(utm_term)%3D(.*)%26""".toRegex()
            val array = arrayListOf<Regex>(source, medium, campaign, content, term)
            for(reg in array){
                val matchResult = reg.find(referrerUrl)
                if(matchResult != null){
                    var (utm, meaning) = matchResult!!.destructured
                    if(meaning.contains("%26"))
                        meaning = meaning.substring(0, meaning.indexOf("%26"))
                    hashMap.put(utm, meaning)
                }
            }
            return hashMap
        }
    }
}