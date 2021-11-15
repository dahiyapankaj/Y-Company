package com.y.company.adapters

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import java.util.ArrayList
import com.y.company.adapters.FirestoreAdapter


abstract class FirestoreAdapter<VH : RecyclerView.ViewHolder?>(private var mQuery: Query?) :
    RecyclerView.Adapter<VH>(), EventListener<QuerySnapshot> {
    private var mRegistration: ListenerRegistration? = null
    private val mSnapshots = ArrayList<DocumentSnapshot>()
    fun startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery!!.addSnapshotListener(this)
        }
    }

    fun stopListening() {
        if (mRegistration != null) {
            mRegistration!!.remove()
            mRegistration = null
        }
        mSnapshots.clear()
        notifyDataSetChanged()
    }

    fun setQuery(query: Query?) {
        // Stop listening
        stopListening()

        // Clear existing data
        mSnapshots.clear()
        notifyDataSetChanged()

        // Listen to new query
        mQuery = query
        startListening()
    }

    override fun getItemCount(): Int {
        return mSnapshots.size
    }

    protected fun getSnapshot(index: Int): DocumentSnapshot {
        return mSnapshots[index]
    }

    protected open fun onError(e: FirebaseFirestoreException?) {}
    protected open fun onDataChanged() {}

    // Add this method
    override fun onEvent(
        documentSnapshots: QuerySnapshot?,
        e: FirebaseFirestoreException?
    ) {

        // Handle errors
        if (e != null) {
            Log.w("TAG", "onEvent:error", e)
            return
        }

        if(documentSnapshots == null){
            Log.w("TAG", "error", e)
            return
        }
        // Dispatch the event
        for (change in documentSnapshots.documentChanges) {
            // Snapshot of the changed document
            val snapshot: DocumentSnapshot = change.document
            when (change.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(change) // Add this line
                DocumentChange.Type.MODIFIED -> onDocumentModified(change) // Add this line
                DocumentChange.Type.REMOVED -> onDocumentRemoved(change) // Add this line
            }
        }
        onDataChanged()
    }

    private fun onDocumentAdded(change: DocumentChange) {
        mSnapshots.add(change.newIndex, change.document)
        notifyItemInserted(change.newIndex)
    }

    private fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            // Item changed but remained in same position
            mSnapshots[change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else {
            // Item changed and changed position
            mSnapshots.removeAt(change.oldIndex)
            mSnapshots.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    private fun onDocumentRemoved(change: DocumentChange) {
        mSnapshots.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

}