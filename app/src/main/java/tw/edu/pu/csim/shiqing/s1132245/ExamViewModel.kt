// ExamViewModel.kt

package tw.edu.pu.csim.shiqing.s1132245

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import tw.edu.pu.csim.shiqing.s1132245.R // 確保 R 資源正確匯入

// 定義常數
private const val DROP_ICON_SIZE_PX = 300f
private const val ROLE_ICON_SIZE_DP = 300f

// 角色圖示邊界結構
data class RoleBounds(
    val roleName: String,
    var left: Float = 0f,
    var top: Float = 0f,
    var right: Float = 0f,
    var bottom: Float = 0f
)

// ExamUiState 結構
data class ExamUiState(
    val author: String = "資管一B 洪詩晴", // 您的姓名
    var score: Int = 0,
    val screenWidthPx: Float = 0f,
    val screenHeightPx: Float = 0f,

    // V4 掉落圖示狀態 (使用 service0 作為預設圖示)
    val dropIconId: Int = R.drawable.service0,
    val dropIconY: Float = 0f,
    val dropIconX: Float = 0f,
    val iconWidthPx: Float = DROP_ICON_SIZE_PX,
    val iconHeightPx: Float = DROP_ICON_SIZE_PX,

    // V5 碰撞訊息狀態
    val message: String = ""
)

class ExamViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState

    // 9 個服務圖示的資源 ID 列表 (請檢查 service0 到 service8 是否齊全!)
    private val iconResources = listOf(
        R.drawable.service0, R.drawable.service1, R.drawable.service2,
        R.drawable.service3, R.drawable.service1, R.drawable.service2,
        R.drawable.service3, R.drawable.service0, R.drawable.service1
    )

    // 儲存四個角色圖示的邊界 (順序需與 ExamScreen.kt 的定位邏輯一致)
    private val roleIcons = listOf(
        // 1. 嬰幼兒 (左上 1/2) - 對應 R.drawable.role0
        RoleBounds("碰撞嬰幼兒"),
        // 2. 兒童 (右上 1/2) - 對應 R.drawable.role0
        RoleBounds("碰撞兒童"),
        // 3. 成人 (左下) - 對應 R.drawable.role1
        RoleBounds("碰撞成人"),
        // 4. 一般民眾 (右下) - 對應 R.drawable.role3
        RoleBounds("碰撞一般民眾")
    )

    private var densityRatio: Float = 1f

    init {
        startDropping()
    }

    // 新增：設定 DP 到 PX 的轉換比例 (從 ExamScreen.kt 傳入)
    fun setDensityRatio(ratio: Float) {
        densityRatio = ratio
    }

    fun updateScreenDimensions(width: Float, height: Float) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                screenWidthPx = width,
                screenHeightPx = height
            )
            // 確保 X 座標設定為中央
            if (_uiState.value.dropIconX == 0f) {
                val centerX = (width / 2f) - (DROP_ICON_SIZE_PX / 2f)
                _uiState.value = _uiState.value.copy(dropIconX = centerX)
            }

            // 重新計算碰撞邊界
            calculateRoleBounds(width, height)
        }
    }

    private fun calculateRoleBounds(width: Float, height: Float) {
        if (densityRatio == 0f) return

        // 將 300dp 轉換為 px 單位
        val roleIconSizePx = ROLE_ICON_SIZE_DP * densityRatio
        val halfScreenHeight = height / 2f

        // 1. 嬰幼兒 (左上 1/2)
        roleIcons[0].apply {
            left = 0f
            top = halfScreenHeight - roleIconSizePx
            right = roleIconSizePx
            bottom = halfScreenHeight
        }

        // 2. 兒童 (右上 1/2)
        roleIcons[1].apply {
            left = width - roleIconSizePx
            top = halfScreenHeight - roleIconSizePx
            right = width
            bottom = halfScreenHeight
        }

        // 3. 成人 (左下)
        roleIcons[2].apply {
            left = 0f
            top = height - roleIconSizePx
            right = roleIconSizePx
            bottom = height
        }

        // 4. 一般民眾 (右下)
        roleIcons[3].apply {
            left = width - roleIconSizePx
            top = height - roleIconSizePx
            right = width
            bottom = height
        }
    }

    private fun resetDropIcon() {
        val newIconId = iconResources[Random.nextInt(iconResources.size)]

        val centerX = (_uiState.value.screenWidthPx / 2f) - (DROP_ICON_SIZE_PX / 2f)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                dropIconId = newIconId,
                dropIconY = 0f,
                dropIconX = centerX
            )
        }
    }

    // 碰撞偵測函式 (AABB 軸對齊邊界框)
    private fun checkCollision(dropX: Float, dropY: Float): RoleBounds? {
        val dropRight = dropX + DROP_ICON_SIZE_PX
        val dropBottom = dropY + DROP_ICON_SIZE_PX

        for (role in roleIcons) {
            val overlapX = dropX < role.right && dropRight > role.left
            val overlapY = dropY < role.bottom && dropBottom > role.top

            if (overlapX && overlapY) {
                return role
            }
        }
        return null
    }

    private fun startDropping() {
        viewModelScope.launch {
            while (_uiState.value.screenHeightPx == 0f || densityRatio == 0f) {
                kotlinx.coroutines.delay(100)
            }

            resetDropIcon()

            while (true) {
                kotlinx.coroutines.delay(100)

                // 檢查遊戲是否暫停
                if (_uiState.value.isGamePaused) continue

                val currentY = _uiState.value.dropIconY
                val currentX = _uiState.value.dropIconX
                val screenH = _uiState.value.screenHeightPx
                val iconH = _uiState.value.iconHeightPx

                val collidedRole = checkCollision(currentX, currentY)

                var newState = _uiState.value
                var shouldReset = false
                var message = ""

                if (collidedRole != null) {
                    // 1. 發生碰撞
                    shouldReset = true
                    val correctRoleName = SERVICE_ANSWERS[dropIconId]

                    // 判斷答案是否正確
                    if (collidedRole.roleName == correctRoleName) {
                        newState = newState.copy(score = newState.score + 1) // 正確：加一分
                    } else {
                        newState = newState.copy(score = newState.score - 1) // 錯誤：減一分
                    }

                    // 顯示 Toast 訊息的內容
                    message =
                        "${getServiceName(dropIconId)}，屬於${correctRoleName!!.removePrefix("碰撞")}的服務" //

                } else if (currentY + iconH > screenH && screenH != 0f) {
                    // 2. 掉落邊界
                    shouldReset = true

                    // 掉落邊界，減一分
                    newState = newState.copy(score = newState.score - 1)

                    val correctRoleName = SERVICE_ANSWERS[dropIconId]
                    message =
                        "${getServiceName(dropIconId)}，屬於${correctRoleName!!.removePrefix("碰撞")}的服務" // 顯示答案

                } else {
                    // 3. 繼續掉落
                    newState = newState.copy(
                        dropIconY = currentY + 20f,
                        message = "" // 清除訊息
                    )
                }

                if (shouldReset) {
                    // V6: 碰撞或掉落後，暫停 3 秒，並顯示 Toast
                    newState = newState.copy(isGamePaused = true)
                    _uiState.value = newState

                    showToastMessage(message) // 顯示 Toast 提示

                    kotlinx.coroutines.delay(3000) // 暫停 3 秒

                    // 暫停結束，重置並繼續遊戲
                    resetDropIcon()
                    _uiState.value = _uiState.value.copy(isGamePaused = false, message = "")

                } else {
                    _uiState.value = newState
                }
            }
        }
    }

    // 實用工具函式 (需要實現 Toast 和資源名稱獲取)
    private fun getServiceName(id: Int): String {
        // 這裡需要根據您的 R.drawable.serviceX 實際名稱返回中文名稱
        // 假設 service0 = "極早期療育", service1 = "華齡服務"
        return when (id) {
            R.drawable.service0 -> "極早期療育"
            R.drawable.service1 -> "華齡服務"
            R.drawable.service2 -> "極重多障"
            R.drawable.service3 -> "輔具服務"
            else -> "某服務"
        }
    }

    data class ExamUiState(
        // ...
        val toastMessage: String = "" // V6: 新增用於 Toast 的訊息
    )
}

