package cat.fpp.tiporhang.ui.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Layout.Alignment
import android.view.KeyEvent
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import cat.fpp.tiporhang.R
import cat.fpp.tiporhang.data.models.GameManager

class MainActivity : AppCompatActivity() {

    /**---------------------------------------------------------------------------------------------
     *                                                                           Global Declarations
     * -------------------------------------------------------------------------------------------*/
    private var init: Boolean = false
    private lateinit var gameManager: GameManager
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var imageFrame: FrameLayout
    private lateinit var resultsViewGroup: ConstraintLayout
    private lateinit var successView: TextView
    private lateinit var failView: TextView
    private lateinit var wordView: TextView
    private lateinit var buttonGroup: Group
    private lateinit var inputButton: ImageButton
    private lateinit var helpButton: ImageButton
    private lateinit var resetButton: ImageButton
    private lateinit var consoleMessage: TextView

    /**---------------------------------------------------------------------------------------------
     *                                                                           OVERRIDE FUNCTIONS
     * -------------------------------------------------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        imageFrame = findViewById<FrameLayout>(R.id.image_frame)
        //Initiate gameManager who owns all game data
        gameManager = GameManager(
            this, imageFrame
        )
        //Setting up a result listener that updates game
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    updateViews()
                }
            }
        findAllViews()
        initialState()
    }

    //Catch keyboard strokes events to turn it into valid inputs
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        event?.let {
            val keyChar: Char = it.unicodeChar.toChar().uppercaseChar()
            if (('A'..'Z').contains(keyChar) && !gameManager.getLettersAll().contains(keyChar))
                gameManager.enterLetter(keyChar)
            else if (it.unicodeChar.toChar() == '.')
                setTemporalMessage(gameManager.viewDevMessage())
            else if (it.unicodeChar == '-'.code)
                setTemporalMessage(gameManager.viewWordHint(),1750)
            updateViews()
        }
        return false
    }

    //Allows going back standard behaviour
    override fun onSupportNavigateUp(): Boolean {
        resetGame(true)
        return false
    }

    /**---------------------------------------------------------------------------------------------
     *                                                                           Creation Functions
     * -------------------------------------------------------------------------------------------*/

    //Convenient method to encapsulate all findView calls
    private fun findAllViews() {
        mainLayout = findViewById(R.id.main)
        resetButton = findViewById<ImageButton>(R.id.resetButton)
        inputButton = findViewById<ImageButton>(R.id.inputButton)
        helpButton = findViewById<ImageButton>(R.id.helpButton)
        consoleMessage = findViewById(R.id.consolemessage)
        resultsViewGroup = findViewById<ConstraintLayout>(R.id.frameLayout)
        successView = findViewById(R.id.successletters)
        failView = findViewById(R.id.failletters)
        wordView = findViewById(R.id.displayWord)
        buttonGroup = findViewById(R.id.buttongroup)
    }

    //Sets the game to its first screen configuration
    private fun initialState() {
        init=false
        imageFrame.run {
            setOnClickListener {
                if (!init) updateViews()
                else if (gameManager.isLastScreen()) resetGame(false)
                else launchKeyboard()
            }
            isFocusable = false
        }
        wordView.run{
            letterSpacing = 0.1f
            isFocusable=false
            text = "-- TOUCH TO START -- "
        }
        //SETUP INPUT BUTTON
        inputButton.setOnClickListener {
            if (!init) updateViews()
            else if (gameManager.isLastScreen()) resetGame(false)
            else launchKeyboard()
        }
        //SETUP HELP BUTTON
        helpButton.run {
            setOnClickListener {
                if (!init) updateViews()
                else if (gameManager.isLastScreen()) resetGame(false)
                else {
                    setTemporalMessage(gameManager.viewWordHint())
                    updateViews()
                }
            }
            isFocusable=false
            visibility = INVISIBLE
        }

        //SETUP RESET BUTTON
        resetButton.run {
            setOnClickListener {
                resetGame(false)
            }
            isFocusable=false
        }

        //SETUP START MESSAGE
        consoleMessage.run {
            textAlignment = Alignment.ALIGN_CENTER.ordinal
            consoleMessage.text = StringBuilder()
                .append("\n\n\n")
                .append("** Pres '_' to get a hint \n ")
                .append("** Press '.' to enter developer mode\n")
            isFocusable=false
        }
        buttonGroup.visibility = INVISIBLE
        resultsViewGroup.visibility = INVISIBLE
        failView.text = ""
        successView.text = ""
    }


    /**---------------------------------------------------------------------------------------------
     *                                                                           Game Management
     * -------------------------------------------------------------------------------------------*/

    private fun resetGame(fullGame: Boolean) {
        if (fullGame) initialState()
        gameManager.updateGameData(true)
        gameManager.reloadImage()
        if (!fullGame) updateViews()
    }

    // Method that forces screen updating
    // Also updates all data present in gameManager before render views
    private fun updateViews() {
        if (!init)init=true
        wordView.text = gameManager.getWordFormatted()
        consoleMessage.text = gameManager.message
        val sb = StringBuilder()
        gameManager.getLettersOk().toCharArray().forEachIndexed() { i, c ->
            sb.append(
                if (i > 0) ", " else ""
            ).append(c)
        }
        successView.text = sb.toString()
        sb.clear()
        gameManager.getLettersNok().toCharArray().forEachIndexed() { i, c ->
            sb.append(
                if (i > 0) ", " else ""
            ).append(c)
        }
        failView.text = sb.toString()
        if (gameManager.isFirstScreen()) {
            resultsViewGroup.visibility = VISIBLE
            helpButton.visibility = VISIBLE
            buttonGroup.visibility = VISIBLE
        }
        gameManager.updateGameData(false)
    }

    //Launches own keyboard activity
    private fun launchKeyboard() {
        val intent = Intent(this, KeyboardActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun setTemporalMessage(oldMessage: String, ms: Long = 1000) {
        Handler().postDelayed({
            gameManager.message = oldMessage
            updateViews()
        }, ms)
    }

}
