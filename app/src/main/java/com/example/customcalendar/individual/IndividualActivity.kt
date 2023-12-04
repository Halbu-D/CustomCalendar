package com.example.customcalendar.individual

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.customcalendar.R
import com.example.customcalendar.databinding.ActivityIndividualBinding
import com.example.customcalendar.utils.FBAuth
import com.example.customcalendar.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class IndividualActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIndividualBinding
    private var selectedbtn: String = "NaN"
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH시mm분", Locale.getDefault())

    private val TAG = IndividualActivity::class.java.simpleName

    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email.toString()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_individual)
        val startDate = findViewById<TextView>(R.id.startDate)
        val endDate =  findViewById<TextView>(R.id.endDate)

        // 선택한 날짜를 받아와서 화면에 뿌려줌
        startDate.text = intent.getStringExtra("day").toString()
        endDate.text = intent.getStringExtra("day").toString()

        var startText = startDate.text.toString()
        var endText = endDate.text.toString()
        var startTime = "09시00분"
        var endTime = "09시10분"

        val radioButtonH: RadioButton = findViewById(R.id.high)
        val radioButtonN: RadioButton = findViewById(R.id.normal)
        val radioButtonL: RadioButton = findViewById(R.id.low)

        val colorStateList = ColorStateList.valueOf(Color.BLACK) // 원하는 색상으로 변경하세요.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radioButtonH.buttonTintList = colorStateList
            radioButtonN.buttonTintList = colorStateList
            radioButtonL.buttonTintList = colorStateList
        }


        binding.startDate.setOnClickListener { // 시작 날짜 설정
            val startView = LayoutInflater.from(this).inflate(R.layout.select_date, null)
            val sBuilder = AlertDialog.Builder(this).setView(startView).setTitle("시작일")
            val startAlertDialog = sBuilder.show()
            val dPicker = startView.findViewById<DatePicker>(R.id.dateSpinner)
            val acceptButton = startView.findViewById<Button>(R.id.acceptButton)
            val closeButton = startView.findViewById<Button>(R.id.closeButton)

            acceptButton.setOnClickListener {// 확인 버튼 처리
                val selectedYear = dPicker.year.toString()
                val selectedMonth = (dPicker.month + 1).toString() // 0부터 시작하므로 1을 더해야 실제 월을 얻을 수 있음.
                val selectedDay = when(dPicker.dayOfMonth){
                    in 1..9 -> "0" + dPicker.dayOfMonth.toString()
                    else -> dPicker.dayOfMonth.toString()
                }


                startText = selectedYear + "-" + selectedMonth +"-"+ selectedDay
                startDate.text = startText

                if(formatter.parse(startText.toString()).time > formatter.parse(endText.toString()).time){
                    endText = startText
                    endDate.text = startText // 시작일이 종료일보다 크면 안 됨
                }

                startAlertDialog.dismiss()
            }
            closeButton.setOnClickListener {// 취소버튼 처리
                startAlertDialog.dismiss()
            }
        }

        binding.endDate.setOnClickListener { // 종료 날짜 설정
            val endView = LayoutInflater.from(this).inflate(R.layout.select_date, null)
            val eBuilder = AlertDialog.Builder(this).setView(endView).setTitle("종료일")
            val dPicker = endView.findViewById<DatePicker>(R.id.dateSpinner)
            val endAlertDialog = eBuilder.show()

            val acceptButton = endView.findViewById<Button>(R.id.acceptButton)
            val closeButton = endView.findViewById<Button>(R.id.closeButton)

            acceptButton.setOnClickListener {// 확인 버튼 처리
                val selectedYear = dPicker.year.toString()
                val selectedMonth = (dPicker.month + 1).toString() // 0부터 시작하므로 1을 더해야 실제 월을 얻을 수 있음.
                val selectedDay = when(dPicker.dayOfMonth){
                    in 1..9 -> "0" + dPicker.dayOfMonth.toString()
                    else -> dPicker.dayOfMonth.toString()
                }
                endText = selectedYear + "-" + selectedMonth +"-"+ selectedDay
                endDate.text = endText

                if(formatter.parse(startText.toString()).time > formatter.parse(endText.toString()).time){
                    startText = endText
                    startDate.text = endText // 종료일이 시작일보다 작은 경우
                }

                endAlertDialog.dismiss()

            }
            closeButton.setOnClickListener {// 취소버튼 처리
                endAlertDialog.dismiss()
            }
        }

        binding.startTime.setOnClickListener { // 종료 날짜 설정
            val View = LayoutInflater.from(this).inflate(R.layout.select_time, null)
            val Builder = AlertDialog.Builder(this).setView(View).setTitle("시작 시간")
            val tPicker = View.findViewById<TimePicker>(R.id.timeSpinner)
            val endAlertDialog = Builder.show()

            val acceptButton = View.findViewById<Button>(R.id.acceptButton)
            val closeButton = View.findViewById<Button>(R.id.closeButton)

            acceptButton.setOnClickListener {// 확인 버튼 처리
                val selectedhour = when(tPicker.hour){
                    in 0..9 -> "0" + tPicker.hour.toString()
                    else ->tPicker.hour.toString()
                }
                val selectedmin = when(tPicker.minute){
                    in 0..9 -> "0" + tPicker.minute.toString()
                    else -> tPicker.minute.toString()
                }

                startTime = selectedhour + "시" + selectedmin + "분"
                findViewById<TextView>(R.id.startTime).text = startTime

                if(formatter.parse(startText.toString()).time == formatter.parse(endText.toString()).time){ //시작일 종료일 같을때만
                    if(timeFormat.parse(startTime.toString()).time > timeFormat.parse(endTime.toString()).time){
                        endTime = startTime
                        findViewById<TextView>(R.id.endTime).text = startTime
                    }
                }

                endAlertDialog.dismiss()
            }
            closeButton.setOnClickListener {// 취소버튼 처리
                endAlertDialog.dismiss()
            }
        }

        binding.endTime.setOnClickListener { // 종료 날짜 설정
            val View = LayoutInflater.from(this).inflate(R.layout.select_time, null)
            val Builder = AlertDialog.Builder(this).setView(View).setTitle("종료 시간")
            val tPicker = View.findViewById<TimePicker>(R.id.timeSpinner)
            val endAlertDialog = Builder.show()

            val acceptButton = View.findViewById<Button>(R.id.acceptButton)
            val closeButton = View.findViewById<Button>(R.id.closeButton)

            acceptButton.setOnClickListener {// 확인 버튼 처리
                val selectedhour = when(tPicker.hour){
                    in 0..9 -> "0" + tPicker.hour.toString()
                    else ->tPicker.hour.toString()
                }
                val selectedmin = when(tPicker.minute){
                    in 0..9 -> "0" + tPicker.minute.toString()
                    else -> tPicker.minute.toString()
                }

                endTime = selectedhour + "시" + selectedmin + "분"

                findViewById<TextView>(R.id.endTime).text = endTime

                if(formatter.parse(startText.toString()).time == formatter.parse(endText.toString()).time){ //시작일 종료일 같을때만
                    if(timeFormat.parse(startTime.toString()).time > timeFormat.parse(endTime.toString()).time){
                        startTime = endTime
                        findViewById<TextView>(R.id.startTime).text = endTime
                    }
                }

                endAlertDialog.dismiss()
            }
            closeButton.setOnClickListener {// 취소버튼 처리
                endAlertDialog.dismiss()
            }
        }


        binding.writeBtn.setOnClickListener {
            //val date = intent.getStringExtra("day").toString()
            val startdate = binding.startDate.text.toString()
            val enddate = binding.endDate.text.toString()
            val starttime = binding.startTime.text.toString()
            val endtime = binding.endTime.text.toString()
            val plan = binding.plan.text.toString()
            val location = binding.location.text.toString()
            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email.toString()
            val inputTime = FBAuth.getTime()

            //FBRef.calendarRef.setValue(CalendarModel(date, plan, location))
            if(plan == "")
                Toast.makeText(binding.root.context, "일정이 없습니다.", Toast.LENGTH_SHORT).show()
            else if(email == "null")
                Toast.makeText(binding.root.context, "비로그인 상태.", Toast.LENGTH_SHORT).show()
            else
                FBRef
                    .calendarRef
                    .push()
                    .setValue(
                        CalendarModel(
                            startdate, enddate, starttime, endtime, plan, location, email, inputTime, selectedbtn
                        )
                    )

            Log.d(TAG, plan)

            finish()
        }

        binding.rdgroup.setOnCheckedChangeListener{ group, checkedID->
            selectedbtn = when(checkedID){
                R.id.high ->  "1"
                R.id.normal -> "2"
                R.id.low -> "3"
                else -> "NaN"
            }
        }
    }
}