package com.chanpal.target.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.chanpal.target.R
import com.chanpal.target.data.Coins
import com.chanpal.target.data.Target
import com.chanpal.target.utils.CoinsDataBase
import com.chanpal.target.utils.TargetDataBase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    var gain = 0
    var loss = 0
    private var chart: BarChart? = null
    private val targetDao = TargetDataBase.instance.getTargetDao()
    private val coinsDao = CoinsDataBase.instance.getCoinsDao()


    val calendar  = Calendar.getInstance()
    val todayYear = calendar.get(Calendar.YEAR)
    val todayMonth = calendar.get(Calendar.MONTH) + 1
    val today = calendar.get(Calendar.DAY_OF_MONTH)
//    val today = 7  //89656

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chart = findViewById(R.id.chart)
        chart!!.description.isEnabled = false
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart!!.setMaxVisibleValueCount(60)
        // scaling can now only be done on x- and y-axis separately
        chart!!.setPinchZoom(false)
        chart!!.setDrawGridBackground(false)

        chart!!.setDrawBarShadow(false)
        chart!!.setDrawValueAboveBar(true)
        chart!!.description?.isEnabled = false
        chart!!.setTouchEnabled(false)
        chart!!.isDragEnabled = false
        chart!!.setScaleEnabled(false)

        val xAxis = chart!!.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        var tfRegular = Typeface.createFromAsset(assets, "OpenSans-Regular.ttf")
        test.text = todayYear.toString() + "年"+todayMonth+"月"+today+"日"
        xAxis.typeface = tfRegular
        xAxis.setDrawGridLines(false)

        chart!!.axisLeft.setDrawGridLines(false)
        // add a nice and smooth animation

        // add a nice and smooth animation
        chart!!.animateY(1500)
        chart!!.legend.isEnabled = false

//        val bar = findViewById<SeekBar>(R.id.seekBar1)
        waterWaveProgress.setShowProgress(true)

        waterWaveProgress.animateWave()
