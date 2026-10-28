package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SecurityHistoryRepository
import com.example.data.local.SecurityScanEntity
import com.example.data.local.UserPreferencesRepository
import com.example.data.model.AppRiskInfo
import com.example.data.model.SavageShieldStyle
import com.example.data.model.SpamIncident
import com.example.data.model.SystemSecurityReport
import com.example.data.model.ThreatLevel
import com.example.data.scanner.AntiSpamAdvisor
import com.example.data.scanner.AppRiskAuditor
import com.example.data.scanner.DeviceSecurityAuditor
import com.example.ui.theme.FXThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SecurityViewModel(
    private val deviceAuditor: DeviceSecurityAuditor,
    private val appAuditor: AppRiskAuditor,
    private val spamAdvisor: AntiSpamAdvisor,
    private val historyRepo: SecurityHistoryRepository,
    private val prefsRepo: UserPreferencesRepository
) : ViewModel() {

    val themeMode: StateFlow<FXThemeMode> = prefsRepo.themeMode
    val savageStyle: StateFlow<SavageShieldStyle> = prefsRepo.savageStyle
    val savagePopupEnabled: StateFlow<Boolean> = prefsRepo.savagePopupEnabled
    val aggressiveSpamFilter: StateFlow<Boolean> = prefsRepo.aggressiveSpamFilter
    val autoScanEnabled: StateFlow<Boolean> = prefsRepo.autoScanEnabled
    val notifyOnThreats: StateFlow<Boolean> = prefsRepo.notifyOnThreats

    val scanHistory: StateFlow<List<SecurityScanEntity>> = historyRepo.allScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentReport = MutableStateFlow<SystemSecurityReport?>(null)
    val currentReport: StateFlow<SystemSecurityReport?> = _currentReport.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _scanCurrentStage = MutableStateFlow("Ready for diagnostic")
    val scanCurrentStage: StateFlow<String> = _scanCurrentStage.asStateFlow()

    // App Risk Scanner states
    private val _appRisks = MutableStateFlow<List<AppRiskInfo>>(emptyList())
    val appRisks: StateFlow<List<AppRiskInfo>> = _appRisks.asStateFlow()

    private val _isScanningApps = MutableStateFlow(false)
    val isScanningApps: StateFlow<Boolean> = _isScanningApps.asStateFlow()

    private val _appSearchQuery = MutableStateFlow("")
    val appSearchQuery: StateFlow<String> = _appSearchQuery.asStateFlow()

    private val _selectedRiskFilter = MutableStateFlow<ThreatLevel?>(null)
    val selectedRiskFilter: StateFlow<ThreatLevel?> = _selectedRiskFilter.asStateFlow()

    // Anti-Spam incidents state
    private val _spamIncidents = MutableStateFlow<List<SpamIncident>>(emptyList())
    val spamIncidents: StateFlow<List<SpamIncident>> = _spamIncidents.asStateFlow()

    init {
        _spamIncidents.value = spamAdvisor.getSampleIncidents()
        // Run initial fast scan on startup
        runFullScan(quickMode = true)
        loadAppRisks()
    }

    fun runFullScan(quickMode: Boolean = false) {
        if (_isScanning.value) return
        viewModelScope.launch {
            _isScanning.value = true
            _scanProgress.value = 0.1f

            val stages = listOf(
                "Checking Linux kernel integrity & superuser paths..." to 0.25f,
                "Auditing ADB debug bridge & developer status..." to 0.45f,
                "Verifying Keyguard hardware credential encryption..." to 0.65f,
                "Scanning network interfaces & transport layer..." to 0.85f,
                "Synthesizing threat diagnostics..." to 1.0f
            )

            for ((stageText, progressVal) in stages) {
                _scanCurrentStage.value = stageText
                _scanProgress.value = progressVal
                if (!quickMode) {
                    delay(300)
                }
            }

            val report = withContext(Dispatchers.IO) {
                deviceAuditor.executeFullAudit()
            }
            _currentReport.value = report

            // Persist report in Room database
            withContext(Dispatchers.IO) {
                val findingsSummary = report.checks
                    .filter { !it.isPassed }
                    .joinToString("; ") { "${it.title}: ${it.description}" }
                    .ifEmpty { "All security parameters verified nominal." }

                val recommended = when {
                    report.highRiskCount > 0 -> "Resolve critical issues immediately: review Lock Screen or Device Tampering."
                    report.moderateRiskCount > 0 -> "Address moderate items: disable USB debugging when not in use."
                    else -> "Device integrity fully safeguarded. Maintain regular scans."
                }

                val entity = SecurityScanEntity(
                    timestamp = report.scanTimestamp,
                    score = report.score,
                    overallStatus = report.overallStatus,
                    totalChecks = report.checks.size,
                    passedChecks = report.passedCount,
                    criticalIssues = report.highRiskCount,
                    moderateIssues = report.moderateRiskCount,
                    recommendedAction = recommended,
                    summaryFindings = findingsSummary
                )
                historyRepo.recordScan(entity)
            }

            _scanCurrentStage.value = "Diagnostic complete"
            _isScanning.value = false
        }
    }

    fun loadAppRisks() {
        if (_isScanningApps.value) return
        viewModelScope.launch {
            _isScanningApps.value = true
            val apps = withContext(Dispatchers.IO) {
                appAuditor.scanInstalledApps()
            }
            _appRisks.value = apps
            _isScanningApps.value = false
        }
    }

    fun setAppSearchQuery(query: String) {
        _appSearchQuery.value = query
    }

    fun setRiskFilter(level: ThreatLevel?) {
        _selectedRiskFilter.value = if (_selectedRiskFilter.value == level) null else level
    }

    fun addManualSpamIncident(numberOrSender: String, type: String, note: String) {
        val newIncident = SpamIncident(
            id = "manual-${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            senderOrCaller = numberOrSender,
            incidentType = type,
            contentPreview = note,
            actionTaken = "User Flagged & Carrier Heuristic Recorded",
            isDemo = false
        )
        _spamIncidents.value = listOf(newIncident) + _spamIncidents.value
    }

    fun clearSpamHistory() {
        _spamIncidents.value = emptyList()
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepo.deleteScan(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepo.clearHistory()
        }
    }

    fun setThemeMode(mode: FXThemeMode) {
        prefsRepo.setThemeMode(mode)
    }

    fun setSavageStyle(style: SavageShieldStyle) {
        prefsRepo.setSavageStyle(style)
    }

    fun setSavagePopupEnabled(enabled: Boolean) {
        prefsRepo.setSavagePopupEnabled(enabled)
    }

    fun setAggressiveSpamFilter(enabled: Boolean) {
        prefsRepo.setAggressiveSpamFilter(enabled)
    }

    fun setAutoScanEnabled(enabled: Boolean) {
        prefsRepo.setAutoScanEnabled(enabled)
    }

    fun setNotifyOnThreats(enabled: Boolean) {
        prefsRepo.setNotifyOnThreats(enabled)
    }

    class Factory(
        private val deviceAuditor: DeviceSecurityAuditor,
        private val appAuditor: AppRiskAuditor,
        private val spamAdvisor: AntiSpamAdvisor,
        private val historyRepo: SecurityHistoryRepository,
        private val prefsRepo: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SecurityViewModel(
                deviceAuditor,
                appAuditor,
                spamAdvisor,
                historyRepo,
                prefsRepo
            ) as T
        }
    }
}
