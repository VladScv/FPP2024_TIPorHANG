package cat.fpp.tiporhang.data.models

import android.content.Context
import android.widget.FrameLayout
import cat.fpp.tiporhang.R
import cat.fpp.tiporhang.data.models.utils.ImageIndex
import java.lang.reflect.Field
import java.util.Locale
import kotlin.random.Random

class GameManager(context: Context, frameLayout: FrameLayout)  {

    /**---------------------------------------------------------------------------------------------
     *                                                      Global ATTRIBUTES and CONSTRUCTOR
     * -------------------------------------------------------------------------------------------*/
    var message: String = "- Enter a letter -"
    private val MAX_ERRORS: Int = 6
    //Encapsulamos la gestion de las imagenes en un objeto aparte
    private val imageManager: ImageManager = ImageManager.getInstance(context, frameLayout)
    private var wordMap: Map<String,String>?=null
    // Mapa para guardar las letras introducidas y si son correctas o no
    private val enteredLetters: MutableMap<Char, Boolean> = mutableMapOf<Char, Boolean>()
    private lateinit var magicWord: String

    init {
        instance=this
        selectWord(context)
        resetGameData()
    }


    /**---------------------------------------------------------------------------------------------
     *                                                                           PUBLIC METHODS
     * -------------------------------------------------------------------------------------------*/

    /**-------------------------------------------------- CHECK GAME STATE METHODS ----------------*/
    /**Metodo para verificar si estamos en la PRIMERA pantalla*/
    fun isFirstScreen(): Boolean { return this.enteredLetters.isEmpty()}
    /**Metodo para verificar si estamos en la ultima pantalla (la que muestra You Win o Game Over)*/
    fun isLastScreen():Boolean{return hasLost()||hasWon()}
    /** Metodo para encapsular verificar si se han desvelado todas las letras */
    private fun hasWon():Boolean{return getLettersOk().containsAll(magicWordDistinctLetters())}

    /**Metodo para encapsular verificar si se han alcanzado el maximo de errores*/
    fun hasLost():Boolean{return getLettersNok().count() >= MAX_ERRORS}

    /**-------------------------------------------------- GET GAME-DATA METHODS ----------------*/
    /** obtener todos los aciertos */
    fun getLettersOk(): List<Char> {
        val retVal : MutableList<Char> = mutableListOf()
        enteredLetters.forEach { c, b ->   if(b) retVal += c }
        return retVal

    }

    /** Obtener todos los fallos */
    fun getLettersNok(): List<Char> {
        val retVal : MutableList<Char> = mutableListOf()
        enteredLetters.forEach { (c, b) ->   if(!b) retVal += c }
        return retVal
    }
    /** Obtener Todas las letras introducidas hasta el momento */
    fun getLettersAll(): List<Char> {
        return enteredLetters.keys.toList()
    }

    /** Metodo que devuelve las letras correctas ordenadas segun su posicion en la palabra secreta */
    fun getWordFormatted(): CharSequence {
        val stringBuilder = StringBuilder()
        this.magicWord.uppercase(Locale.getDefault()).forEach { letter ->
            stringBuilder.append(
                if (enteredLetters.containsKey(letter)) letter.toString()
                else '_'
            )
        }
        return stringBuilder
    }

    /**-------------------------------------------------- UPDATE GAME-DATA FUNCTIONS ----------------*/

    /** Metodo para añadir una nueva letra
     * @param letter: Char que verificar
     * @return Integer -1: La letra ya estaba registrada, no deberia enviarse
     * 0: La letra no estaba registrada pero no es correcta
     * CHAR_VALUE: El valor entero de la tecla añadida para poderla recuperar facilmente
     */
    fun enterLetter(letter: Char){
        //Si la letra ya estaba registrada devuelve -1 sin hacer nada mas
        if (enteredLetters.containsKey(letter.uppercaseChar())) return
        //La letra no estaba registrada, ahora checkeamos si es correcta
        val checkLetter = magicWord.contains(letter, true)
        //Añadimos el valor en el mapa
        enteredLetters[letter.uppercaseChar()] = checkLetter
        //Devolvemos 0 si era erronea o el valor entero del Char si era correcta
        message= letter + if (checkLetter)": Well done! " else ": Oops, nope. "
    }
    /**Actualiza los datos del juego segun el numero de errores y aciertos (en ese orden)
     * @param forceReset: Activar para forzar a reiniciar la partida
     */
    fun updateGameData(forceReset: Boolean) {
        when {
            forceReset -> resetGameData()
            hasLost() -> imageManager.updateFrame(ImageIndex.GAME_OVER)

            hasWon()-> imageManager.updateFrame(ImageIndex.YOU_WIN)
            else -> imageManager.updateFrame(ImageIndex.entries[getLettersNok().count()])
        }
    }

    /** Workaround to solve updating screen problems when managing images outside context */
    fun reloadImage() {
        imageManager.reloadImage()
    }

    fun viewWordHint(): String {
        val oldMessage = message
        message= wordMap!!.getOrDefault(magicWord.lowercase(),"ERRR")
        return oldMessage
    }

    fun viewDevMessage(): String {
        val oldmessage= message
        solveMagicWord()
        return oldmessage
    }

    /**---------------------------------------------------------------------------------------------
     *                                                                        PRIVATE FUNCTIONS
     * -------------------------------------------------------------------------------------------*/
    /** Metodo que nos devuelve la lista de letras de la palabra secreta (sin repeticiones) */
    private fun magicWordDistinctLetters(): List<Char> = magicWord.uppercase().toCharArray().distinct()

   /** Metodo que selecciona aleatoriamente una palabra de la lista y la setea en la variable "magicWord" */
    private fun selectWord(context: Context?) {
        //If words data is not loaded
        if (wordMap.isNullOrEmpty()) {
            //Obtain all XML defined strings (IT INCLUDES ALL project strings)
            val fields: Array<Field> = R.string::class.java.fields
            val stringsMap = mutableMapOf<String, String>()

            for (field in fields) {
                val resId = field.getInt(null)
                val resName = field.name
                //filter only values starting by "_"
                if(resName.toCharArray().first()==('_')) {
                    val resValue = context!!.getString(resId)
                    stringsMap[resName.trim('_')] = resValue
                }
            }
            this.wordMap = stringsMap
        }
        this.magicWord = wordMap!!.keys.toList()[Random.nextInt(
            from = 0, until = wordMap!!.size
        )].uppercase(Locale.getDefault())
    }

    fun solveMagicWord() {
        message = magicWord
    }

    /**RESET
     * borra todas las letras y restablece el mapa.
     * También vuelve a setear una nueva palabra aleatoria
     */
    private fun resetGameData() {
        enteredLetters.clear()
        message= "- Enter a letter -"
        selectWord(null)
        imageManager.updateFrame(ImageIndex.TITLE)
    }

    /** This allows to recover same instance of GameManage through different activities */
    companion object {
        lateinit var instance: GameManager
    }
}

