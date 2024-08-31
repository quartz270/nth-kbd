package org.futo.inputmethod.latin.uix.actions

import android.util.Log
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.futo.inputmethod.keyboard.internal.CloudTranslationHandler
import org.futo.inputmethod.keyboard.internal.TranslationHelper
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.Suggest.TAG
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionEditText
import org.futo.inputmethod.latin.uix.ActionWindow
import org.futo.inputmethod.latin.uix.LocalManager


@Composable
fun LanguageDropdown(
    languageList: Map<String, String>,
    mExpanded: MutableState<Boolean>,
    mSelectedText: MutableState<String>,
    sourceLang: Boolean
) {
    Row (modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceBright,
            shape = RoundedCornerShape(24.dp),
            onClick = { mExpanded.value = true },
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .padding(2.dp)
                .weight(1.0f)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(TranslationHelper.getLanguageName(mSelectedText.value), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            expanded = mExpanded.value,
            onDismissRequest = { mExpanded.value = false },
        ) {
            languageList.toSortedMap().forEach { (langCode, label) ->
                val isTargetAuto = !(!sourceLang && langCode === "auto")
                DropdownMenuItem(text = {
                    Text(label)
                }, onClick = {
                    mExpanded.value = false
                    mSelectedText.value = langCode
                }, enabled = isTargetAuto,
                trailingIcon = {
                    if(langCode === "auto") IconWithColor(iconId = R.drawable.auto_detect, iconColor = MaterialTheme.colorScheme.onBackground)
                })
            }
        }
    }
}

@Composable
fun LanguageSelector(modifier: Modifier, isLoading: Boolean, mSelectedLanguage1: MutableState<String>, mSelectedLanguage2: MutableState<String>) {
    val mExpanded1 = remember { mutableStateOf(false) }
    val mExpanded2 = remember { mutableStateOf(false) }
    val mLanguages = TranslationHelper.getLanguages();

    Row(modifier = modifier) {
        Column (modifier = Modifier.weight(5f)) {
            LanguageDropdown(mLanguages, mExpanded1, mSelectedLanguage1, true)
        }
        Column (modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically)) {
            if (isLoading) {
                CircularProgressIndicator()

            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    val icon = painterResource(id = R.drawable.nth_translate)
                    val color = MaterialTheme.colorScheme.onBackground
                    IconButton(onClick = {
                        if (mSelectedLanguage1.value == "auto") {
                            return@IconButton
                        }
                        val temp = mSelectedLanguage2.value
                        mSelectedLanguage2.value = mSelectedLanguage1.value + ""
                        mSelectedLanguage1.value = temp + ""
                    }) {
                        Canvas(modifier = modifier) {
                            translate(
                                left = this.size.width / 2.0f - icon.intrinsicSize.width / 2.0f,
                                top = this.size.height / 2.0f - icon.intrinsicSize.height / 2.0f
                            ) {
                                with(icon) {
                                    draw(
                                        icon.intrinsicSize,
                                        colorFilter = ColorFilter.tint(
                                            color
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Column (modifier = Modifier.weight(5f)) {
            LanguageDropdown(mLanguages, mExpanded2, mSelectedLanguage2, false)
        }
    }
}


@Composable
fun TranslationInputBox(text: MutableState<String>, placeHolder: String = "", fontSize: Float = 15f, callbackTextChange: (String) -> Int) {
    val context = LocalContext.current
    val manager = LocalManager.current

    val height = with(LocalDensity.current) {
        48.dp.toPx()
    }

    val inputType = EditorInfo.TYPE_CLASS_TEXT

    val color = LocalContentColor.current
    val hintColor = MaterialTheme.colorScheme.onBackground

    AndroidView(
        factory = {
            ActionEditText(context).apply {
                this.inputType = inputType

                setTextChangeCallback {
                    text.value = it
                    callbackTextChange(it)
                }
                hint = placeHolder
                setHintTextColor(hintColor.toArgb())

                setText(text.value)
                textSize = fontSize

                setTextColor(color.toArgb())

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                setHeight(height.toInt())

                val editorInfo = EditorInfo().apply {
                    this.inputType = inputType
                }
                onCreateInputConnection(editorInfo)

                manager.overrideInputConnection(inputConnection!!, editorInfo)

                requestFocus()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        onRelease = {
            manager.unsetInputConnection()
        }
    )
}


@Composable
fun SourceTextInput(modifier: Modifier, searchText: MutableState<String>, onTextChange: (String) -> Int) {
    Row(modifier = modifier) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceBright,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .padding(2.dp)
                .weight(1.0f)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.CenterStart,
            ) {
                TranslationInputBox(text = searchText, placeHolder = "Type text here to translate.", fontSize = 20f, callbackTextChange = onTextChange)
            }
        }
    }
}

@Composable
fun TranslateScreen(
    isLoading: Boolean,
    onTextChange: (String) -> Int,
    languageFrom: MutableState<String>, languageTo: MutableState<String>
) {
    val searchText =  remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1.0f)) {
            LanguageSelector(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                isLoading,
                mSelectedLanguage1 = languageFrom,
                mSelectedLanguage2 = languageTo,
            )
            SourceTextInput(
                modifier = Modifier
                    .weight(2.0f)
                    .fillMaxWidth(),
                searchText,
                onTextChange,
            )
        }
    }
}

val TranslateAction = Action(
    icon = R.drawable.nth_translate,
    name = R.string.translate_action_title,
    simplePressImpl = null,
    persistentState = null,
    canShowKeyboard = true,
    windowImpl = { manager, persistentState ->
        object : ActionWindow {
            @Composable
            override fun windowName(): String {
                return stringResource(R.string.translate_action_title)
            }

            @Composable
            override fun WindowContents(keyboardShown: Boolean) {
                val languageFrom = remember { mutableStateOf("auto") }
                val languageTo = remember { mutableStateOf("fr") }
                val isLoading = remember { mutableStateOf(false) }
                val error = remember { mutableStateOf<String?>(null) }

                val debounceJob = remember { mutableStateOf<Job?>(null) }

                val coroutineScope = rememberCoroutineScope()

                TranslateScreen(
                    isLoading = isLoading.value,
                    onTextChange = {updatedString ->
                        if (updatedString.isNotEmpty()) {
                            debounceJob.value?.cancel()
                            debounceJob.value = coroutineScope.launch {
                                isLoading.value = true
                                error.value = null
                                delay(500)
                                if (languageFrom.value == languageTo.value) {
                                    manager.replaceAllText(updatedString)
                                    isLoading.value = false
                                } else {
                                    val result = CloudTranslationHandler.translate(
                                        languageFrom.value,
                                        languageTo.value,
                                        updatedString
                                    )
                                    result.fold(
                                        onSuccess = {
                                            Log.d(
                                                TAG,
                                                "Translation Success"
                                            )
                                            manager.replaceAllText(it)
                                            isLoading.value = false
                                        },
                                        onFailure = {
                                            Log.d(
                                                TAG,
                                                "Translation fail"  
                                            )
                                            error.value = it.message
                                            isLoading.value = false
                                        }
                                    )
                                }
                            }
                        }
                        Log.d(TAG, "Initiated Translation")
                    },
                    languageFrom,
                    languageTo,
                )
            }

            override fun close() {
            }
        }
    }
)
