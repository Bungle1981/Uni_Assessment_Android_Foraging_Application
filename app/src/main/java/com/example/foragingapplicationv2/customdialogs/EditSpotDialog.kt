package com.example.foragingapplicationv2.customdialogs

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.foragingapplicationv2.R
import com.example.foragingapplicationv2.utils.Constants
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import java.lang.ClassCastException

class EditSpotDialog: DialogFragment() {
    private var forageLatitude: TextInputEditText? = null
    private var forageLongitude: TextInputEditText? = null
    private var forageType: TextInputEditText? = null
    private var forageNotes: TextInputEditText? = null
    private var forageLocationDescription: TextInputEditText? = null
    private var forageSpotID: TextInputEditText? = null
    private var btnCreate: Button? = null
    private var errorText: TextView? = null

    private var listener: DialogListener2? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner);

        val pageView = inflater.inflate(R.layout.dialog_edit_spot, container, false)

        forageLatitude = pageView?.findViewById(R.id.textInputForageLatitude)
        forageLongitude = pageView?.findViewById(R.id.textInputForageLongitude)
        forageLocationDescription = pageView?.findViewById(R.id.textInputForageLocationDescription)
        forageType = pageView?.findViewById(R.id.textInputForageType)
        forageNotes = pageView?.findViewById(R.id.textInputForageNotes)
        forageSpotID = pageView?.findViewById(R.id.textInputForageSpotID)

        errorText = pageView?.findViewById(R.id.error_text)

        val data: Bundle? = getArguments()

        forageType?.setText(data?.getString("type"))
        forageNotes?.setText(data?.getString("notes"))
        forageLatitude?.setText(data?.getString("lat"))
        forageLongitude?.setText(data?.getString("lon"))
        forageLocationDescription?.setText(data?.getString("description"))
        forageSpotID?.setText(data?.getString("spotID"))

        btnCreate = pageView?.findViewById(R.id.btnCreateForagingSpot)

        btnCreate?.setOnClickListener {
            editForageSpot()
        }
        return pageView
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun editForageSpot() {
        val type = Constants.capitalizeString(forageType?.text.toString().trim{ it <= ' '})
        val notes = forageNotes?.text.toString().replaceFirstChar{ it.uppercase() }.trim{ it <= ' '}
        val latitude = forageLatitude?.text.toString()
        val longitude = forageLongitude?.text.toString()
        val locationDescription = forageLocationDescription?.text.toString()
        val spotID = forageSpotID?.text.toString().toInt()

        if (validateNewForageForm(type, latitude, longitude)) {
            listener?.applyText(true, type, notes, LatLng(latitude.toDouble(), longitude.toDouble()), false, locationDescription, spotID)
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
            listener = context as DialogListener2
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString())
        }
    }

    interface DialogListener2 {
        fun applyText(success: Boolean, forageName: String, foragenote: String?, position: LatLng, shared: Boolean, description: String, spotID: Int)
    }



}