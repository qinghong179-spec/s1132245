package tw.edu.pu.csim.shiqing.s1132245 // 僅在檔案頂部出現一次

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 定義畫面狀態的資料類別
data class ExamUiState(
    val author: String = "資管一B 洪詩晴", // 您的系級與姓名
    val score: Int = 0,
    val screenWidthPx: Float = 0f,
    val screenHeightPx: Float = 0f
)

class ExamViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState

    // 更新螢幕寬高的方法
    fun updateScreenDimensions(width: Float, height: Float) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                screenWidthPx = width,
                screenHeightPx = height
            )
        }
    }
}