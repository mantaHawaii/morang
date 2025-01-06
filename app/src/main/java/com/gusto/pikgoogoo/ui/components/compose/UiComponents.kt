package com.gusto.pikgoogoo.ui.components.compose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.data.Article

@Preview
@Composable
fun ItemList() {
    Surface() {
        LazyColumn {
            
        }
    }
}


@Preview
@Composable
fun ArticleItemRow() {
    val buttonHeight = 32.dp
    // 전체 레이아웃을 위한 Column 사용

    Row(
        Modifier.background(colorResource(id = R.color.white))
            .padding(start = 24.dp, end = 32.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "1",
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

                Image(
                    painter = painterResource(id = R.drawable.grade2), // 이미지 리소스 설정
                    contentDescription = null,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
                Column(
                    modifier = Modifier.padding(start = 24.dp).height(75.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "여기에 텍스트가 들어갑니다.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = {0.75f},
                            modifier = Modifier
                                .height(15.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            color = colorResource(id = R.color.innuendo),
                            trackColor = colorResource(id = R.color.gossamer_pink))
                        Text(
                            text = "75",
                            color = colorResource(id = R.color.innuendo),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 5.dp).align(Alignment.Bottom),
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
                        textRight = "0",
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
                        textRight = "0",
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