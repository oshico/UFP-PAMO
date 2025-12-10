package edu.ufp.pam.examples.p01_composeclients.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edu.ufp.pam.examples.p01_composeclients.ui.ui.theme.PAMOTheme
import kotlinx.coroutines.launch

class MainComposeClientsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PAMOTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    ClientScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

private data class ThumbState(
    val isVisible: Boolean,
    val height: Float = 0f,
    val offset: Float = 0f
)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PAMOTheme {
        Scaffold { innerPadding ->
            //Greeting("Android")
            ClientScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

// Opt-in for ExperimentalMaterial3Api is needed for OutlinedTextField
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen(modifier: Modifier = Modifier) {
    //==== Set state management variables ====
    // 1. State for TextField which will be used to insert client names
    // 'remember' makes Compose keep this value across recompositions
    // 'mutableStateOf' makes it observable, so the UI updates when it changes.
    var name by remember { mutableStateOf("") }
    // 2. State for the list of clients
    // 'rememberSaveable' is used here to survive configuration changes (like screen rotation).
    val clientList = rememberSaveable { mutableStateListOf<String>() }
    // 3. State that LazyColumn and scrollbar can share
    val listState = rememberLazyListState()

    //==== Set UI layout ====
    // One column containing input section (Row) and output list (LazyColumn)
    Column(
        modifier = Modifier
            .fillMaxSize()
            //.padding(16.dp), // Add padding around screen content
            .padding(horizontal = 16.dp), // Add horizontal padding only to not interfere with Scaffold's vertical padding
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Add top padding manually to separate from top system bar
        Spacer(modifier = Modifier.height(16.dp))
        //Add on Row for input section, containing TextField and Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add TextField for client name input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Insert Client Name") },
                modifier = Modifier.weight(1f), //TextField takes up available space
                singleLine = true
            )
            //Space between TextField and Button
            Spacer(modifier = Modifier.width(8.dp)) //Space between TextField and Button
            //Add Button for inserting client name
            Button(
                onClick = {
                    // Only add name if not blank
                    if (name.isNotBlank()) {
                        clientList.add(name) // Add the current name to the list
                        name = "" // Clear the TextField after adding
                    }
                },
                // Button disabled if TextField is empty
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        }

        // LazyColumn with weight(1f) to take up remaining space
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(clientList) { clientName ->
                Text(
                    text = clientName, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }

        // Add Box to place the scrollbar alongside the LazyColumn
        Box(modifier = Modifier.weight(1f)) {
            //Add LazyColumn to display the list of clients
            LazyColumn(
                state = listState, //Assign state to the LazyColumn
                modifier = Modifier.fillMaxSize()
            ) {
                items(clientList) { clientName ->
                    Text(
                        text = clientName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
            /// Use custom scrollbar implementation
            CustomScrollbar(
                lazyListState = listState,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        // Add a bottom padding manually
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CustomScrollbar(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    thumbWidth: Dp = 8.dp,
    thumbColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
) {
    val coroutineScope = rememberCoroutineScope()

    //State holding height of scrollbar's track (outer Box)
    var trackHeight by remember { mutableStateOf(0f) }

    //Derived state calculates thumb's properties based on scroll state
    //Runs when lazyListState or trackHeight change
    val thumbState by remember(lazyListState, trackHeight) {
        derivedStateOf {
            if (trackHeight == 0f || !lazyListState.canScrollForward && !lazyListState.canScrollBackward) {
                // If track has no height or can't be scrolled then thumb is not visible
                return@derivedStateOf ThumbState(isVisible = false)
            }

            //Calculate total height of all items in the list
            val totalContentHeight = lazyListState.layoutInfo.totalItemsCount *
                    (lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0)
            //Height of area where the list is visible
            val viewportHeight = lazyListState.layoutInfo.viewportSize.height
            //Ratio of visible content to total content determines thumb height
            val thumbHeight = (viewportHeight.toFloat() / totalContentHeight) * trackHeight
            //Calculate how far list has been scrolled
            val totalScrolledOffset = (lazyListState.firstVisibleItemIndex *
                    (lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0)) +
                    lazyListState.firstVisibleItemScrollOffset
            //Ratio of scrolled content to total content determines thumb offset
            val thumbOffset =
                (totalScrolledOffset.toFloat() / totalContentHeight) * trackHeight
            //Return thumb state with calculated height and offset
            ThumbState(
                isVisible = true,
                height = thumbHeight,
                offset = thumbOffset
            )
        }
    }

    // Outer Box (The Track) - invisible container
    Box(
        modifier = modifier
            .width(thumbWidth)
            .fillMaxHeight()
            .onSizeChanged { size ->
                trackHeight = size.height.toFloat()
            } // Capture the track height
    ) {
        // Inner Box (The Thumb) - only shown if isVisible is true
        if (thumbState.isVisible) {
            Box(
                modifier = Modifier
                    .width(thumbWidth)
                    .height(with(LocalDensity.current) { thumbState.height.toDp() })
                    .offset(y = with(LocalDensity.current) { thumbState.offset.toDp() })
                    .background(thumbColor, CircleShape)
                    .draggable(
                        state = rememberDraggableState { delta ->
                            coroutineScope.launch {
                                // When dragging, convert the drag delta to a scroll delta
                                val totalContentHeight =
                                    lazyListState.layoutInfo.totalItemsCount *
                                            (lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.size
                                                ?: 0)
                                val scrollAmount =
                                    (delta / trackHeight) * totalContentHeight
                                lazyListState.scrollBy(scrollAmount)
                            }
                        },
                        orientation = Orientation.Vertical
                    )
            )
        }
    }
}

