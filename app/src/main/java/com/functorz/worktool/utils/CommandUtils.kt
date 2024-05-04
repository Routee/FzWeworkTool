package com.functorz.worktool.utils

import com.apollographql.apollo.ApolloCall.Callback
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.functorz.worktool.Constant
import com.functorz.worktool.InsertCommandMutation
import com.functorz.worktool.model.WeworkMessageBean
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

        fun upload(message: WeworkMessageBean) {
            val client = getClient()
            GlobalScope.launch {
                val insertCommandMutation = InsertCommandMutation(Input.optional(GsonUtils.toJson(message)))
                client.mutate(insertCommandMutation).enqueue(object: Callback<InsertCommandMutation.Data>() {
                    override fun onResponse(response: Response<InsertCommandMutation.Data>) {
                        LogUtils.eTag("FzWorkTool", response.data?.insert_command_one?.content ?: "")
                    }

                    override fun onFailure(e: ApolloException) {
                        LogUtils.eTag("FzWorkTool", e.toString())
                    }
                })
            }
        }
    }
}
