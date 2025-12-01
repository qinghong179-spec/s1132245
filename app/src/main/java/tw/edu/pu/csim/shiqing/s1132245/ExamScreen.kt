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
import androidx.compose.foundation.gestures.detectDragGestures // V4: 拖曳手勢
import androidx.compose.ui.input.pointer.pointerInput // V4: 拖曳手勢
import androidx.compose.ui.unit.IntOffset // V4: 拖曳位移
import kotlin.math.roundToInt // V4: 座標取整
import tw.edu.pu.csim.shiqing.s1132245.R

// 定義圖示的固定尺寸 (300px 換算為 300.dp)
private val ICON_SIZE = 300.dp

@Composable
fun ExamScreen(
    viewModel: ExamViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val density = LocalDensity.current
    val densityRatio = density.density // V5: 獲取 DP 到 PX 的轉換比例

    // 使用 BoxWithConstraints 獲取 Composable 的最大尺寸 (DP)
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = with(density) { constraints.maxWidth.toPx() }
        val heightPx = with(density) { constraints.maxHeight.toPx() }

        // 獲取 DP 單位下的螢幕高度
        val screenHeightDp = constraints.maxHeight

        LaunchedEffect(Unit) {
            viewModel.updateScreenDimensions(widthPx, heightPx)
            viewModel.setDensityRatio(densityRatio) // V5: 傳遞轉換比例給 ViewModel
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow), // V2: 黃色背景
            // contentAlignment 移除，改用 Modifier.align()
        ) {

            // ==========================================================
            // A. 中間的文字和圖片內容 (V2 內容)
            // ==========================================================
            Column(
                modifier = Modifier.align(Alignment.Center), // 確保中間內容保持居中
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // V2: happy.png 圖片
                Image(
                    painter = painterResource(id = R.drawable.happy),
                    contentDescription = "Exam Logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(text = "瑪利亞基金會服務大考驗", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp)) // V2: 間距高度 10dp

                Text(text = "作者：${uiState.author}", fontSize = 16.sp) // V2: 顯示作者
                Text(
                    text = "螢幕大小：${"%.1f".format(uiState.screenWidthPx)} x ${"%.1f".format(uiState.screenHeightPx)} px",
                    fontSize = 14.sp
                ) // V2: 顯示螢幕尺寸

                Text(text = "成績：${uiState.score}分", fontSize = 14.sp)

                // V5: 顯示碰撞/邊界訊息
                if (uiState.message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = uiState.message,
                        fontSize = 14.sp,
                        color = Color.Red // 讓訊息顯眼
                    )
                }
            }

            // ==========================================================
            // B. 四個角色圖示 (V3 新增內容)
            // 角色圖示：role0, role1, role3
            // ==========================================================

            // 1. 嬰幼兒 (左邊切齊，底部切齊螢幕 1/2 處)
            Image(
                painter = painterResource(id = R.drawable.role0), // 假設 role0
                contentDescription = "Infant",
                modifier = Modifier
                    .size(ICON_SIZE) // 300px/dp
                    .align(Alignment.TopStart)
                    .offset(
                        x = 0.dp,
                        y = (screenHeightDp / 2) - ICON_SIZE // 底部在 1/2 處
                    )
            )

            // 2. 兒童 (右邊切齊，底部切齊螢幕 1/2 處)
            Image(
                painter = painterResource(id = R.drawable.role0), // 假設 role0
                contentDescription = "Child",
                modifier = Modifier
                    .size(ICON_SIZE) // 300px/dp
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 0.dp,
                        y = (screenHeightDp / 2) - ICON_SIZE
                    )
            )

            // 3. 成人 (左邊切齊，底部切齊螢幕底部)
            Image(
                painter = painterResource(id = R.drawable.role1), // 假設 role1
                contentDescription = "Adult",
                modifier = Modifier
                    .size(ICON_SIZE)
                    .align(Alignment.BottomStart)
            )

            // 4. 一般民眾 (右邊切齊，底部切齊螢幕底部)
            Image(
                painter = painterResource(id = R.drawable.role3), // 假設 role3
                contentDescription = "General Public",
                modifier = Modifier
                    .size(ICON_SIZE)
                    .align(Alignment.BottomEnd)
            )

            // ==========================================================
            // C. 掉落中的服務圖示 (V4 新增內容)
            // ==========================================================
            Image(
                painter = painterResource(id = uiState.dropIconId),
                contentDescription = "Falling Icon",
                modifier = Modifier
                    .size(ICON_SIZE) // 300px/dp
                    // V4: 使用 offset 根據 ViewModel 中的 px 座標移動圖示
                    .offset {
                        // 將 Float 座標轉換為 IntOffset (Int 座標)
                        IntOffset(
                            x = uiState.dropIconX.roundToInt(),
                            y = uiState.dropIconY.roundToInt()
                        )
                    }
                    // V4: 實作水平拖曳
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume() // 消耗事件
                            viewModel.updateDropIconX(dragAmount.x) // 僅處理水平拖曳
                        }
                    }
            )
        }
    }
}