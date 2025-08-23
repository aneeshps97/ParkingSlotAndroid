package com.example.parkingslot.customresuables.scrollables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkingslot.customresuables.labels.LabelWithTrailingIcon

@Composable
fun ScrollableBoxContent(listofData: List<String>,
                         onRemove: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxHeight(.8f)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Column {listofData.forEachIndexed { index, data ->
            LabelWithTrailingIcon(
                "",
                data,
                { onRemove(index) }   // call back to parent
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        }
    }
}
