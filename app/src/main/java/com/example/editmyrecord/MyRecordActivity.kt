package com.example.editmyrecord

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.editmyrecord.ui.theme.BlueGreen
import com.example.editmyrecord.ui.theme.EditMyRecordTheme
import com.example.editmyrecord.ui.theme.Grey100
import com.example.editmyrecord.ui.theme.Grey400
import com.example.editmyrecord.ui.theme.Grey500
import com.example.editmyrecord.ui.theme.Grey900
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyRecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditMyRecordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MyRecordScreen()
                }
            }
        }
    }
}

@Composable
fun MyRecordScreen() {
    var clickPhoto by remember { mutableStateOf(false) }
    var clickInfoUid by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val db = remember {
        AppDatabase.getDatabase(context)
    }
    val recordList by db.recordDao().getAll().collectAsState(initial = emptyList())
//    val firstPhotoList: List<Bitmap> = recordList.map { record ->
//        val uriList = UriListConverter().toUrisList(record.photo).first()
//        val contentResolver = context.contentResolver
//        MediaStore.Images.Media.getBitmap(contentResolver, uriList)
//    }
    val scope = rememberCoroutineScope()

    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 10.dp, top = 15.dp)
        ) {
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 170.dp)) {
                items(recordList) { record ->
                    ImageItem(
                        record = record,
                        context = context,
                        onClick = {
                            clickInfoUid = record.uid
                            clickPhoto = !clickPhoto
                        }
                    )
                }
            }
        }
        Column {
            NavigateBar(
                homeColor = Grey900,
                searchColor = Grey900,
                mapColor = Grey900,
                myRecordColor = BlueGreen,
                context = context
            )
        }
    }
    if (clickPhoto) {
        MyRecordView(
            record = recordList.find { it.uid == clickInfoUid }!!,
            context = context,
            db = db,
            scope = scope
        ) {
            clickPhoto = false
        }
    }
}


@Composable
fun ImageItem(record: Record, context: Context, onClick: () -> Unit) {
    val changeUri = record.photo.trim('[', ']').replace(" ", "")
    val uriStringList = changeUri.split(",")
    val uriList = uriStringList.map { Uri.parse(it) }
    //uriLsit!![0]보다는 remember사용하면 상태가 변경될 때만 작동하기 때문에 불필요한 작동을 방지할 수 있음.
    var firstUriList = remember { mutableStateOf(uriList[0]) }
//    val firstUriList = uriList[0]
    //firstUriList를 remember로 변경하니깐 it이 오류가 나서 주석 처리함.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                firstUriList.value = uri
            }
        }
    )
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(5.dp)
            .size(170.dp)
            .clickable { onClick() },
    ) {
//        for (text in uriList) {
//            Text(text = text, fontSize = 10.sp)
//        }
        AsyncImage(
            model = firstUriList.value,
            contentDescription = "사진들",
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.delete),
            filterQuality = FilterQuality.None

        )
        val bitmap = loadImageBitmap(context, firstUriList.toString())
        bitmap?.let {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "사진들",
                contentScale = ContentScale.Crop
            )
        }
    }
//    Button(
//        onClick = onClick,
//        shape = RoundedCornerShape(10.dp),
//        modifier = Modifier
//            .padding(5.dp)
//            .size(170.dp),
//        contentPadding = PaddingValues(0.dp),
//    ) {
//        //Coil library
//        AsyncImage(
//            model = firstUriList,
//            contentDescription = "사진들",
//            contentScale = ContentScale.Crop,
//        )
//        firstUriList.value.let { uri ->
//            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                ImageDecoder.decodeBitmap(
//                    ImageDecoder.createSource(
//                        context.contentResolver,
//                        uri
//                    )
//                )
//            } else {
//                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
//            }
//            GetBitmap(bitmap = bitmap)
//        }
//        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}


