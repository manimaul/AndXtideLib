package com.mxmariner.tides.main.util

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.mxmariner.tides.main.extensions.args
import com.mxmariner.tides.main.model.ActivityResult
import com.mxmariner.tides.main.util.ActivityResultBroker.Companion.KEY_OPTIONS
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class RxActivityResult @Inject constructor() {

    fun startActivityForResultSingle(fragmentManager: FragmentManager,
                                     intent: Intent?,
                                     options: Bundle? = null): Single<ActivityResult> {
        return Single.defer {
            val broker = ActivityResultBroker()
            val args = Bundle()
            args.putParcelable(ActivityResultBroker.KEY_INTENT, intent)
            args.putInt(ActivityResultBroker.KEY_REQUEST_CODE, rand16BitInt())
            if (options != null) {
                args.putParcelable(KEY_OPTIONS, options)
            }
            broker.arguments = args

            fragmentManager.beginTransaction()
                    .add(broker, ActivityResultBroker.TAG)
                    .commit()
            broker.resultSingle
        }.subscribeOn(AndroidSchedulers.mainThread())
    }
}

class ActivityResultBroker : Fragment() {

    private val resultSubject = PublishSubject.create<ActivityResult>()
    val resultSingle: Single<ActivityResult>
        get() = resultSubject.singleOrError()

    companion object {
        internal val TAG = ActivityResultBroker::class.java.simpleName
        internal const val KEY_INTENT = "KEY_INTENT"
        internal const val KEY_OPTIONS = "KEY_OPTIONS"
        internal const val KEY_REQUEST_CODE = "KEY_REQUEST_CODE"
    }

    private val requestCode: Int
        get() = args.getInt(KEY_REQUEST_CODE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val intent = args.getParcelable<Intent>(KEY_INTENT)
        val options = args.getBundle(KEY_OPTIONS)
        if (options != null) {
            startActivityForResult(intent, requestCode, options)
        } else {
            startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == this.requestCode) {
            resultSubject.onNext(ActivityResult(requestCode, resultCode, data))
            resultSubject.onComplete()
            fragmentManager?.beginTransaction()
                    ?.remove(this)
                    ?.commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resultSubject.onComplete()
    }
}
