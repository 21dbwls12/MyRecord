package com.example.editmyrecord

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.editmyrecord.ui.theme.BlueGreen
import com.example.editmyrecord.ui.theme.EditMyRecordTheme
import com.example.editmyrecord.ui.theme.Grey100
import com.example.editmyrecord.ui.theme.Grey200
import com.example.editmyrecord.ui.theme.Grey400
import com.example.editmyrecord.ui.theme.Grey50
import com.example.editmyrecord.ui.theme.Grey900
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditMyRecordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    RecordScreen()
                }
            }
        }
    }
}

@Composable
fun RecordScreen() {
    var clickRecord by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = remember {
        AppDatabase.getDatabase(context)
    }
    val recordList by db.recordDao().getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var clickRecent by remember { mutableStateOf(false) }
    var clickInfoUid by remember { mutableStateOf(0) }


    Box {
        //최근 추억 하단에 고정하기 위한 Column
        Column {
            Spacer(modifier = Modifier.height(100.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                //기록 입력하기 위한 기록창과 연결된 버튼
                Button(
                    onClick = { clickRecord = !clickRecord },
                    colors = ButtonDefaults.buttonColors(Grey50),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .size(353.dp)
                        .padding(10.dp),
                    border = BorderStroke(1.dp, Grey200)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        MainText(maintext = "오늘의 기억", fontSize = 24)
                        Spacer(modifier = Modifier.width(15.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.addcircle),
                            contentDescription = "기억을 기록하세요",
                            tint = Color(0xFF26282B)
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxSize(),
                ) {
                    MainText(maintext = "최근 추억", fontSize = 16)
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .height(170.dp),
                    ) {
                        val sortedRecordList = recordList.sortedByDescending { it.uid }

                        for (index in 0 .. 4) {
                            RecentLogview(record = sortedRecordList.getOrNull(index)) {
                                clickInfoUid = sortedRecordList.getOrNull(index)?.uid ?: clickInfoUid
                                clickRecent = true
                            }
                            Spacer(modifier = Modifier.width(15.dp))
                        }
                    }
                }
            }
            NavigateBar(
                homeColor = BlueGreen,
                searchColor = Grey900,
                mapColor = Grey900,
                myRecordColor = Grey900,
                context = context
            )
        }
        if (clickRecord) {
            InputRecord(context = context, db = db, scope = scope) {
                clickRecord = false
            }
        }
        if (clickRecent) {
            MyRecordView(
                record = recordList.find { it.uid == clickInfoUid }!!,
                context = context,
                db = db,
                scope = scope
            ) {
                clickRecent = false
            }
        }
    }
}

//MainActivity에 있는 Text틀
@Composable
fun MainText(maintext: String, fontSize: Int) {
    Text(
        text = maintext,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        color = Grey900,
    )
}

//최근 추억에 들어갈 Button
//클릭시 해당 내용으로 이동
@Composable
fun RecentLogview(record: Record?, onClick: () -> Unit) {
    val changeUri = record?.photo?.trim('[', ']')?.replace(" ", "")
    val uriStringList = changeUri?.split(",")
    val uriList = uriStringList?.map { Uri.parse(it) }
//    val firstUriList = remember { mutableStateOf(uriList?.getOrNull(0)) }
    val firstUriList = uriList?.getOrNull(0) ?: null

    record?.let {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .size(135.dp)
                .clickable { onClick() },
        ) {
            AsyncImage(
                model = firstUriList,
                contentDescription = "사진",
                contentScale = ContentScale.Crop,
            )
        }
    }
}

