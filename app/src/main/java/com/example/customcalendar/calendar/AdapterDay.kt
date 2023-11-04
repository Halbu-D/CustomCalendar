package com.example.customcalendar.calendar

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.customcalendar.R
import com.example.customcalendar.databinding.DayAdapterBinding
import com.example.customcalendar.individual.CalendarModel
import com.example.customcalendar.individual.IndividualActivity
import com.example.customcalendar.individual.PlanAdapter
import com.example.customcalendar.utils.FBAuth
import com.example.customcalendar.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class AdapterDay(val tempMonth:Int, val dayList: MutableList<Date>, val height:Int): RecyclerView.Adapter<AdapterDay.DayView>() {
    val ROW = 6
    var selectedDate = LocalDate.now()
    val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

    inner class DayView(val binding: DayAdapterBinding): RecyclerView.ViewHolder(binding.root)
    private val uid = FBAuth.getUid()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val binding = DayAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false) // ViewBinding 초기화
        return DayView(binding)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        holder.binding.itemDayLayout.setOnClickListener {
            val intent = Intent(holder.binding.root.context, IndividualActivity::class.java)
            intent.putExtra("day","${formatter.format(dayList[position])}")
            holder.binding.root.context.startActivity(intent)

            //Toast.makeText(holder.binding.root.context, "${height}", Toast.LENGTH_SHORT).show()
            //Toast.makeText(holder.binding.root.context, "${dayList[position]}", Toast.LENGTH_SHORT).show()
        }

        holder.binding.itemDayText.text = dayList[position].date.toString()
        holder.binding.itemDayLayout.layoutParams.height = height/8 //높이 조절

        val planData = mutableListOf<CalendarModel>()
        val planAdapter = PlanAdapter(planData)
        holder.binding.planList.adapter = planAdapter

        planAdapter.notifyDataSetChanged()

        holder.binding.itemDayText.setTextColor(when(position % 7) {
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        })

        if(tempMonth != dayList[position].month) {
            holder.binding.itemDayText.alpha = 0.4f
        }

        if(Calendar.getInstance().get(Calendar.MONTH) == dayList[position].month && Calendar.getInstance().get(Calendar.DATE) == dayList[position].date) { // 오늘 날짜 강조
            holder.binding.itemDayLayout.setBackgroundResource(R.drawable.round_border)
        } // 기존 날짜 강조

        getPlanData(holder, position, planData, planAdapter)
    }

    private fun getPlanData(holder: DayView, position: Int, planData: MutableList<CalendarModel>, planAdapter: PlanAdapter) { //파이어베이스에서 일정 가져오기

        val planListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일") // 비교할 문자열 형태
                val currentDate = dateFormat.format(dayList[position])

                planData.clear()

                for(dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(CalendarModel::class.java)

                    if(currentDate == item?.startDate && uid == item?.uid )  // 시작일 일치, 중복 아닌경우
                        planData.add(item!!)
                }

                planAdapter.notifyDataSetChanged()
            }



            override fun onCancelled(databaseError: DatabaseError) {
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.calendarRef.addValueEventListener(planListener)
    }
/*

    private fun getPlanData(holder: DayView, position: Int, planData: MutableList<CalendarModel>) { //파이어베이스에서 일정 가져오기

        val planListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dateformat = SimpleDateFormat("yyyy년 MM월 dd일") // 비교할 문자열 형태
                val currentdate = dateformat.format(dayList[position])

                for(dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(CalendarModel::class.java)

                    //Log.d(TAG, item?.startDate.toString())

                    if(currentdate == item?.startDate && uid == item?.uid)  // 시작일 일치시
                        //holder.binding.planList.
                        holder.binding.plan.setText(item?.plan)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.calendarRef.addValueEventListener(planListener)

    }
*/

    override fun getItemCount(): Int {
        return ROW * 7
    }
}