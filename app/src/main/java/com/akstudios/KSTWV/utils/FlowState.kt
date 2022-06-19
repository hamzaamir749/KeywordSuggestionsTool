package com.akstudios.KSTWV.utils

import com.seo.app.seostudio.model.KeywordItem


sealed class FlowState {
    object Empty : FlowState()
    class Failure(var error: Throwable?) : FlowState()
    object Loading : FlowState()
    class Error(val error: String) : FlowState()

    class SuccessApiResponse(var data:HashMap<String, KeywordItem>) : FlowState()

}