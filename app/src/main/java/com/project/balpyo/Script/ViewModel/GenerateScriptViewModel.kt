package com.project.balpyo.Script.ViewModel

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.balpyo.LoadingFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ScriptResultFragment
import com.project.balpyo.Script.ScriptTimeFragment
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.GenerateScriptRequest
import com.project.balpyo.api.response.GenerateScriptResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerateScriptViewModel() : ViewModel() {
    var script = MutableLiveData<String>()
    var gptId = MutableLiveData<String>()

    fun generateScript(fragment: Fragment, mainActivity: MainActivity) {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        var inputScriptInfo = GenerateScriptRequest(MyApplication.scriptSubject, MyApplication.scriptSubtopic, MyApplication.scrpitTime, "1234", "false")
        Log.d("##", "scripit info : ${inputScriptInfo}")

        apiClient.apiService.generateScript("${tokenManager.getUid()}",inputScriptInfo)?.enqueue(object :
            Callback<GenerateScriptResponse> {
            override fun onResponse(call: Call<GenerateScriptResponse>, response: Response<GenerateScriptResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: GenerateScriptResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    script.value = result?.result!!.resultScript.get(0).message.content
                    gptId.value = result?.result!!.gptId

                    findNavController(fragment).navigate(R.id.scriptResultFragment)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: GenerateScriptResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    findNavController(fragment).navigate(R.id.scriptTimeFragment)
                }
            }

            override fun onFailure(call: Call<GenerateScriptResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());


                findNavController(fragment).navigate(R.id.scriptTimeFragment)
            }
        })
    }
}