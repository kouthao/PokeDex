package com.walmart.pokedexapp.activities

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.walmart.pokedexapp.databinding.ActivityDetailBinding
import com.walmart.pokedexapp.helper.*
import com.walmart.pokedexapp.repository.entities.LoadResult
import com.walmart.pokedexapp.repository.entities.Response
import com.walmart.pokedexapp.viewmodels.PokeDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModel<PokeDetailViewModel>()

    companion object {
        const val POKE_NAME = "poke_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getPokeData().observe(this) {
            updateViews(it)
        }

        viewModel.loading().observe(this) {
            binding.progressBar.isVisible = it == LoadResult.LOADING
            binding.pokeGroup.isVisible = it == LoadResult.SUCCESS
            binding.errorPoke.isVisible = it == LoadResult.FAIL
        }

        val pokeName = intent.getStringExtra(POKE_NAME)?:""
        viewModel.loadData(pokeName)
    }

    private fun updateViews(item: Response.PokeDetailData) {
        binding.name.text = item.name.capitalizeFirst()
        binding.weight.text = item.weight.formatWeight()
        binding.height.text = item.height.formatHeight()

        binding.types.removeAllViews()
        item.types.sortedBy { it.typeName() }.forEach { type ->
            val chip = Chip(this).apply {
                text = type.typeName().capitalizeFirst()
                setTextColor(Color.WHITE)
                setChipBackgroundColorResource(type.getTypeColor())
            }

            binding.types.addView(chip)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return false
    }
}