@Composable
fun MyRecordView(
    record: Record,
    context: Context, db: AppDatabase, scope: CoroutineScope, onBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White
    ) {
        var clickEdit by remember { mutableStateOf(false) }
        val changeUri = record.photo.trim('[', ']').replace(" ", "")
        val uriStringList = changeUri.split(",")
        val uriList = uriStringList.map { Uri.parse(it) }
        var clickEditUid by remember { mutableStateOf(0) }
        var clickPhoto by remember { mutableStateOf(false) }
        var clickPhotoUri by remember { mutableStateOf<Uri?>(null) }
//        var screenSize by remember { mutableStateOf(IntSize(0, 0)) }

        Column {
            //상단
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize(),
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
                    Spacer(modifier = Modifier.width(150.dp))
                    IconButton(
                        onClick = {
                            val deleteInfo = record
                            if (deleteInfo != null) {
                                scope.launch(Dispatchers.IO) {
                                    db.recordDao().delete(deleteInfo)
                                }
                            }
                            onBack()
                        },
                        colors = IconButtonDefaults.iconButtonColors(Color.White),
                        modifier = Modifier
                            .height(48.dp)
                            .width(48.dp)
                            .padding(0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "삭제 하기",
                            tint = Color(0xfff02323)
                        )
                    }
                    TopButton(
                        onClick = {
                            clickEditUid = record.uid
                            clickEdit = !clickEdit
                        },
                        icon = null,
                        detail = "",
                        color = Grey500,
                        modifier = Modifier,
                        text = "수정",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Spacer(modifier = Modifier.height(13.dp))
                if (record.title != null) {
                    MyRecordText(text = record.title!!, color = Grey900, fontSize = 20)
                }
                MyRecordText(text = record.place, color = Grey500, fontSize = 14)
            }
            Row(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                if (!clickPhoto) {
                    for (recordImage in uriList) {
                        Card(
                            modifier = Modifier
                                .width(333.dp)
                                .height(310.dp)
                                .padding(6.dp)
                                .clickable {
                                    clickPhotoUri = recordImage
                                    clickPhoto = true
                                },
                            shape = RoundedCornerShape(10.dp)
                        ) {
//                            Text(text = "$recordImage")
                            AsyncImage(
                                model = recordImage,
                                contentDescription = "사진",
                                contentScale = ContentScale.Crop,
                            )
//                            val bitmap = loadImageBitmap(context, uriStringList)
//                            bitmap?.let {
//                                Image(
//                                    bitmap = bitmap.asImageBitmap(), contentDescription = "", contentScale = ContentScale.Fit
//
//                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            ) {
                if (record.mainText != null) {
                    MyRecordText(text = record.mainText!!, color = Grey900, fontSize = 16)
                }
                if (record.tag != null) {
                    MyRecordText(text = record.tag!!, color = BlueGreen, fontSize = 14)
                }
                Spacer(modifier = Modifier.height(10.dp))
                MyRecordText(text = record.date, color = Grey500, fontSize = 14)
            }
        }
        if (clickEdit) {
            EditRecord(
                record = record,
                context = context,
                db = db,
                scope = scope,
                uriList = uriList
            ) {
                clickEdit = false
            }
        }
        if (clickPhoto) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var scale by remember { mutableStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }
                val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                    scale *= zoomChange
                    if (scale * zoomChange < 1f) {
                        scale = 1f
                        offset = Offset(0f, 0f)
                    }
//                    val newOffset = offset + offsetChange
//                    if (offestBoundary(newOffset, screenSize, scale))
                    if (scale > 1f) {
                        offset += offsetChange * 2f
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { clickPhoto = false }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
//                            .onGloballyPositioned { coordinates ->
//                                screenSize = coordinates.size
//                            }
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .transformable(state = state),
                        contentAlignment = Alignment.Center
                    ) {
//                Text(text = "$clickPhotoUri")
                        AsyncImage(
                            model = clickPhotoUri,
                            contentDescription = "사진",
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyRecordText(text: String, color: Color, fontSize: Int) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp)
    )
}

fun offestBoundary(offset: Offset, screenSize: IntSize, scale: Float): Boolean {
    val scaledWidth = screenSize.width.toFloat() * scale
    val scaledHeight = screenSize.height.toFloat() * scale

    return offset.x >= -scaledWidth / 2 && offset.x <= scaledWidth / 2 && offset.y >= -scaledHeight / 2 && offset.y <= scaledHeight / 2
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecord(
    record: Record?,
    context: Context,
    db: AppDatabase,
    scope: CoroutineScope,
    uriList: List<Uri>?,
    onBack: () -> Unit
) {
    var editTitle by remember { mutableStateOf(record?.title ?: "") }
    var editPlace by remember { mutableStateOf(record?.place ?: "") }
    var editDate by remember { mutableStateOf(record?.date ?: "") }
    var editMainText by remember { mutableStateOf(record?.mainText ?: "") }
    var isTitleFocused by remember { mutableStateOf(true) }
    var isContentFocused by remember { mutableStateOf(true) }
    var editTag by remember { mutableStateOf(record?.tag ?: "") }
    //사진 다중 선택
    var selectUris by remember {
        mutableStateOf(uriList ?: emptyList())
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
                        text = "추억 수정하기",
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
                            record?.title = editTitle
                            record?.date = editDate
                            record?.place = editPlace
                            record?.mainText = editMainText
                            record?.tag = editTag
                            record?.photo = selectUris.toString()

                            scope.launch(Dispatchers.IO) {
                                if (record != null) {
                                    db.recordDao().update(record)
                                }
                            }
                            onBack()
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
                            value = editTitle,
                            onValueChange = {
                                editTitle = it
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
                        DefaultTextField(
                            text = "장소",
                            value = editPlace,
                            onValuechange = { editPlace = it })
                        Line()
                        DefaultTextField(
                            text = "날짜",
                            value = editDate,
                            onValuechange = { editDate = it })
                        Line()
                        TextField(
                            value = editMainText,
                            onValueChange = {
                                editMainText = it
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
                        DefaultTextField(
                            text = "태그",
                            value = editTag,
                            onValuechange = { editTag = it })
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingAnimation() {
    val animation = rememberInfiniteTransition()
    val progress by animation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart,
        )
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .scale(progress)
            .alpha(1f - progress)
            .border(
                5.dp,
                color = Color.Black,
                shape = CircleShape
            )
    )
}

fun loadImageBitmap(context: Context, imageUri: String): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

