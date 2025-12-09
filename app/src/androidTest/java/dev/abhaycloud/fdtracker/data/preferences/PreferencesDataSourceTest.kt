package dev.abhaycloud.fdtracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Instrumented tests for DataStore preferences
 * Tests preference storage and retrieval
 */
@RunWith(AndroidJUnit4::class)
class PreferencesDataSourceTest {

    private lateinit var context: Context
    private lateinit var preferencesDataSource: PreferencesDataSource

    // Use a test-specific datastore
    //private val Context.testDataStore: DataStore<Preferences> by preferencesDataStore(
    //    name = "test_preferences"
    //)

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        preferencesDataSource = PreferencesDataSource(context)
    }

    @Test
    fun saveDarkMode_andRetrieve_returnsCorrectValue() = runBlocking {
        // Save dark mode as true
        preferencesDataSource.setDarkMode(true)

        // Retrieve and verify
        val result = preferencesDataSource.darkMode.first()
        assertTrue(result)
    }

    @Test
    fun saveDarkMode_withFalse_returnsCorrectValue() = runBlocking {
        // Save dark mode as false
        preferencesDataSource.setDarkMode(false)

        // Retrieve and verify
        val result = preferencesDataSource.darkMode.first()
        assertFalse(result)
    }

    @Test
    fun getDarkMode_withoutSaving_returnsDefaultValue() = runBlocking {
        // Without saving, should return default (false)
        val result = preferencesDataSource.darkMode.first()
        assertFalse(result)
    }

    @Test
    fun saveDynamicColor_andRetrieve_returnsCorrectValue() = runBlocking {
        // Save dynamic color as true
        preferencesDataSource.setDynamicColor(true)

        // Retrieve and verify
        val result = preferencesDataSource.dynamicColor.first()
        assertTrue(result)
    }

    @Test
    fun setDynamicColor_withFalse_returnsCorrectValue() = runBlocking {
        // Save dynamic color as false
        preferencesDataSource.setDynamicColor(false)

        // Retrieve and verify
        val result = preferencesDataSource.dynamicColor.first()
        assertFalse(result)
    }

    @Test
    fun getDynamicColor_withoutSaving_returnsDefaultValue() = runBlocking {
        // Without saving, should return default (false)
        val result = preferencesDataSource.dynamicColor.first()
        assertFalse(result)
    }

    @Test
    fun saveBiometricAuth_andRetrieve_returnsCorrectValue() = runBlocking {
        // Save biometric auth as true
        preferencesDataSource.setBiometricAuth(true)

        // Retrieve and verify
        val result = preferencesDataSource.biometricAuth.first()
        assertTrue(result)
    }

    @Test
    fun saveBiometricAuth_withFalse_returnsCorrectValue() = runBlocking {
        // Save biometric auth as false
        preferencesDataSource.setBiometricAuth(false)

        // Retrieve and verify
        val result = preferencesDataSource.biometricAuth.first()
        assertFalse(result)
    }

    @Test
    fun multiplePreferences_canBeSavedAndRetrieved() = runBlocking {
        // Save multiple preferences
        preferencesDataSource.setDarkMode(true)
        preferencesDataSource.setDynamicColor(false)
        preferencesDataSource.setBiometricAuth(true)

        // Retrieve and verify all
        val darkMode = preferencesDataSource.darkMode.first()
        val dynamicColor = preferencesDataSource.dynamicColor.first()
        val biometricAuth = preferencesDataSource.biometricAuth.first()

        assertTrue(darkMode)
        assertFalse(dynamicColor)
        assertTrue(biometricAuth)
    }

    @Test
    fun updatePreference_overwritesPreviousValue() = runBlocking {
        // Save initial value
        preferencesDataSource.setDarkMode(true)
        var result = preferencesDataSource.darkMode.first()
        assertTrue(result)

        // Update to new value
        preferencesDataSource.setDarkMode(false)
        result = preferencesDataSource.darkMode.first()
        assertFalse(result)
    }

    @Test
    fun preferences_persistAcrossInstances() = runBlocking {
        // Save with first instance
        preferencesDataSource.setDarkMode(true)

        // Create new instance and verify value persists
        val newInstance = PreferencesDataSource(context)
        val result = newInstance.darkMode.first()
        assertTrue(result)
    }

    @Test
    fun flowUpdates_whenPreferenceChanges() = runBlocking {
        // Get initial value
        val initial = preferencesDataSource.darkMode.first()
        assertFalse(initial)

        // Update preference
        preferencesDataSource.setDarkMode(true)

        // Get updated value
        val updated = preferencesDataSource.darkMode.first()
        assertTrue(updated)
    }

    @Test
    fun rapidUpdates_handleCorrectly() = runBlocking {
        // Rapidly update preference multiple times
        preferencesDataSource.setDarkMode(true)
        preferencesDataSource.setDarkMode(false)
        preferencesDataSource.setDarkMode(true)
        preferencesDataSource.setDarkMode(false)

        // Final value should be false
        val result = preferencesDataSource.darkMode.first()
        assertFalse(result)
    }

    @Test
    fun allPreferencesToggles_workIndependently() = runBlocking {
        // Set all to true
        preferencesDataSource.setDarkMode(true)
        preferencesDataSource.setDynamicColor(true)
        preferencesDataSource.setBiometricAuth(true)

        // Verify all are true
        assertTrue(preferencesDataSource.darkMode.first())
        assertTrue(preferencesDataSource.dynamicColor.first())
        assertTrue(preferencesDataSource.biometricAuth.first())

        // Toggle only dark mode
        preferencesDataSource.setDarkMode(false)

        // Verify only dark mode changed
        assertFalse(preferencesDataSource.darkMode.first())
        assertTrue(preferencesDataSource.dynamicColor.first())
        assertTrue(preferencesDataSource.biometricAuth.first())
    }
}
