package ru.dinarastepina.besermyankb

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.IBinder
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import ru.dinarastepina.keyboardview.Keyboard
import ru.dinarastepina.keyboardview.KeyboardView

class BesermyanKeyboard : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    private lateinit var mInputManager: InputMethodManager
    private var token: IBinder? = null
    private var firstTime: Boolean = true
    private lateinit var keyboardView: KeyboardView
    override fun onPress(primaryCode: Int) {
    }

    override fun onRelease(primaryCode: Int) {

    }

    override fun onCreateInputView(): View {
        token = window.window?.attributes?.token
        keyboardView =  layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView

        val keyboard = Keyboard(this, R.xml.caps_pad);
        keyboardView.keyboard = keyboard
        keyboardView.isPreviewEnabled = false
        mInputManager =  getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        mInputManager.shouldOfferSwitchingToNextInputMethod(token)
        keyboardView.setOnKeyboardActionListener(this)
        return keyboardView;
    }


    override fun onComputeInsets(outInsets: Insets?) {
        super.onComputeInsets(outInsets)
        if(!isFullscreenMode) {
            outInsets?.contentTopInsets = outInsets?.visibleTopInsets
        }
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val inputConnection: InputConnection = currentInputConnection
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                inputConnection.deleteSurroundingText(1, 0)
                val currentText = inputConnection.getExtractedText(ExtractedTextRequest(), 0).text
                if (currentText.isEmpty()) {
                    switchKeyboard(R.xml.caps_pad)
                }
            }
            Keyboard.KEYCODE_DONE -> inputConnection.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
            )
            -6 -> mInputManager.switchToLastInputMethod(token)
            -2 -> switchKeyboard(R.xml.numbers_pad)
            -8 -> switchKeyboard(R.xml.caps_pad)
            -10, -11 -> switchKeyboard(R.xml.main_pad)
            else -> {
                val inserted = primaryCode.toChar()
                inputConnection.commitText(inserted.toString(), 1)
                if (firstTime || inserted.isUpperCase()) {
                    switchKeyboard(R.xml.main_pad)
                    firstTime = false
                }

                if (inserted.toString().trimEnd().endsWith(".")) {
                    switchKeyboard(R.xml.caps_pad)
                }
            }
        }
    }

    private fun switchKeyboard(pad: Int) {
        val keyboard = Keyboard(this as Context, pad);
        keyboardView.keyboard = keyboard
        keyboardView.isPreviewEnabled = false
        keyboardView.invalidateAllKeys()
        keyboardView.setOnKeyboardActionListener(this)
    }

    override fun onText(text: CharSequence?) {
    }

    override fun swipeLeft() {
    }

    override fun swipeRight() {
    }

    override fun swipeDown() {
    }

    override fun swipeUp() {
    }
}
