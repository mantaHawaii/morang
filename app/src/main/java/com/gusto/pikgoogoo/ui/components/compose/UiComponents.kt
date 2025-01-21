package com.gusto.pikgoogoo.ui.components.compose

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Article
import com.gusto.pikgoogoo.data.ArticleOrder
import com.gusto.pikgoogoo.ui.article.list.ArticleListFragment
import com.gusto.pikgoogoo.ui.article.list.ArticleListViewModel
import com.gusto.pikgoogoo.util.DataState

@Composable
fun CollaspingArticleLayout(viewModel: ArticleListViewModel, title: String, subjectId: Int, clickListener: ArticleListFragment.ClickListener) {
    val initialHeight = 330f
    val scalingFactor = 1f
    val scrollState = rememberLazyListState()
    val dragAmountYState = remember { mutableStateOf(0f) }
    val headerHeightState = animateFloatAsState(
        targetValue = if (initialHeight+dragAmountYState.value*scalingFactor < 0f) 0f
        else if (initialHeight+dragAmountYState.value*scalingFactor > initialHeight) initialHeight
        else initialHeight+dragAmountYState.value*scalingFactor
    )
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val b = if (headerHeightState.value >= 0f && headerHeightState.value < initialHeight && available.y > 0) 1 else 0
                val csf = if (scrollState.canScrollForward) 1 else 0
                val csb = if (scrollState.canScrollBackward) 1 else 0
                dragAmountYState.value += available.y*((csf and csb) or b).toFloat()

                /*if (dragAmountYState.value in -initialHeight/scalingFactor..0f) {
                    dragAmountYState.value += available.y
                    dragAmountYState.apply {
                        value = value.coerceIn(-initialHeight/scalingFactor, 0f)
                    }
                }*/
                return Offset.Zero
            }
        }
    }
    //val dataState = viewModel.articlesData.collectAsState()
    Surface(//헤더 부분
        modifier = Modifier
            .fillMaxWidth()
            .testTag("layoutHeader"),
        color = colorResource(R.color.background_grey)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {//헤더 내부 레이아웃 정의 - 탑바/주제제목/검색창/탭레이아웃/댓글보기
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {//헤더의 탑바 - 뒤로가기/즐겨찾기/공유
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_keyboard_arrow_left_24),
                    contentDescription = "back",
                    tint = colorResource(R.color.text_dark_grey),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(11.dp)
                        .clickable {
                            clickListener.onBackClick()
                        }
                )//뒤로가기버튼
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_bookmark_border_24),
                    contentDescription = "bookmark",
                    tint = colorResource(R.color.text_dark_grey),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(11.dp)
                        .clickable {
                            clickListener.onBookmarkClick(subjectId)
                        }
                    )//즐겨찾기 버튼
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_share_24),
                    contentDescription = "공유",
                    tint = colorResource(R.color.text_dark_grey),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(11.dp)
                        .clickable {
                            clickListener.onShareClick(subjectId)
                        }
                    )//공유 버튼
            }
            Box(modifier = Modifier.height(headerHeightState.value.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    val titleFontSize = 20.sp
                    val searchFontSize = 15.sp
                    val tabDataList = listOf(
                        TabData("전체") { clickListener.onTabClick(subjectId, ArticleOrder.ALL) },
                        TabData("일") { clickListener.onTabClick(subjectId, ArticleOrder.DAY) },
                        TabData("주") { clickListener.onTabClick(subjectId, ArticleOrder.WEEK) },
                        TabData("월") { clickListener.onTabClick(subjectId, ArticleOrder.MONTH) },
                        TabData("신규") { clickListener.onTabClick(subjectId, ArticleOrder.NEW) },
                    )

                    Text(
                        text = title,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(start = 32.dp, end = 32.dp),
                        fontSize = titleFontSize,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )
                    SearchField(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 16.dp, start = 90.dp, end = 90.dp),
                        searchFontSize = searchFontSize
                    )
                    MyTabRow(tabDataList)
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clickable {
                            clickListener.onShowCommentsClick(subjectId)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_segment_24),
                        contentDescription = "bookmark",
                        tint = colorResource(R.color.text_dark_grey),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                    Text(text = "댓글보기", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.text_dark_grey))
                }
            }
            /*if (dataState.value is DataState.Success) {
                val items = (dataState.value as DataState.Success<List<Article>>).result
                LazyColumn(
                    modifier = Modifier.nestedScroll(nestedScrollConnection),
                    state = scrollState
                ) {
                    items(items) { item: Article ->
                        ArticleItemRow(item)
                    }
                }
            }*/

        }
    }
}

