package com.example.editmyrecord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editmyrecord.ui.theme.BlueGreen
import com.example.editmyrecord.ui.theme.EditMyRecordTheme
import com.example.editmyrecord.ui.theme.Grey100
import com.example.editmyrecord.ui.theme.Grey200
import com.example.editmyrecord.ui.theme.Grey400
import com.example.editmyrecord.ui.theme.Grey500
import com.example.editmyrecord.ui.theme.Grey900

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditMyRecordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    SearchScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var searchText by remember { mutableStateOf("") }
    var isContentFocused by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var clickSearch by remember { mutableStateOf(false) }
    var searchedText by remember { mutableStateOf("") }
    val db = remember {
        AppDatabase.getDatabase(context)
    }
    val recordList by db.recordDao().getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var clickSearchList by remember { mutableStateOf(false) }
    var clickInfoUid by remember { mutableStateOf(0) }

    Column {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .width(300.dp)
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            isContentFocused = false
                        },
                        label = {
                            if (isContentFocused) {
                                Text(
                                    text = "제목, 장소, 날짜, 해시태그",
                                    color = Grey400,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = BlueGreen,
                            containerColor = Grey200,
                            cursorColor = BlueGreen,
                            disabledIndicatorColor = Color.White,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White
                        ),
                        modifier = Modifier
                            .width(300.dp)
                            .height(80.dp)
                            .padding(start = 20.dp, end = 0.dp, top = 10.dp, bottom = 3.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 1
                    )
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Button(
                            onClick = {
                                clickSearch = false
                                searchText = ""
                            },
                            colors = ButtonDefaults.buttonColors(Grey200),
                            modifier = Modifier
                                .height(80.dp)
                                .width(50.dp)
                                .padding(bottom = 20.dp, top = 20.dp),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "x",
                                color = Color(0xfff02323),
                                fontSize = 18.sp,
                                modifier = Modifier.padding(0.dp)
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        clickSearch = true
                        searchedText = searchText
                    },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    modifier = Modifier
                        .height(80.dp)
                        .width(80.dp)
                        .padding(top = 10.dp, bottom = 10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "검색",
                        color = Grey900,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }
            Divider(thickness = 1.dp, color = Grey100)
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val searchList = recordList.filter {
                    val tagList = it.tag?.split("#")?.map { it.trim() }
                    if (clickSearch) {
                        it.title == searchedText || it.place == searchedText || it.date == searchedText || tagList?.any {it == searchedText} ?: false
                    } else {
                        false
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                for (record in searchList) {
                    SearchList(
                        record = record,
                        onClick = {
                            clickInfoUid = record.uid
                            clickSearchList = !clickSearchList
                        }
                    )
                    Divider(thickness = 1.dp, color = Grey100)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
        Column {
            NavigateBar(
                homeColor = Grey900,
                searchColor = BlueGreen,
                mapColor = Grey900,
                myRecordColor = Grey900,
                context = context
            )
        }
    }
    if (clickSearchList) {
        MyRecordView(record = recordList.find { it.uid == clickInfoUid }!!, context = context, db = db, scope = scope) {
            clickSearchList = false
        }
    }
}

@Composable
fun SearchList(record: Record?, onClick: () -> Unit) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(Color.White),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${record?.place}",
                    fontSize = 20.sp,
                    color = Grey900,
                    modifier = Modifier.width(300.dp),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "${record?.date}",
                    fontSize = 16.sp,
                    color = Grey500,
                    modifier = Modifier.width(300.dp),
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}