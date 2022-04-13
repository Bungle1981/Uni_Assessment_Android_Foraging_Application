package com.example.foragingapplicationv2.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foragingapplicationv2.R
import com.example.foragingapplicationv2.adapters.RecyclerItemAdapter
import com.example.foragingapplicationv2.databinding.ActivityMyForagingSpotsBinding
import com.example.foragingapplicationv2.firebase.FirestoreClass
import com.example.foragingapplicationv2.roomdatabase.ForageSpotDao
import com.example.foragingapplicationv2.roomdatabase.ForageSpotDatabase
import com.example.foragingapplicationv2.roomdatabase.ForageSpotEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class My_Foraging_Spots : BaseActivity() {
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
                } )
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

}