@Composable
fun ArticleItemRow(article: Article) {
    val buttonHeight = 32.dp
    // 전체 레이아웃을 위한 Column 사용

    Row(
        Modifier
            .background(colorResource(id = R.color.white))
            .padding(start = 24.dp, end = 32.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = article.rank.toString(),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .defaultMinSize(minWidth = 32.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Row를 사용하여 이미지와 텍스트를 나란히 배치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 25.dp), // 아래쪽 여백 설정
                verticalAlignment = Alignment.CenterVertically // 세로로 가운데 정렬
            ) {
                if (article.imageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(article.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = article.content,
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        contentScale = if (article.cropImage == 1) ContentScale.Crop else ContentScale.Inside
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .height(75.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = article.content,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = {article.voteRate/100f},
                            modifier = Modifier
                                .height(15.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            color = colorResource(id = R.color.innuendo),
                            trackColor = colorResource(id = R.color.gossamer_pink))
                        Text(
                            text = article.voteRate.toInt().toString(),
                            color = colorResource(id = R.color.innuendo),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .align(Alignment.Bottom),
                            fontSize = 15.sp)
                        Text(
                            text = "%",
                            color = colorResource(id = R.color.innuendo),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.Bottom))
                    }
                }
            }

            // 두 개의 버튼을 배치
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp), // 버튼 사이 간격 설정
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight)
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    ArticleButton(
                        textLeft = "댓글",
                        textRight = article.commentCount.toString(),
                        backgroundColor = colorResource(id = R.color.spun_sugar),
                        contentColor = colorResource(id = R.color.skydiver)
                    )
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    ArticleButton(
                        textLeft = "투표",
                        textRight = article.voteCount.toString(),
                        backgroundColor = colorResource(id = R.color.gossamer_pink),
                        contentColor = colorResource(id = R.color.innuendo)
                    )
                }

            }
        }
    }

}

@Composable
fun ArticleButton(
    onClick:()->Unit={},
    modifier: Modifier = Modifier,
    textLeft: String,
    textRight: String,
    backgroundColor: Color = colorResource(id = R.color.skyblue_500),
    contentColor: Color = colorResource(id = R.color.white)
) {
    Button(onClick = { onClick() },
        modifier = modifier.wrapContentWidth(),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor, // 배경 색
            contentColor = contentColor // 텍스트 색
        )
    ) {
        Row(modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically) {
            ResponsiveText(text = textLeft)
            VerticalDivider(modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                thickness = 1.2.dp,
                color = contentColor)
            ResponsiveText(text = textRight, textModifier = Modifier.defaultMinSize(minWidth = 32.dp))
        }
    }
}

@Composable
fun ResponsiveText(text: String, modifier: Modifier = Modifier, textModifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxHeight()) {
        // 부모의 크기에 따라 텍스트 크기 계산
        val fontSize: TextUnit = when {
            maxHeight < 35.dp -> 12.sp // 화면이 좁으면 작은 텍스트
            maxHeight < 50.dp -> 18.sp // 화면이 중간이면 적당한 크기
            else -> 24.sp // 화면이 크면 큰 텍스트
        }

        Text(text = text,
            fontSize = fontSize,
            modifier = textModifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically),
            overflow = TextOverflow.Visible,
            textAlign = TextAlign.Center)
    }
}