//내비게이션바
@Composable
fun NavigateBar(
    homeColor: Color,
    searchColor: Color,
    mapColor: Color,
    myRecordColor: Color,
    context: Context
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = Color.White
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .background(
                        brush = gra(
                            "v",
                            "shadow"
                        ) as Brush,
                        shape = RectangleShape
                    )
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavigateBarButton(
                    bannerMenu = "기록",
                    navigateIcon = R.drawable.editnote,
                    iconDetail = "기록 화면",
                    color = homeColor,
                    onClick = {
                        //!is는 클래스의 인스턴스거나 그의 하위 클래스가 아닐 때 조건이 참
                        //!=는 클래스의 인스턴스를 직접 비교
                        if (context !is RecordActivity) {
                            val intent = Intent(context, RecordActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                )
                NavigateBarButton(
                    bannerMenu = "검색",
                    navigateIcon = R.drawable.search,
                    iconDetail = "검색 화면",
                    color = searchColor,
//                    context = context,
                    onClick = {
                        if (context !is SearchActivity) {
                            val intent = Intent(context, SearchActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                )
                NavigateBarButton(
                    bannerMenu = "지도",
                    navigateIcon = R.drawable.map,
                    iconDetail = "지도 화면",
                    color = mapColor,
//                    context = context,
                    onClick = {
                        if (context !is MapActivity && context !is MainActivity) {
                            val intent = Intent(context, MapActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                )
                NavigateBarButton(
                    bannerMenu = "내 기록",
                    navigateIcon = R.drawable.inventory,
                    iconDetail = "내 기록 화면",
                    color = myRecordColor,
//                    context = context,
                    onClick = {
                        if (context !is MyRecordActivity) {
                            val intent = Intent(context, MyRecordActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                )
            }
        }
    }
}

//내비게이션바 안에 들어갈 Button
//클릭시 화면 전환
@Composable
fun NavigateBarButton(
    bannerMenu: String,
    navigateIcon: Int,
    iconDetail: String,
    color: Color,
//    context: Context,
    onClick: () -> Unit
//    intent: Intent
//    componentActivity: Class<out ComponentActivity>
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxHeight()
            .width(90.dp),
        colors = ButtonDefaults.buttonColors(Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = navigateIcon),
                contentDescription = iconDetail,
                tint = color
            )
            Text(
                text = bannerMenu,
                color = color,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

//MainActivity에 띄울 기록 입력 창
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputRecord(context: Context, db: AppDatabase, scope: CoroutineScope, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var mainText by remember { mutableStateOf("") }
    var isTitleFocused by remember { mutableStateOf(true) }
    var isContentFocused by remember { mutableStateOf(true) }
//    var clickSave by remember { mutableStateOf(false) }
    var tag by remember { mutableStateOf("") }
    //사진 다중 선택
    var selectUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    //사진 불러오기 위한 런처
    val launcherM = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {
            selectUris = it
        }
    )

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White
    ) {
        Column {
            //상단
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    TopButton(
                        onClick = { onBack() },
                        icon = R.drawable.arrowback,
                        detail = "뒤로 가기",
                        color = Grey900,
                        modifier = Modifier.padding(top = 3.dp),
                        text = "이전",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "오늘의 기억 등록하기",
                        color = Grey900,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .height(48.dp)
                            .width(150.dp)
                            .padding(top = 12.dp),
                    )
                    TopButton(
                        onClick = {
                            if (selectUris.isNotEmpty() && place.isNotBlank() && date.isNotBlank()) {
                                val newRecord = Record(
                                    title = title,
                                    date = date,
                                    place = place,
                                    mainText = mainText,
                                    tag = tag,
                                    photo = selectUris.toString()
                                )
                                scope.launch(Dispatchers.IO) {
                                    db.recordDao().insertAll(newRecord)
                                }
                                val intent = Intent(context, MapActivity::class.java)
                                context.startActivity(intent)
                            }
                        },
                        icon = R.drawable.done,
                        detail = "저장 하기",
                        color = BlueGreen,
                        modifier = Modifier,
                        text = "저장",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            //입력될 TextField만 스크롤 가능
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        TextField(
                            value = title,
                            onValueChange = {
                                title = it
                                isTitleFocused = false
                            },
                            label = {
                                if (isTitleFocused) {
                                    Text(
                                        text = "제목",
                                        color = Grey400,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp
                                    )
                                }
                            },
                            textStyle = TextStyle(
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = BlueGreen,
                                containerColor = Color.White,
                                cursorColor = Color(0xff03b2b2),
                                disabledIndicatorColor = Color.White,
                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = Color.White,
                            ),
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp),
                        )
                        Line()
                        DefaultTextField(text = "장소", value = place, onValuechange = { place = it })
                        Line()
                        DefaultTextField(text = "날짜", value = date, onValuechange = { date = it })
                        Line()
                        TextField(
                            value = mainText,
                            onValueChange = {
                                mainText = it
                                isContentFocused = false
                            },
                            label = {
                                if (isContentFocused) {
                                    Text(
                                        text = "내용을 입력하세요.",
                                        color = Grey400,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Grey900,
                                containerColor = Color.White,
                                cursorColor = Color(0xff03b2b2),
                                disabledIndicatorColor = Color.White,
                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(385.dp)
                                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 16.dp)
                        )
                        Line()
                        //해시태그 사용하면 색깔이 바뀌게 되는 것도 가능?
                        DefaultTextField(text = "태그", value = tag, onValuechange = { tag = it })
                        Line()
                        //사진 선택 도구
                        Button(
                            onClick = {
                                launcherM.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                            },
                            colors = ButtonDefaults.buttonColors(Grey100),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(start = 20.dp, end = 20.dp, top = 16.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.addphoto),
                                    contentDescription = "사진 추가",
                                    tint = Grey900
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "사진 등록하기",
                                    color = Grey900,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    //불러온 사진 위치
                    val rows = selectUris.chunked(3)
                    items(rows) { rowUris ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, end = 5.dp),
                        ) {
                            rowUris.forEach { uri ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .padding(3.dp)
                                        .size(120.dp),
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "사진들",
                                        contentScale = ContentScale.Crop
                                    )
                                    //AsynImage 대신
                                    //val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    //    ImageDecoder.decodeBitmap(
                                    //        ImageDecoder.createSource(
                                    //            context.contentResolver,
                                    //            uri
                                    //        )
                                    //    )
                                    //} else {
                                    //    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                                    //}
                                    //GetBitmap(bitmap = bitmap)
                                    //이 코드를 쓰려고 했는데 contentScale = ContentScale.Crop 이렇게 출력할 방법을 찾지 못함
                                }
                            }
                        }
                        //LazyColumn안에는 LazyVerticalGrid는 사용이 안됨.
                        // LazyColumn안에 넣은 내용들이 같이 스크롤이 되게 하려고 했음
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(3),
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp)
//                ) {
//                    items(selectUris) { uri ->
//                        Card(
//                            shape = RoundedCornerShape(10.dp),
//                            modifier = Modifier.padding(3.dp),
//                        ) {
////                            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
////                                ImageDecoder.decodeBitmap(
////                                    ImageDecoder.createSource(
////                                        context.contentResolver,
////                                        uri
////                                    )
////                                )
////                            } else {
////                                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
//                            AsyncImage(
//                                model = uri,
//                                contentDescription = "사진들",
//                                contentScale = ContentScale.Crop
//                            )
//                        }
////                            GetBitmap(bitmap = bitmap)
//
//                    }
//                }
//            }
                    }
                }
            }
        }
    }
}

@Composable
fun TopButton(
    onClick: () -> Unit,
    icon: Int? = null,
    detail: String,
    color: Color,
    modifier: Modifier,
    text: String,
    fontWeight: FontWeight
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color.White),
        modifier = Modifier
            .height(48.dp)
            .width(100.dp)
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = detail,
                tint = color,
                modifier = modifier
            )
        }
        Text(text = text, color = color, fontWeight = fontWeight, fontSize = 14.sp)
    }
}

