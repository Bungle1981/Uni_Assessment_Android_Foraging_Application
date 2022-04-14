package com.example.foragingapplicationv2.customdialogs

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.foragingapplicationv2.R
import com.example.foragingapplicationv2.utils.Constants
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import java.lang.ClassCastException


class MyCustomDialog: DialogFragment() {
    private var latitude: String? = null
    private var longitude: String? = null
    private var forageLatitude: TextInputEditText? = null
    private var forageLongitude: TextInputEditText? = null
    private var forageType: TextInputEditText? = null
    private var forageNotes: TextInputEditText? = null
    private var forageSwitch: Switch? = null
    private var btnCreate: Button? = null
    private var errorText: TextView? = null

    private var listener: DialogListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner);

        val pageView = inflater.inflate(R.layout.dialog_new_spot, container, false)

        forageLatitude = pageView?.findViewById(R.id.textInputForageLatitude)
        forageLongitude = pageView?.findViewById(R.id.textInputForageLongitude)
        forageType = pageView?.findViewById(R.id.textInputForageType)
        forageNotes = pageView?.findViewById(R.id.textInputForageNotes)
        forageSwitch = pageView?.findViewById(R.id.switchShareSpot)
        errorText = pageView?.findViewById(R.id.error_text)

        val data: Bundle? = getArguments()
        forageLatitude?.setText(data?.getString("lat"))
        forageLongitude?.setText(data?.getString("lon"))
        btnCreate = pageView?.findViewById(R.id.btnCreateForagingSpot)

        btnCreate?.setOnClickListener {
            createNewForageSpot()
        }
        return pageView
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun createNewForageSpot() {
        val type = Constants.capitalizeString(forageType?.text.toString().trim{ it <= ' '})
        val notes = forageNotes?.text.toString().replaceFirstChar{ it.uppercase() }.trim{ it <= ' '}
        val latitude = forageLatitude?.text.toString()
        val longitude = forageLongitude?.text.toString()

        if (validateNewForageForm(type, latitude, longitude)) {
            // Details all present - pass details back to map activity to create the RIGHT type of stored object

            if (forageSwitch?.isChecked == true) {
                // Need to create and save forage spot to the AMAZON FIRESTORE database
                listener?.applyTexts(true, type, notes, LatLng(latitude.toDouble(), longitude.toDouble()), true)
            } else {
                // Need to create and store on user device - SharedPreferences or Room database?
                listener?.applyTexts(true, type, notes, LatLng(latitude.toDouble(), longitude.toDouble()), false)
            }
            dialog?.dismiss()
        } else {
            errorText?.setVisibility(View.VISIBLE)
            forageType?.setBackgroundColor(Color.parseColor(("#D00000")))
            forageType?.setAlpha(0.4F)

        }
    }

    private fun validateNewForageForm(forageType: String, forageLat: String, forageLon: String): Boolean {
        return !(TextUtils.isEmpty(forageType) || TextUtils.isEmpty(forageLat) || TextUtils.isEmpty(forageLon))
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString())
        }
    }

    interface DialogListener {
        fun applyTexts(success: Boolean, forageName: String?, foragenote: String?, position: LatLng, shared: Boolean)
    }

}