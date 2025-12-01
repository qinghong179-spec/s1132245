package tw.edu.pu.csim.shiqing.s1132245 // 僅在檔案頂部出現一次

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
import androidx.compose.foundation.layout.BoxWithConstraints // 2. BoxWithConstraints 匯入
import tw.edu.pu.csim.shiqing.s1132245.R // 1. R 資源匯入

@Composable
fun ExamScreen(
    viewModel: ExamViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val density = LocalDensity.current

    // 使用 BoxWithConstraints 獲取 Composable 的最大尺寸 (以 DP 為單位)
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = with(density) { constraints.maxWidth.toPx() }
        val heightPx = with(density) { constraints.maxHeight.toPx() }

        LaunchedEffect(Unit) {
            viewModel.updateScreenDimensions(widthPx, heightPx)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ... 圖片和文字部分保持不變 ...
                Image(
                    painter = painterResource(id = R.drawable.happy),
                    contentDescription = "Exam Logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "瑪利亞基金會服務大考驗",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "作者：${uiState.author}", // 顯示：資管一B 洪詩晴
                    fontSize = 16.sp
                )

                Text(
                    text = "螢幕大小：${"%.1f".format(uiState.screenWidthPx)} x ${
                        "%.1f".format(
                            uiState.screenHeightPx
                        )
                    } px",
                    fontSize = 14.sp
                )

                Text(
                    text = "成績：${uiState.score}分",
                    fontSize = 14.sp
                )
            }
        }
    }
}


