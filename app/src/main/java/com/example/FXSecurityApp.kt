package com.example

import android.app.Application
import com.example.data.local.SecurityDatabase
import com.example.data.local.SecurityHistoryRepository
import com.example.data.local.UserPreferencesRepository
import com.example.data.scanner.AntiSpamAdvisor
import com.example.data.scanner.AppRiskAuditor
import com.example.data.scanner.DeviceSecurityAuditor

class FXSecurityApp : Application() {

    lateinit var database: SecurityDatabase
        private set

    lateinit var historyRepository: SecurityHistoryRepository
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var deviceSecurityAuditor: DeviceSecurityAuditor
        private set

    lateinit var appRiskAuditor: AppRiskAuditor
        private set

    lateinit var antiSpamAdvisor: AntiSpamAdvisor
        private set

    override fun onCreate() {
        super.onCreate()
        database = SecurityDatabase.getInstance(this)
        historyRepository = SecurityHistoryRepository(database.securityScanDao())
        preferencesRepository = UserPreferencesRepository(this)
        deviceSecurityAuditor = DeviceSecurityAuditor(this)
        appRiskAuditor = AppRiskAuditor(this)
        antiSpamAdvisor = AntiSpamAdvisor(this)
    }
}
