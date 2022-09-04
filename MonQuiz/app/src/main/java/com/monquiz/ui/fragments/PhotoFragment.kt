package com.monquiz.ui.fragments

import android.animation.Animator
import com.monquiz.interfaces.SelectedImageCallBack
import android.view.LayoutInflater
import android.view.ViewGroup
import com.monquiz.R
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import com.monquiz.utils.CommonMethods
import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import androidx.annotation.NonNull
import com.google.android.cameraview.CameraView
import com.monquiz.utils.Constants
import com.monquiz.utils.Permissions
import org.jetbrains.annotations.Nullable
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class PhotoFragment : BaseFragment(), View.OnClickListener {
    private var cameraView: CameraView? = null
    private var ivSwitchCamera: ImageView? = null
    private var ivFlash: ImageView? = null
    private var ivTakePhoto: ImageView? = null
    private var mShutter: View? = null
    private var currentFlash = 0
    private var backgroundHandler: Handler? = null
    private var selectedImageCallBack: SelectedImageCallBack? = null
    @Nullable
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        var view: View? = null
        view = if (Permissions.checkPermissionForAccessCamera(getActivity())) {
            inflater.inflate(R.layout.layout_photo_fragment, container, false)
        } else {
            inflater.inflate(R.layout.layout_photo_fragment_without_camera_view, container, false)
        }
        cameraView = view!!.findViewById(R.id.cameraView)
        ivSwitchCamera = view!!.findViewById(R.id.ivSwitchCamera)
        ivFlash = view!!.findViewById(R.id.ivFlash)
        ivTakePhoto = view!!.findViewById(R.id.ivTakePhoto)
        mShutter = view!!.findViewById(R.id.mShutter)
        selectedImageCallBack = getActivity() as SelectedImageCallBack?
        assert(selectedImageCallBack != null)
        selectedImageCallBack!!.selectedImage("", false, "")
        if (cameraView != null) {
            cameraView!!.addCallback(mCallback)
        }
        initializeListeners()
        return view
    }

    private fun initializeUi(view: View?) {

    }

    private fun initializeListeners() {
        ivSwitchCamera!!.setOnClickListener(this)
        ivFlash!!.setOnClickListener(this)
        ivTakePhoto!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (cameraView != null) cameraView!!.start()
    }

    override fun onPause() {
        if (cameraView != null) cameraView!!.stop()
        super.onPause()
    }

   override fun onDestroy() {
        super.onDestroy()
        if (backgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                backgroundHandler!!.looper.quitSafely()
            } else {
                backgroundHandler!!.looper.quit()
            }
            backgroundHandler = null
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.ivSwitchCamera -> if (cameraView != null) {
                val facing: Int = cameraView!!.facing
                cameraView!!.facing = if (facing == CameraView.FACING_FRONT) CameraView.FACING_BACK else CameraView.FACING_FRONT
            }
            R.id.ivFlash -> if (cameraView != null) {
                currentFlash = (currentFlash + 1) % FLASH_OPTIONS.size
                cameraView!!.flash = FLASH_OPTIONS[currentFlash]
                ivFlash!!.setImageResource(FLASH_ICONS[currentFlash])
            }
            R.id.ivTakePhoto -> {
                if (cameraView != null) {
                    cameraView!!.takePicture()
                }
                animateShutter()
            }
            else -> {
            }
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun animateShutter() {
        mShutter!!.visibility = View.VISIBLE
        mShutter!!.alpha = 0f
        val alphaInAnim = ObjectAnimator.ofFloat(mShutter, "alpha", 0f, 0.8f)
        alphaInAnim.duration = 100
        alphaInAnim.startDelay = 100
        alphaInAnim.interpolator = ACCELERATE_INTERPOLATOR
        val alphaOutAnim = ObjectAnimator.ofFloat(mShutter, "alpha", 0.8f, 0f)
        alphaOutAnim.duration = 200
        alphaOutAnim.interpolator = DECELERATE_INTERPOLATOR
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mShutter!!.visibility = View.GONE
            }
        })
        animatorSet.start()
    }

    private fun getBackgroundHandler(): Handler {
        if (backgroundHandler == null) {
            val thread = HandlerThread("background")
            thread.start()
            backgroundHandler = Handler(thread.looper)
        }
        return backgroundHandler!!
    }

    private val mCallback: CameraView.Callback = object : CameraView.Callback() {
        override fun onCameraOpened(cameraView: CameraView?) {}
        override fun onCameraClosed(cameraView: CameraView?) {}
        override fun onPictureTaken(cameraView: CameraView?, data: ByteArray) {
            // showToast(getString(R.string.picture_taken));
            CommonMethods.infoLog("onPictureTaken " + data.size)
            getBackgroundHandler().post {
                val wallpaperDirectory = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + Constants.IMAGE_FILE_FOLDER
                )
                wallpaperDirectory.mkdir()
                val imageFilePath: String = System.currentTimeMillis().toString() //Constants.IMAGE_FILE_NAME;
                val imagePath = "$wallpaperDirectory/$imageFilePath.jpg"
                selectedImageCallBack!!.selectedImage(imagePath, false, "")
                val file = File(imagePath)
                try {
                    val out = FileOutputStream(file)
                    out.write(data)
                    out.flush()
                    out.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                galleryAddPic(imagePath)
            }
        }
    }

    private fun galleryAddPic(imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        requireActivity().sendBroadcast(mediaScanIntent)
    }

    companion object {
        private val ACCELERATE_INTERPOLATOR: Interpolator = AccelerateInterpolator()
        private val DECELERATE_INTERPOLATOR: Interpolator = DecelerateInterpolator()
        private val FLASH_OPTIONS = intArrayOf(
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON
        )
        private val FLASH_ICONS = intArrayOf(
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on
        )
    }
}