package com.example.notes_firebase

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes_firebase.CreateNote.firebaseModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.notes_grid.view.*
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var noteAdapter:FirestoreRecyclerAdapter<firebaseModel,NoteViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.title=getString(R.string.home_title)
        val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#FF5693FD"))
        actionBar?.setBackgroundDrawable(colorDrawable)

        auth=FirebaseAuth.getInstance()
        user= auth.currentUser!!
        firestore=FirebaseFirestore.getInstance()

        newNoteFAB.setOnClickListener{
            startActivity(Intent(this,CreateNote::class.java))
        }

        val query=firestore.collection("notes")
            .document(user.uid)
            .collection("myNotes")
            .orderBy("title", Query.Direction.ASCENDING)

        val alluser : FirestoreRecyclerOptions<firebaseModel> = FirestoreRecyclerOptions.Builder<firebaseModel>()
            .setQuery(query,firebaseModel::class.java)
            .build()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED
        )

        noteAdapter = object: FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>(alluser) {

            override  fun onBindViewHolder(noteViewHolder:NoteViewHolder,i:Int,firebasemodel: firebaseModel) {

                val colorCode:Int=getRandomColor()
                noteViewHolder.itemView.note.setBackgroundColor(noteViewHolder.itemView.resources.getColor(colorCode,null))
                noteViewHolder.itemView.noteTitle.text=firebasemodel.title
                noteViewHolder.itemView.noteDesc.text=firebasemodel.description

                val docId:String=noteAdapter.snapshots.getSnapshot(i).id

                noteViewHolder.itemView.noteAction.setOnClickListener { v:View ->
                    val popup=PopupMenu(v.context,v)
                    popup.gravity=Gravity.END

                    popup.menu.add("Edit").setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{
                        override fun onMenuItemClick(menu: MenuItem?): Boolean {
                            val intent=Intent(v.context,EditNote::class.java)
                            intent.putExtra("title",firebasemodel.title)
                            intent.putExtra("description",firebasemodel.description)
                            intent.putExtra("noteId",docId)
                            v.context.startActivity(intent)
                            return false
                        }
                    })

                    popup.menu.add("Share").setOnMenuItemClickListener {
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT, "${firebasemodel.title}\n\n${firebasemodel.description}")
                        intent.type = "text/plain"
                        startActivity(Intent.createChooser(intent, "Select Your application:"))
                        false
                    }

                    popup.menu.add("Export to PDF").setOnMenuItemClickListener {
                        val dateTime1 = LocalDateTime.now()
                        val format=DateTimeFormatter.ofPattern("ddMMyyyyHHmmss")
                        val formatDateTime=dateTime1.format(format)

                        val filePath=Environment.getExternalStorageDirectory().path + "/Download/Take_Notes" + formatDateTime + ".pdf"
                        val file= File(filePath)
                        val pdfDocument=PdfDocument()
                        val pageInfo=PdfDocument.PageInfo.Builder(300,600,1).create()
                        val page=pdfDocument.startPage(pageInfo)

                        val paint= Paint()
                        val stringPdf=firebasemodel.title+"\n\n"+firebasemodel.description

                        val x = 10
                        var y = 25

                        for (line in stringPdf.split("\n")) {
                            page.canvas.drawText(line, x.toFloat(), y.toFloat(), paint)
                            y += (paint.descent() - paint.ascent()).toInt()
                        }

                        pdfDocument.finishPage(page)
                        try {
                            pdfDocument.writeTo(FileOutputStream(file))
                        } catch (e:Exception){
                            Toast.makeText(v.context,e.message.toString(),Toast.LENGTH_SHORT).show()
                        } finally {
                            pdfDocument.close()
                        }

                        false
                    }

                    popup.menu.add("Delete").setOnMenuItemClickListener {
                        val builder=AlertDialog.Builder(this@HomeActivity)
                        builder.setTitle("Delete Note")
                            .setMessage("Are you sure you want to delete this note??")

                        builder.setPositiveButton("Yes"){_,_ ->
                            val docRef = firestore.collection("notes")
                                .document(user.uid)
                                .collection("myNotes")
                                .document(docId)

                            docRef.delete().addOnSuccessListener {
                                /*Toast.makeText(applicationContext,
                                    "Note is deleted",
                                    Toast.LENGTH_SHORT).show()*/
                            }.addOnFailureListener {
                                Toast.makeText(applicationContext,
                                    "Failed to delete",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                        builder.setNegativeButton("No"){_,_ -> }
                        val alertDialog=builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                        false
                    }

                    popup.show()
                }

                noteViewHolder.itemView.setOnClickListener {
                    val intent=Intent(applicationContext,PreviewNote::class.java)
                    intent.putExtra("title",firebasemodel.title)
                    intent.putExtra("description",firebasemodel.description)
                    intent.putExtra("noteId",docId)
                    startActivity(intent)
                }
            }

            override fun onCreateViewHolder(parent:ViewGroup,viewType:Int) : NoteViewHolder {
                val v:View=LayoutInflater.from(parent.context).inflate(R.layout.notes_grid,parent,false)
                return NoteViewHolder(v)
            }

            override fun onDataChanged() {
                super.onDataChanged()
                no_notes.visibility = if(noteAdapter.itemCount==0)
                    View.VISIBLE
                else
                    View.GONE
            }
        }

        recyclerView.hasFixedSize() // true if the app has specified that changes in adapter content cannot change the size of the RecyclerView itself.
        val staggeredGridLayoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager=staggeredGridLayoutManager
        recyclerView.adapter=noteAdapter
    }

    private fun getRandomColor(): Int {
        val colorCode = ArrayList<Int>()

        colorCode.add(R.color.color1)
        colorCode.add(R.color.color2)
        colorCode.add(R.color.color3)
        colorCode.add(R.color.color4)
        colorCode.add(R.color.color5)
        colorCode.add(R.color.color6)
        colorCode.add(R.color.color7)
        colorCode.add(R.color.color8)
        colorCode.add(R.color.color9)
        colorCode.add(R.color.color10)
        colorCode.add(R.color.color11)
        colorCode.add(R.color.color12)
        colorCode.add(R.color.color13)
        colorCode.add(R.color.color14)
        colorCode.add(R.color.color15)

        return colorCode[Random().nextInt(colorCode.size)]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_setting->{
                val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri: Uri =Uri.fromParts("package",packageName,null)
                intent.data = uri
                startActivity(intent)
            }
            R.id.logout -> {
                auth.signOut()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            R.id.changePassword -> {
                startActivity(Intent(this,ChangePassword::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    class NoteViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val notetitle: TextView
        private val notecontent: TextView
        var mnote: LinearLayout

        init {
            notetitle = itemView.findViewById(R.id.noteTitle)
            notecontent = itemView.findViewById(R.id.noteDesc)
            mnote = itemView.findViewById(R.id.note)

            //Animate Recyclerview
            val translateAnim: Animation =
                AnimationUtils.loadAnimation(itemView.context, R.anim.translate_anim)
            mnote.animation = translateAnim
        }
    }

    private var backPressedTime:Long=0
    override fun onBackPressed() {

        if(backPressedTime+2000>System.currentTimeMillis()) {
            super.onBackPressed()
            exitProcess(0)
        }
        else {
            val snackBar=
                Snackbar.make(homelayout,"Press back again to exit",Snackbar.LENGTH_SHORT)
            val snackBarView=snackBar.view
            snackBarView.setBackgroundColor(Color.BLACK)
            snackBar.show()
        }
        backPressedTime= System.currentTimeMillis()
    }
}