@Composable
fun SearchField(modifier: Modifier = Modifier, searchFontSize: TextUnit) {
    Box(
        modifier = modifier
            .background(
                color = Color.White,  // 배경 색상
                shape = RoundedCornerShape(1000.dp)  // 16dp 만큼 라운드 처리
            )
            .padding(top = 11.dp, bottom = 11.dp, start = 15.dp, end = 15.dp),

    ) {

        val text = remember {
            mutableStateOf("")
        }

        val hintVisible = remember {
            derivedStateOf { text.value.isEmpty() }
        }

        if (hintVisible.value) {
            // placeholder 용 Text
            Text(text = "Search...", fontSize = searchFontSize, color = colorResource(R.color.text_grey))
        }

        BasicTextField(
            value = TextFieldValue(text.value),
            onValueChange = { newText ->
                text.value = newText.text
            },
            textStyle = TextStyle(color = colorResource(R.color.text_dark_grey), fontSize = searchFontSize),
            singleLine = true
        )
    }
}

@Stable
data class TabData(val text: String, val onClick: () -> Unit)

@Composable
fun MyTabLayout(tabDataList: List<TabData>) {
    // 선택된 탭을 기억할 상태
    val selectedTabIndex = remember { mutableStateOf(0) }

    Column {
        // TabRow 사용
        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            modifier = Modifier.fillMaxWidth(0.8f),
            divider = {
                HorizontalDivider(color = Color.Transparent)
            },
            containerColor = colorResource(R.color.background_grey),
            indicator = { tabPositions ->
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value]),
                    color = colorResource(R.color.main),
                    height = 3.dp,
                    width = 48.dp
                )

            }
        ) {
            // 각 탭을 생성
            tabDataList.forEachIndexed { index, data ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    onClick = { selectedTabIndex.value = index },
                    text = { Text(data.text) },
                    selectedContentColor = colorResource(R.color.main),
                    unselectedContentColor = colorResource(R.color.text_grey)
                )
            }
        }

        // 선택된 탭에 대한 onClick함수 실행
        tabDataList[selectedTabIndex.value].onClick()
    }
}

@Composable
fun MyTabRow(tabDataList: List<TabData>) {

    val sizeState = remember { mutableStateOf(DpSize(0.dp, 0.dp)) }
    val density = LocalDensity.current
    val selectedIndexState = remember { mutableStateOf(0) }
    val position = remember { mutableStateOf(Offset(0f, 0f)) }
    val animatedState = animateIntOffsetAsState(
        targetValue = position.value.let { offset ->
            IntOffset(offset.x.let { it-sizeState.value.width.value/2f }.toInt(), offset.y.let { it-sizeState.value.height.value/2f }.toInt())
        }
    )
    val widthState = animateDpAsState(
        targetValue = sizeState.value.width
    )

    Box {
        Row(
            modifier = Modifier.zIndex(2f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabDataList.forEachIndexed { index, data ->
                Text(
                    text = data.text,
                    fontSize = 15.sp,
                    color = if (index==selectedIndexState.value) colorResource(R.color.main) else colorResource(R.color.text_grey) ,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                        .onGloballyPositioned { layoutCoordinates ->
                            if (index==selectedIndexState.value) {
                                position.value = layoutCoordinates.positionInParent()
                                Log.d("MR_ALF", "위치:"+position.value.toString())
                                val sizeInPx = layoutCoordinates.size
                                val width = with(density) { sizeInPx.width.let { it*1.4f }.toDp() }
                                val height = with(density) { sizeInPx.height.let { it*1.5f }.toDp() }
                                sizeState.value = DpSize(width, height)
                            }
                        }
                        .clickable {
                            data.onClick()
                            selectedIndexState.value = index
                        }
                )
            }
        }
        Surface(
            color = colorResource(R.color.spun_sugar),
            modifier = Modifier.size(widthState.value, sizeState.value.height)
                .offset {
                    animatedState.value
                }
                .zIndex(1f),
            shape = RoundedCornerShape(3.dp)
        ) {

        }
    }

}
