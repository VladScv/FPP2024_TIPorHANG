package cat.fpp.tiporhang.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Surface
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import cat.fpp.tiporhang.R
import cat.fpp.tiporhang.data.models.GameManager
import kotlin.math.max
import kotlin.math.roundToInt

class KeyboardActivity : AppCompatActivity() {
    //Save a reference for each created button and its character value
    private val keyButtons = mutableMapOf<Char, Button>()

    //Delegates initialization to a getter to avoid problems when loading before context
    // properly set up, GameManager must be previously initialized in MainActivity
    // Here we are just getting already created instance
    private val gameManager: GameManager
        get() = GameManager.instance

    //Delegates initialization to a getter to avoid problems when loading before context properly setup
    private val keyboardLayout: GridLayout
        get() = findViewById<GridLayout>(R.id.keyboard_layout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard)
        createKeyboardLayout(keyButtons);
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    //Catch keystrokes on a keyboard to convert into valid input
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        event?.let {
            val keyChar = it.unicodeChar.toChar().uppercaseChar()
            if (keyButtons.containsKey(keyChar)) {
                resultOK(keyChar)
                return false
            }

        }
        return false

    }

    //------------------------------------------------------------------------------PRIVATE METHODS

    /**---------------------------------------------------------------------------------------------
    RESULT OK:
    Sets the intent result of this activity to the letter passed as parameter.
    Also ends the activity.
    Parameter (letter): Char to be set as result
    ---------------------------------------------------------------------------------------------**/

    private fun resultOK(letter: Char) {
        val returnIntent = Intent()
        val button = keyButtons[letter]!!
        formatButton(
            button, letter
        )
        button.setOnClickListener {}
        gameManager.enterLetter(letter)
        returnIntent.putExtra("result", letter.toString())
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    /**---------------------------------------------------------------------------------------------
    CREATE Keyboard Layout:
    Creates new full keyboard containing just each letter in alphabet
    It is responsible to create all buttons and returns the keymap
    Returns: MutableMap<Char,Button> with all created buttons indexed by alphabet letter value
    ---------------------------------------------------------------------------------------------**/
    private fun createKeyboardLayout(keymap: MutableMap<Char, Button>): MutableMap<Char, Button> {

        keyboardLayout.columnCount = calculateColumnCount()
        ('A'..'Z').forEach { c ->
            val button = createButton(c)
            keymap[c] = button
            keyboardLayout.addView(button)
        }
        val backbtn = Button(this)
        backbtn.text = "close"
        backbtn.layoutParams = GridLayout.LayoutParams().apply {
            this.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2)
            this.width = 400
            this.height = 200
            bottomMargin = 5
            rightMargin = 5
        }
        backbtn.background = ContextCompat.getDrawable(this, R.drawable.roundbtn)
        backbtn.setPadding(0)
        backbtn.textSize = if (isPortrait()) 10f else 10f

        backbtn.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("result", "")
            setResult(RESULT_CANCELED, returnIntent)
            finish()
        }
        keyboardLayout.addView(backbtn)

        return keymap
    }

    /**-------------------------------------------------------------------------------------------------
    CREATE BUTTON:
    Creates each keyButton.
    Parameter (letter): Char to be set as alphabet letter value for the new button
    -------------------------------------------------------------------------------------------------**/
    private fun createButton(letter: Char): Button {
        val button = Button(this)
        button.text = letter.toString()
        button.layoutParams = GridLayout.LayoutParams().apply {
            this.width = 200
            this.height = 200
            bottomMargin = 5
            rightMargin = 5
        }
        formatButton(button, letter)
        button.setPadding(0)
        button.textSize = if (isPortrait()) 30f else 20f
        button.setOnClickListener {
            resultOK(letter)
        }
        return button
    }

    private fun formatButton(button: Button, letter: Char) {
        button.background = ContextCompat.getDrawable(
            this, if (gameManager.getLettersOk().contains(letter)) R.drawable.button_ok
            else if (gameManager.getLettersNok().contains(letter)) R.drawable.button_nok
            else R.drawable.roundbtn
        )
        if (gameManager.getLettersAll().contains(letter)) {
            button.isClickable = false
            button.isActivated = false
        }
    }

    /**---------------------------------------------------------------------------------------------
    Calculate Column Count:
    Gets de maximum number of columns that fits in current width.
    Size values are still hardcoded.
    ---------------------------------------------------------------------------------------------**/

    private fun calculateColumnCount(): Int {
        //------------------------BOILERPLATE for accessing display metrics from different versions
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display
        } else {
            @Suppress("DEPRECATION") windowManager.defaultDisplay
        }
        // Create a new empty DisplayMetrics object
        val displayMetrics = DisplayMetrics()
        // assign our display to our DisplayMetrics object
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        // check if display is portrait
        val isPortrait =
            !(display?.rotation == Surface.ROTATION_90 || display?.rotation == Surface.ROTATION_270)
        // Calculate returning value (number of columns)
        //PORTRAIT: [ Screen Pixels * 3/4 (margins scale) ] / 200px (button size)
        //LANDSCAPE: [ Screen Pixels * 4/6 (margins scale) ] / 200px (button size)
        val retVal = displayMetrics.widthPixels.times(
            if (isPortrait) (3f / 4f)
            else (5f / 6f)
        ).div(200f).roundToInt()
        // Using max() function to ensure a minimum number of columns
        return max(
            if (isPortrait) 2 else 4, //---- MIN VALUE FOR Portrait / Landscape designs
            retVal
        )
    }

    fun isPortrait(): Boolean {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display
        } else {
            @Suppress("DEPRECATION") windowManager.defaultDisplay
        }
        // Create a new empty DisplayMetrics object
        val displayMetrics = DisplayMetrics()
        // assign our display to our DisplayMetrics object
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        // check if display is portrait
        return !(display?.rotation == Surface.ROTATION_90 || display?.rotation == Surface.ROTATION_270)
    }
}
