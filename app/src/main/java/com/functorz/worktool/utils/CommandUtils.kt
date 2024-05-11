package com.functorz.worktool.utils

import com.apollographql.apollo.ApolloCall.Callback
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.functorz.worktool.CommandSubscription
import com.functorz.worktool.Constant
import com.functorz.worktool.InsertCommandMutation
import com.functorz.worktool.service.MyLooper
import com.functorz.worktool.service.error
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.lang.Exception

class CommandUtils {
    companion object {
        val cachedClient: Map<String, ApolloClient> = mapOf()

        private fun newClient(): ApolloClient {
            val client = ApolloClient.builder().serverUrl(Constant.gqlUrl).build()
            cachedClient.plus(Pair(Constant.gqlUrl, client))
            return client
        }

        private fun getClient(): ApolloClient {
            return cachedClient.get(Constant.gqlUrl) ?: newClient()
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun upload(message: String) {
            val client = getClient()
            GlobalScope.launch {
                val insertCommandMutation = InsertCommandMutation(Input.optional(message))
                client.mutate(insertCommandMutation)
                    .enqueue(object : Callback<InsertCommandMutation.Data>() {
                        override fun onResponse(response: Response<InsertCommandMutation.Data>) {
                            LogUtils.dTag(
                                "FzWorkTool",
                                " insert command response:" + response.data?.insert_command_one?.content ?: ""
                            )
                        }

                        override fun onFailure(e: ApolloException) {
                            LogUtils.eTag("FzWorkTool ","insert command failed:" + e.toString())
                        }
                    })
            }
        }

        @OptIn(
            InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class,
            DelicateCoroutinesApi::class
        )
        fun subscribeCommand() {
            GlobalScope.launch {
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(AuthorizationInterceptor())
                    .build()
                val gqlSubscriptionUrl = Constant.getGqlSubscriptionUrl()
                val apolloClient = ApolloClient.builder()
                    .serverUrl(Constant.gqlUrl)
                    .subscriptionTransportFactory(
                        WebSocketSubscriptionTransport.Factory(
                            gqlSubscriptionUrl,
                            okHttpClient
                        )
                    ).okHttpClient(okHttpClient)
                    .build()
                apolloClient.subscribe(CommandSubscription()).toFlow()
                    .retryWhen { cause, attempt ->
                        delay(attempt * 1000)
                        LogUtils.eTag(
                            "FzWorkTool",
                            "subscribe failed:" + cause.message.plus("\r\n").plus(cause.cause?.message).plus("\r\n")
                                .plus("attempt = ${attempt}")
                        )
                        ToastUtils.showShort("${attempt}s后重试")
                        true
                    }
                    .collect(object : FlowCollector<Response<CommandSubscription.Data>> {
                        override suspend fun emit(value: Response<CommandSubscription.Data>) {
                            try {
                                val content = value.data?.command?.last()?.content
                                LogUtils.dTag("FzWorkTool", " received message: $content")
                                MyLooper.onMessage(GsonUtils.toJson(content))
                            } catch (e: Exception) {
                                LogUtils.eTag("FzWorkTool", "received message wagith error:$e")
                                error(e.message)
                            }
                        }
                    })
            }
        }
    }
}
