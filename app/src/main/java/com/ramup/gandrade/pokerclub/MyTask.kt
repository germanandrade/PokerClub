package com.ramup.gandrade.pokerclub

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.util.concurrent.Executor

class MyTask(): Task<Void>() {

    override fun isComplete(): Boolean {

        return false
    }

    override fun addOnFailureListener(failureListener: OnFailureListener): Task<Void> {
        failureListener.onFailure(Exception())
        return this
    }

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<Void> {
        return this
    }

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResult(): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in Void>): Task<Void> {
        return this
    }

    override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in Void>): Task<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in Void>): Task<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isSuccessful(): Boolean {
        return false
    }

    override fun getException(): Exception? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}