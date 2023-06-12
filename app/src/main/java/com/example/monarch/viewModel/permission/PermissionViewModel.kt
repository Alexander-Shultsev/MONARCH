package com.example.monarch.viewModel.permission

import android.app.AppOpsManager
import android.os.Build
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monarch.di.App.Companion.getContextInstance
import com.example.monarch.viewModel.common.SharedPreference
import com.example.monarch.viewModel.common.SharedPreferences

class PermissionViewModel: ViewModel() {

    private val sharedPreference = SharedPreference(getContextInstance())

    // вызываемое событие (переход на экран запроса разрешения)
    private val _action: MutableLiveData<Action> = MutableLiveData()
    val action: LiveData<Action> = _action

    // полученное значение
    private val _stateUsagePermission: MutableLiveData<Boolean> = MutableLiveData()
    val stateUsagePermission: LiveData<Boolean> = _stateUsagePermission

    // проверка выдачи разрешения на получение статистики использования приложений
    private val appOpsManager: AppOpsManager =
        getContextInstance().getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager

    init {
        _stateUsagePermission.value = checkUsageStatsPermission()
    }

    // выдача разрешение на сбор данных об использовании устройства
    fun setGrantedUsageStatsPermission() {
        sharedPreference.save(SharedPreferences.isTimeUsedPermission, true)
    }

    // события от приложения
    class Action(private var value: Int) {
        companion object {
            const val QUERY_PERMISSION_STATE_USED = 0
        }

        fun getValue(): Int = value

        fun setValue(value: Int) {
            this.value = value
        }
    }

    // проверка наличия разрешения
    private fun checkUsageStatsPermission(): Boolean {
        val context = getContextInstance()

        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android-sdk => 29
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(),
                context.packageName
            )
        } else {
            appOpsManager.checkOpNoThrow( // android-sdk < 29
                "android:get_usage_stats",
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // запуск запроса на разрешение при его отсутствии
    fun isUsageStatsPermission() {
        _stateUsagePermission.value = checkUsageStatsPermission()

        if (!_stateUsagePermission.value!!) { // если нет разрешений, дать их
            _action.value = Action(Action.QUERY_PERMISSION_STATE_USED)
        }
    }
}