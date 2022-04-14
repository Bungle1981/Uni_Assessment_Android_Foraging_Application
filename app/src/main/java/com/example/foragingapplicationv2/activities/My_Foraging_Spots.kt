package com.example.foragingapplicationv2.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foragingapplicationv2.adapters.RecyclerItemAdapter
import com.example.foragingapplicationv2.customdialogs.EditSpotDialog
import com.example.foragingapplicationv2.databinding.ActivityMyForagingSpotsBinding
import com.example.foragingapplicationv2.firebase.FirestoreClass
import com.example.foragingapplicationv2.roomdatabase.ForageSpotDao
import com.example.foragingapplicationv2.roomdatabase.ForageSpotDatabase
import com.example.foragingapplicationv2.roomdatabase.ForageSpotEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class My_Foraging_Spots : BaseActivity(), EditSpotDialog.DialogListener2 {
    private var binding : ActivityMyForagingSpotsBinding? = null
    // Room DB Setup TODO - This is accessed on 2 activities - can I refactor to make the code easier
    private lateinit var db: ForageSpotDatabase
    private lateinit var forageSpotDao: ForageSpotDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyForagingSpotsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        db = ForageSpotDatabase.getInstance(this)
        forageSpotDao = db.forageSpotDao()
        getUsersSpots()
    }

    fun getUsersSpots() {
        showProgressDialog("Loading your private foraging spots")
        lifecycleScope.launch(Dispatchers.IO) {
            val localResults: List<ForageSpotEntity> = forageSpotDao.fetchSpotsByUser(id = FirestoreClass().getCurrentUserID())
            createRecyclerView(localResults)
        }
    }

    fun createRecyclerView(forageList: List<ForageSpotEntity>) {
        hideProgressDialog()
        if(forageList.isEmpty()) {
            showSnackBar("You don't currently have any private foraging spots saved.", "Error")
        } else {
            val itemAdapter = RecyclerItemAdapter(forageList,
            {
                deleteId ->
                deleteRecordAlertDialog(deleteId)
            }, {
                mapId->
                    getSpotDetails(mapId)
                }, {
                    updateId->
                    updateRecordDialog(updateId)
                }
            )
            binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
            binding?.recyclerView?.adapter = itemAdapter
        }
    }

    fun getSpotDetails(id: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val localResult: ForageSpotEntity = forageSpotDao.fetchSpotBySpotID(id=id)
            goToMap(localResult)
        }
    }

    fun goToMap(result: ForageSpotEntity) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("shared", result.shared)
        intent.putExtra("type", result.forageType)
        intent.putExtra("notes", result.notes)
        intent.putExtra("latitude", result.latitude)
        intent.putExtra("longitude", result.longitude)
        startActivity(intent)
    }

    fun deleteRecordAlertDialog(deleteID: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Forage Spot?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") {dialogInterface, _ ->
            lifecycleScope.launch{
                forageSpotDao.deleteForageSpot(ForageSpotEntity(deleteID))
            }.invokeOnCompletion {
                dialogInterface.dismiss()
                startActivity(getIntent())
                finish()
            }
        }
        builder.setNegativeButton("No") {dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }




    fun updateRecordDialog(updateID: Int) {
        var spotToUpdate: ForageSpotEntity? = null
        lifecycleScope.launch(Dispatchers.IO) {
            spotToUpdate = forageSpotDao.fetchSpotBySpotID(updateID)
        }.invokeOnCompletion {
            if (spotToUpdate != null) {
                var args: Bundle = Bundle()
                args.putString("lat", spotToUpdate?.latitude.toString())
                args.putString("lon", spotToUpdate?.longitude.toString())

                args.putString("type", spotToUpdate?.forageType.toString())
                args.putString("notes", spotToUpdate?.notes.toString())
                args.putString("description", spotToUpdate?.addressDescription.toString())
                args.putString("spotID", spotToUpdate?.spotID.toString())

                val customDialogBox = EditSpotDialog()
                customDialogBox.setArguments(args)
                customDialogBox.show(supportFragmentManager, "MyCustomFragment")
            }

        }

    }

    override fun applyText(success: Boolean, forageName: String, foragenote: String?, position: LatLng, shared: Boolean, description: String, spotID: Int) {
        if (success) {
            var forageNote = ""
            if (foragenote != null) {
                forageNote = foragenote
            }
            val updatedSpot = ForageSpotEntity(
                spotID = spotID,
                submittedUserID = FirestoreClass().getCurrentUserID(),
                forageType = forageName,
                notes = forageNote,
                latitude = position.latitude,
                longitude = position.longitude,
                shared = shared,
                addressDescription = description
            )
            lifecycleScope.launch {
                forageSpotDao.updateForageSpot(updatedSpot)
            }.invokeOnCompletion {
                startActivity(getIntent())
                finish()
            }
        }
    }


}