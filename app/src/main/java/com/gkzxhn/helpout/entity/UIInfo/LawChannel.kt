package com.gkzxhn.helpout.entity.UIInfo

import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App

data class LawChannel(
        val category: String,
        val name: String,
        val desc: String?,
        val imgRes: Int? = null
) {
    companion object {
        val CHANNELS = listOf(
                LawChannel("PROPERTY_DISPUTES", App.mContext.getString(R.string.property_dispute), App.mContext.getString(R.string.property_dispute_tips), R.mipmap.icon_property_dispute),
                LawChannel("MARRIAGE_FAMILY", App.mContext.getString(R.string.msttishr_family), App.mContext.getString(R.string.msttishr_family_tips), R.mipmap.icon_marriage_family),
                LawChannel("TRAFFIC_ACCIDENT", App.mContext.getString(R.string.traffic_accident), App.mContext.getString(R.string.traffic_accident_tips), R.mipmap.icong_traffic_accident),
                LawChannel("WORK_COMPENSATION", App.mContext.getString(R.string.work_cover), App.mContext.getString(R.string.work_cover_tips), R.mipmap.icon_work_cover),
                LawChannel("CONTRACT_DISPUTE", App.mContext.getString(R.string.icon_contract_dispute), App.mContext.getString(R.string.icon_contract_dispute_tips), R.mipmap.icon_contract_dispute),
                LawChannel("CRIMINAL_DEFENSE", App.mContext.getString(R.string.criminal_defense), App.mContext.getString(R.string.criminal_defense_tips), R.mipmap.icon_criminal_defense),
                LawChannel("HOUSING_DISPUTES", App.mContext.getString(R.string.property_disputes), App.mContext.getString(R.string.property_disputes_tips), R.mipmap.icon_property_disputes),
                LawChannel("LABOR_EMPLOYMENT", App.mContext.getString(R.string.labour_employment), App.mContext.getString(R.string.labour_employment_tips), R.mipmap.icon_labour_employment)
        )

        fun find(category: String): LawChannel? =
                CHANNELS.find { it.category == category }

        fun findByName(name: String): LawChannel? =
                CHANNELS.find { it.name == name }
    }
}