//        waveProgress.setWaterBgColor(R.color.sea_blue)
        waterWaveProgress.setFontSize(52)

        // THIS IS THE ORIGINAL DATA YOU WANT TO PLOT
        initDao()
    }
    private fun initDao() {
        var sList: MutableList<Coins> = mutableListOf<Coins>()
        if (coinsDao.getCoins(todayYear * 10000 + todayMonth * 100 + 1) == null) {
            for (index in 1 .. 31) {
                var defaultDate = Coins(todayYear * 10000 + todayMonth * 100 + index,index.toFloat(),0f)
                sList.add(defaultDate)
            }
            coinsDao.insert(sList)
            var lastMonthProgress = targetDao.getTarget(todayYear * 100 + todayMonth - 1)
            if (lastMonthProgress == null) {
                val targetData = Target(todayYear * 100 + todayMonth, 0f, 0f)
                targetDao.insert(targetData)
            } else {
                val targetData = Target(todayYear * 100 + todayMonth, 0f, lastMonthProgress.TargetProgress)
                targetDao.insert(targetData)
            }
        }
        val data: MutableList<Data> = ArrayList()
        for (index  in 0 .. 30) {
            data.add(Data(
                coinsDao.getCoins(todayYear * 10000 + todayMonth * 100 + index + 1).CoinsDate!!,
                coinsDao.getCoins(todayYear * 10000 + todayMonth * 100 + index + 1).CoinsNumber!!
            ))
        }

        today_score.text = coinsDao.getCoins(todayYear * 10000 + todayMonth * 100 + today).CoinsNumber.toString()
        Log.d(TAG , "today_score   ： ${coinsDao.getCoins(todayYear * 10000 + todayMonth * 100 + today).CoinsNumber}")
        var monthText = targetDao.getTarget(todayYear * 100 + todayMonth).MonthNumber
        Log.d(TAG , "month_score   ： ${targetDao.getTarget(todayYear * 100 + todayMonth).MonthNumber}")
        if ( targetDao.getTarget(todayYear * 100 + todayMonth) == null) {
            month_score.text = "0"
            waterWaveProgress.setTarget(0)
            waterWaveProgress.setProgress(0)
        } else {
            month_score.text = monthText.toString()
            var lastMonthPro = targetDao.getTarget(todayYear * 100 + todayMonth - 1)
            if (lastMonthPro == null) {
                waterWaveProgress.setTarget(monthText.toInt())
                waterWaveProgress.setProgress(monthText.toInt() / 10000)
            } else {
                waterWaveProgress.setTarget((lastMonthPro.TargetProgress!! + monthText).toInt())
                waterWaveProgress.setProgress((lastMonthPro.TargetProgress!! + monthText).toInt() / 10000)
            }
        }
        initData(data)
    }

    /**
     * Demo class representing data.
     */
    private class Data constructor(
        val xValue: Float,
        var yValue: Float
    )

    private fun initData(data: MutableList<Data>) {
//        val colors: MutableList<Int> = ArrayList()
//        val green = Color.rgb(110, 190, 102)
//        val red = Color.rgb(211, 74, 88)
        val values = ArrayList<BarEntry>()
        val set1: BarDataSet

        for (i in 0 until data.size) {
            // specific colors
//            if (data[i].yValue >= 0) colors.add(red) else colors.add(green)
            val entry = BarEntry(data[i].xValue, data[i].yValue)
            values.add(entry)
        }
        if (chart!!.data != null &&
            chart!!.data.dataSetCount > 0
        ) {
            set1 = chart!!.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart!!.data.notifyDataChanged()
            chart!!.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "Data Set")
//            set1.colors = colors
            set1.setDrawValues(false)
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            chart!!.data = data
            chart!!.setFitBars(true)
        }

        chart!!.invalidate()
    }

    fun datePick(view: View) {
        var builder: AlertDialog.Builder  = AlertDialog.Builder(this)
        var view: View  = layoutInflater.inflate(R.layout.date_dialog, null)
        val datePicker: DatePicker = view.findViewById(R.id.date_picker)
        val define : TextView = view.findViewById(R.id.button_define)
        val cancel : TextView = view.findViewById(R.id.button_cancel)
        //设置日期简略显示 否则详细显示 包括:星期\周
//        datePicker.calendarViewShown = false
        //设置date布局
        builder.setView(view)
        var dialog  = builder.create()
        define.setOnClickListener {
            val year = datePicker.year
            val month = datePicker.month +1
            val dayOfMonth = datePicker.dayOfMonth
            val getDate = year.toString() + "年"+month+"月"+dayOfMonth+"日"
            disPlayDate(getDate,year,month,dayOfMonth)
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun disPlayDate(date: String, year: Int, month: Int, dayOfMonth: Int) {
        var searchDate = year * 10000 + month * 100 + dayOfMonth
        var todayDate = todayYear * 10000 + todayMonth * 100 + today
        if (searchDate < 20210801 || searchDate > todayDate) {
            Toast.makeText(this , "超出可查询范围，请重新选择查询日期",Toast.LENGTH_SHORT).show()
            return
        }
        test.text = date
        today_score.text = coinsDao.getCoins(year * 10000 + month * 100 + dayOfMonth).CoinsNumber.toString()
        month_score.text = targetDao.getTarget(year * 100 + month).MonthNumber.toString()
        val dataSearch: MutableList<Data> = ArrayList()
        for (index  in 0 .. 30) {
            dataSearch.add(Data(
                coinsDao.getCoins(year * 10000 + month * 100 + index + 1).CoinsDate!!,
                coinsDao.getCoins(year * 10000 + month * 100 + index + 1).CoinsNumber!!
            ))
        }
        initData(dataSearch)
    }

    fun inputData(view: View) {
        dialogInput()
    }

    private fun dialogInput() {
        var builder: AlertDialog.Builder  = AlertDialog.Builder(this)
        var view: View  = layoutInflater.inflate(R.layout.input_dialog, null)
        val gainEdit : EditText = view.findViewById(R.id.gain_input)
        val lossEdit : EditText = view.findViewById(R.id.loss_input)
        val define : TextView = view.findViewById(R.id.input_define)
        val cancel : TextView = view.findViewById(R.id.input_cancel)
        // 设置 dialog 布局
        builder.setView(view)
        var dialog  = builder.create()
        define.setOnClickListener {
            if (gainEdit.text.isNotEmpty() && lossEdit.text.isEmpty()) {
                gain = gainEdit.text.toString().toInt()
                loss = 0
            } else if (gainEdit.text.isEmpty() && lossEdit.text.isNotEmpty()) {
                gain = 0
                loss = lossEdit.text.toString().toInt()
            } else if (gainEdit.text.isNotEmpty() && lossEdit.text.isNotEmpty()) {
                gain = gainEdit.text.toString().toInt()
                loss = lossEdit.text.toString().toInt()
            } else if (gainEdit.text.isEmpty() && lossEdit.text.isEmpty()) {
                gain = 0
                loss = 0
            }
            var score: Int = gain - loss
            saveDao(score)
            dialog.dismiss()

        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun saveDao(score: Int) {
        Log.d(TAG , "年月日   ： $todayYear  $todayMonth   $today    $score")
        var coinsData = Coins(todayYear * 10000 + todayMonth * 100 + today, today.toFloat(), score.toFloat())
        Log.d(TAG , "coinsData   ： $today    $score")
        coinsDao.update(coinsData)
        var monthData = 0f
        for (day in 0 .. 30) {
            monthData += coinsDao.getAllByDateDesc()[day].CoinsNumber!!
        }
        var progressData = 0f
        for (index in 0 until targetDao.getAllTarget().size) {
            Log.d(TAG , "progressData   : ${targetDao.getAllTarget()[index].MonthNumber!!}")
            Log.d(TAG , "size   : ${targetDao.getAllTarget().size}")
            progressData += targetDao.getAllTarget()[index].MonthNumber!!
        }
        var lastmonthpro = targetDao.getTarget(todayYear * 100 + todayMonth - 1)
        var targetProgress = 0f
        if (lastmonthpro != null) {
            targetProgress = lastmonthpro.TargetProgress
        }
        val targetData = Target(todayYear * 100 + todayMonth, monthData, targetProgress + progressData)
        if (targetDao.getTarget(todayYear * 100 + todayMonth) == null) {
            Log.d(TAG , "inser default data  ${targetDao.getTarget(todayYear * 100 + todayMonth)}")
            targetDao.insert(targetData)
        } else {
            Log.d(TAG , "update default data  ${targetDao.getTarget(todayYear * 100 + todayMonth)}")
            targetDao.update(targetData)
        }
        initDao()
    }

}