//기록 입력창에 입력할 기본 정보 TextField
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTextField(text: String, value: String, onValuechange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Text(text = text, color = Grey900, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            value = value,
            onValueChange = onValuechange,
            modifier = Modifier
                .height(50.dp),
            maxLines = 1,
            textStyle = TextStyle(
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            colors = TextFieldDefaults.textFieldColors(
                textColor = BlueGreen,
                containerColor = Color.White,
                cursorColor = Color(0xff03b2b2),
                disabledIndicatorColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White
            ),
        )
    }
}

@Composable
fun Line() {
    Divider(thickness = 1.dp, color = Grey100)
}

@Composable
fun GetBitmap(bitmap: Bitmap) {
    Image(bitmap = bitmap.asImageBitmap(), contentDescription = "")
}

//그라데이션
@Composable
fun gra(select: String, color: String): Any {
    val shadow = listOf(
        Color(0xfffcfcfc),
        Color(0xfffafafa),
        Color(0xfff8f8f8),
        Color(0xfff6f6f6),
        Color(0xfff4f4f4),
    )
    val shadowReverse = shadow.reversed()

    return when (select) {
        "v" -> {
            when (color) {
                "shadow" ->
                    Brush.verticalGradient(
                        shadow
                    )

                "shadowReverse" ->
                    Brush.verticalGradient(
                        shadowReverse
                    )

                else -> {}
            }
        }

        "h" -> {
            when (color) {
                "shadow" ->
                    Brush.horizontalGradient(
                        shadow
                    )

                "shadowReverse" ->
                    Brush.horizontalGradient(
                        shadowReverse
                    )

                else -> {}
            }
        }

        "R" -> {
            when (color) {
                "shadow" ->
                    Brush.radialGradient(
                        shadow
                    )

                "shadowReverse" ->
                    Brush.radialGradient(
                        shadowReverse
                    )

                else -> {}
            }
        }

        else -> {}
    }
}