package tw.edu.pu.csim.shiqing.s1132245 // 僅在檔案頂部出現一次

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 定義畫面狀態的資料類別
data class ExamUiState(
    val author: String = "資管二B 洪詩晴", // 您的系級與姓名
    val score: Int = 0,
    val screenWidthPx: Float = 0f,
    val screenHeightPx: Float = 0f,

    // V4 新增的狀態
    val dropIconId: Int = R.drawable.service0, // 隨機圖示的資源ID (預設值)
    val dropIconY: Float = 0f,             // 圖示的垂直位置 (Y 座標)
    val dropIconX: Float = 0f,             // 圖示的水平位置 (X 座標，用於拖曳)
    val iconWidthPx: Float = 300f,         // 假設掉落圖示的寬度是 300px
    val iconHeightPx: Float = 300f         // 假設掉落圖示的高度是 300px
)
)

class ExamViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState

    private val iconResources = listOf(
        R.drawable.service0, R.drawable.service0, R.drawable.service0,
        R.drawable.service1, R.drawable.service2, R.drawable.service0,
        R.drawable.service1, R.drawable.service2, R.drawable.service3
    )
    data class ExamUiState(
        // ...
        val dropIconId: Int = R.drawable.service1, // 預設圖示ID
        // ...
    )
    init {
        // 在 ViewModel 啟動時開始掉落循環
        startDropping()
    }

    // 更新螢幕寬高的方法
    fun updateScreenDimensions(width: Float, height: Float) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                screenWidthPx = width,
                screenHeightPx = height
            )
        }
        // 隨機生成新圖示並重設位置到頂部中央
        fun resetDropIcon() {
            // 隨機選擇一個圖示ID
            val newIconId = iconResources[Random.nextInt(iconResources.size)]

            // 將 X 座標重設到中央 (螢幕寬度 / 2 - 圖示寬度 / 2)
            val centerX = (_uiState.value.screenWidthPx / 2f) - (_uiState.value.iconWidthPx / 2f)

            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    dropIconId = newIconId,
                    dropIconY = 0f,  // Y 座標重設為頂部
                    dropIconX = centerX // X 座標重設為中央
                )
            }
        }

        // 處理掉落動畫的主邏輯 (每 0.1s 掉落 20px)
        private fun startDropping() {
            viewModelScope.launch {
                // 確保螢幕尺寸先被設定
                while (_uiState.value.screenHeightPx == 0f) {
                    kotlinx.coroutines.delay(100)
                }

                // 第一次啟動時，重設圖示到頂部中央
                resetDropIcon()

                while (true) {
                    kotlinx.coroutines.delay(100) // 延遲 0.1 秒

                    val currentY = _uiState.value.dropIconY
                    val screenH = _uiState.value.screenHeightPx
                    val iconH = _uiState.value.iconHeightPx

                    // 檢查是否碰到螢幕底部 (圖示底部 Y > 螢幕高度)
                    if (currentY + iconH > screenH) {
                        resetDropIcon() // 碰到底部，重新生成並回到頂部
                    } else {
                        // 向下掉落 20px
                        _uiState.value = _uiState.value.copy(dropIconY = currentY + 20f)
                    }
                }
            }
        }

        // 處理水平拖曳的更新
        fun updateDropIconX(deltaX: Float) {
            val currentX = _uiState.value.dropIconX
            val screenW = _uiState.value.screenWidthPx
            val iconW = _uiState.value.iconWidthPx

            // 計算新的 X 位置，並限制在螢幕寬度內
            val newX = (currentX + deltaX).coerceIn(0f, screenW - iconW)

            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(dropIconX = newX)
            }
        }
    }