package dairungarbayev.app.minimalisttracker

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val MODE_ZERO : String = "modeZero"
    private val MODE_GOOD : String = "modeGood"

    private val dateKey = "date"
    private val pointsKey = "points"
    private val currentModeKey : String = "currentMode"
    private val startKey = "start"

    private var date : Long = 0
    private var points : Float = 0.0f
    private var currentMode : String = " "
    private var start : Long = 0

    private lateinit var resetButton : TextView
    private lateinit var dateAndPoints : TextView
    private lateinit var mainButton : View
    private lateinit var modeView : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getViews()
    }

    //data is retrieved and views are set
    override fun onResume() {
        super.onResume()
        getDataFromPrefs()
        checkData()
        setViews()

        mainButton.setOnClickListener(mainButtonListener)
        resetButton.setOnClickListener(resetListener)
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    private fun getViews(){
        resetButton = findViewById(R.id.reset)
        dateAndPoints = findViewById(R.id.date_and_points)
        mainButton = findViewById(R.id.main_button)
        modeView = findViewById(R.id.current_mode_view)
    }

    //not tested yet | void method testing?
    //can be mocked from context using mockito
    private fun getDataFromPrefs(){
        val prefs = this.getPreferences(Context.MODE_PRIVATE)
        date = prefs.getLong(dateKey,Calendar.getInstance().timeInMillis)
        points = prefs.getFloat(pointsKey,0.0f)
        currentMode = prefs.getString(currentModeKey,MODE_ZERO)!!
        start = prefs.getLong(startKey,0)
    }

    //doesn't handle time zone changes
    private fun checkData(){
        val currentTime = Calendar.getInstance()
        val storedTime = Calendar.getInstance()
        storedTime.timeInMillis = date
        if (currentTime.get(Calendar.DAY_OF_YEAR) != storedTime.get(Calendar.DAY_OF_YEAR)){
            resetButton.visibility = View.VISIBLE
            Toast.makeText(this,"New Day!",Toast.LENGTH_SHORT).show()
        } else resetButton.visibility = View.GONE
    }

    private fun setViews(){
        val cal = Calendar.getInstance()
        cal.timeInMillis = date

        val formatter = SimpleDateFormat("yyyy.MM.dd")
        val string = formatter.format(cal.time) + ":  " + points.toString()
        dateAndPoints.text = string

        when (currentMode){
            MODE_ZERO -> modeView.setBackgroundColor(Color.WHITE)
            MODE_GOOD -> modeView.setBackgroundColor(Color.BLUE)
        }
    }

    private fun saveData(){
        //do not mutate the date
        //date = Calendar.getInstance().timeInMillis
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putLong(dateKey,date)
            putFloat(pointsKey,points)
            putString(currentModeKey,currentMode)
            putLong(startKey,start)
            commit()
        }
    }

    private val mainButtonListener = View.OnClickListener {
        //write in journal!!!---------------------------------------------
        val currentTimeInMillis = Calendar.getInstance().timeInMillis
        when (currentMode){
            MODE_GOOD -> {
                val timeIntervalInMins = (currentTimeInMillis - start)/60000.00f
                points += timeIntervalInMins * 1.00f //weight of MODE GOOD
                currentMode = MODE_ZERO
                start = 0
            }
            MODE_ZERO -> {
                start = currentTimeInMillis
                currentMode = MODE_GOOD
            }
        }
        setViews()
    }

    private val resetListener = View.OnClickListener {
        points = 0.0f
        date = Calendar.getInstance().timeInMillis
        currentMode = MODE_ZERO
        start = 0
        resetButton.visibility = View.GONE
        //modeView.setBackgroundColor(Color.WHITE)
        setViews()
    }
}