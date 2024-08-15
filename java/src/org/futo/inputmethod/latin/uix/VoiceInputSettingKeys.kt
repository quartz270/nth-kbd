package org.futo.inputmethod.latin.uix

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

val ENABLE_SOUND = SettingsKey(
    key = booleanPreferencesKey("enable_sounds"),
    default = true
)

val VERBOSE_PROGRESS = SettingsKey(
    key = booleanPreferencesKey("verbose_progress"),
    default = false
)

val ENABLE_ENGLISH = SettingsKey(
    key = booleanPreferencesKey("enable_english"),
    default = true
)

val ENABLE_MULTILINGUAL = SettingsKey(
    key = booleanPreferencesKey("enable_multilingual"),
    default = false
)

val DISALLOW_SYMBOLS = SettingsKey(
    key = booleanPreferencesKey("disallow_symbols"),
    default = true
)

val PREFER_BLUETOOTH = SettingsKey(
    key = booleanPreferencesKey("prefer_bluetooth_recording"),
    default = false
)

val AUDIO_FOCUS = SettingsKey(
    key = booleanPreferencesKey("request_audio_focus"),
    default = true
)

val OPEN_AI_BASE_URL = SettingsKey(
    key = stringPreferencesKey("open_ai_base_url"),
    default = "https://api.openai.com/v1"
)

val OPEN_AI_API_PATH = SettingsKey(
    key = stringPreferencesKey("open_ai_api_path"),
    default = "/chat/completions"
)

val OPEN_AI_KEY = SettingsKey(
    key = stringPreferencesKey("open_ai_key"),
    default = ""
)

val OPEN_AI_MODEL = SettingsKey(
    key = stringPreferencesKey("open_ai_model"),
    default = "gpt-4o-mini"
)

val TTS_ASSIST = SettingsKey(
    key = booleanPreferencesKey("tts_assist"),
    default = false
)

val AI_ASSIST = SettingsKey(
    key = booleanPreferencesKey("ai_assist"),
    default = false
)

val ENGLISH_MODEL_INDEX = SettingsKey(
    key = intPreferencesKey("english_model_index"),
    default = 0
)

val MULTILINGUAL_MODEL_INDEX = SettingsKey(
    key = intPreferencesKey("multilingual_model_index"),
    default = 1
)

val LANGUAGE_TOGGLES = SettingsKey(
    key = stringSetPreferencesKey("enabled_languages"),
    default = setOf()
)