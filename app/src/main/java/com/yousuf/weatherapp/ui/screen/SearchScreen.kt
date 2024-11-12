package com.yousuf.weatherapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yousuf.weatherapp.R
import com.yousuf.weatherapp.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Show location rational dialog only if possible
        ShowRationalDialog()

        val text = remember { viewModel.searchQuery }
        OutlinedTextField(
            value =  text.value,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = {
                Text(text = stringResource(id = R.string.search_hint))
            }
        )

        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = { viewModel.getWeatherData() },
            enabled = text.value.isNotEmpty()
        ) {
            Text(text = stringResource(id = R.string.search))
        }
    }
}