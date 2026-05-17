package com.esmanureral.neurostage.domain.patient

import android.content.Context
import androidx.annotation.ArrayRes
import com.esmanureral.neurostage.R

object DementiaStageLabels {
    @ArrayRes
    val arrayRes: Int = R.array.dementia_stage_labels

    fun labels(context: Context): Array<String> =
        context.resources.getStringArray(arrayRes)

    fun labelAt(context: Context, stageIndex: Int): String =
        labels(context).getOrElse(stageIndex) { "" }
}
