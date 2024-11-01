package com.example.dam_g1_aplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dam_g1_aplication.R
import com.example.dam_g1_aplication.dataClasses.Achievements

class AchievementsAdapter(
    private val achievements: List<Achievements>
) : RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.achievement_name)
        val descriptionTextView: TextView = view.findViewById(R.id.achievement_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achiv, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.nameTextView.text = achievement.title
        holder.descriptionTextView.text = achievement.description
    }

    override fun getItemCount() = achievements.size
}
