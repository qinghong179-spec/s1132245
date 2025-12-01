package tw.edu.pu.csim.shiqing.s1132245

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import tw.edu.pu.csim.shiqing.s1132245.R

// 定義圖示的固定尺寸 (300px = 300.dp 方便計算)
private val ICON_SIZE = 300.dp

@Composable
fun ExamScreen(
    viewModel: ExamViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val density = LocalDensity.current

    // 使用 BoxWithConstraints 獲取 Composable 的最大尺寸 (DP)
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = with(density) { constraints.maxWidth.toPx() }
        val heightPx = with(density) { constraints.maxHeight.toPx() }

        // 獲取 DP 單位下的螢幕高度
        val screenHeightDp = constraints.maxHeight

        LaunchedEffect(Unit) {
            viewModel.updateScreenDimensions(widthPx, heightPx)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow), // 黃色背景
            // ❌ 移除 contentAlignment = Alignment.Center，改用 align()
        ) {

            // ==========================================================
            // A. 中間的文字和圖片內容 (v2 內容)
            // ==========================================================
            Column(
                modifier = Modifier.align(Alignment.Center), // 確保中間內容保持居中
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ... (您的 happy.png 圖片) ...
                Image(
                    painter = painterResource(id = R.drawable.happy),
                    contentDescription = "Exam Logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(text = "瑪利亞基金會服務大考驗", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp)) // 間距高度 10dp

                Text(text = "作者：${uiState.author}", fontSize = 16.sp)
                Text(
                    text = "螢幕大小：${"%.1f".format(uiState.screenWidthPx)} x ${"%.1f".format(uiState.screenHeightPx)} px",
                    fontSize = 14.sp
                )
                Text(text = "成績：${uiState.score}分", fontSize = 14.sp)
            }

            // ==========================================================
            // B. 四個角色圖示 (v3 新增內容)
            // ==========================================================

            // 1. 嬰幼兒 (左邊切齊，底部切齊螢幕 1/2 處)
            Image(
                painter = painterResource(id = R.drawable.role0),
                contentDescription = "Infant",
                modifier = Modifier
                    .size(ICON_SIZE)
                    .align(Alignment.TopStart) // 從左上角開始定位
                    .offset(
                        x = 0.dp, // 切齊螢幕左邊
                        y = (screenHeightDp / 2) - ICON_SIZE // 底部位置 (1/2 處) 減去圖示高度
                    )
            )

            // 2. 兒童 (右邊切齊，底部切齊螢幕 1/2 處)
            Image(
                painter = painterResource(id = R.drawable.role0),
                contentDescription = "Child",
                modifier = Modifier
                    .size(ICON_SIZE)
                    .align(Alignment.TopEnd) // 從右上角開始定位
                    .offset(
                        x = 0.dp, // 切齊螢幕右邊
                        y = (screenHeightDp / 2) - ICON_SIZE
                    )
            )

            // 3. 成人 (左邊切齊，底部切齊螢幕底部)
            Image(
                painter = painterResource(id = R.drawable.role1),
                contentDescription = "Adult",
                modifier = Modifier
                    .size(ICON_SIZE)
                    .align(Alignment.BottomStart) // 左邊切齊，底部切齊

                        Image(
                        painter = painterResource(id = uiState.dropIconId),
                contentDescription = "Falling Icon",
                modifier = Modifier
                    .size(ICON_SIZE) // 使用固定尺寸
                    // 使用 offset 根據 ViewModel 中的 px 座標移動圖示
                    .offset {
                        // 將 Float 座標轉換為 IntOffset (Int 座標)
                        IntOffset(
                            x = uiState.dropIconX.roundToInt(),
                            y = uiState.dropIconY.roundToInt()
                        )
                    }
                    // 實作水平拖曳
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume() // 消耗事件
                            viewModel.updateDropIconX(dragAmount.x) // 僅處理水平拖曳
                        }
                    }
            )

            // 4. 一般民眾 (右邊切齊，底部切齊螢幕底部)
            Image(
                painter = painterResource(id = R.drawable.role3),
                contentDescription = "General Public",
                modifier = Modifier
                    .size(ICON_SIZE)
                    .align(Alignment.BottomEnd) // 右邊切齊，底部切齊
            )
        }
    }
}