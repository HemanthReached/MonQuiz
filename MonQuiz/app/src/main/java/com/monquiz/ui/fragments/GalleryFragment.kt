package com.monquiz.ui.fragments

import android.annotation.SuppressLint
import android.database.Cursor
import com.monquiz.adapter.GalleryAdapter.ItemClickListener
import android.widget.TextView
import com.fenchtose.nocropper.CropperView
import com.google.android.material.appbar.AppBarLayout
import com.monquiz.interfaces.SelectedImageCallBack
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.monquiz.R
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.monquiz.adapter.GalleryAdapter
import es.dmoral.toasty.Toasty
import java.lang.Exception
import java.util.*

class GalleryFragment : BaseFragment(), ItemClickListener, View.OnClickListener {
    private var rvGallery: RecyclerView? = null
    private var tvError: TextView? = null
    private var ivEnlarge: ImageView? = null
    private var ivRotate: ImageView? = null
    private var cropperView: CropperView? = null
    private var appBarLayout: AppBarLayout? = null
    private var selectedImageCallBack: SelectedImageCallBack? = null
    private var angle = 0
    private var isSnappedToCenter = false
    private var bitmap: Bitmap? = null
    private var listOfImages: LinkedList<String?>? = null
    private var fromScreen: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_gallery_fragment, container, false)
        dataFromIntent
        initializeUi(view)
        initializeListeners()
        setBehaviour(view)
        imagesFromExternalMemory
        imagesFromInternalMemory
        goToAdapter()
        return view
    }

    private val dataFromIntent: Unit
        get() {
            val bundle: Bundle? = arguments
            if (bundle != null) {
                if (bundle.containsKey(getString(R.string.from_screen))) {
                    fromScreen = bundle.getString(getString(R.string.from_screen))
                }
            }
        }

    private fun initializeUi(view: View) {
        cropperView = view.findViewById(R.id.cropperView)
        rvGallery = view.findViewById(R.id.rvGallery)
        tvError = view.findViewById(R.id.tvError)
        ivEnlarge = view.findViewById(R.id.ivEnlarge)
        ivRotate = view.findViewById(R.id.ivRotate)
        selectedImageCallBack = activity as SelectedImageCallBack?
        listOfImages = LinkedList()
    }

    private fun initializeListeners() {
        ivEnlarge!!.setOnClickListener(this)
        ivRotate!!.setOnClickListener(this)
    }

    private fun setBehaviour(view: View) {
        appBarLayout = view.findViewById(R.id.appBarLayout)
        val params = appBarLayout!!.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = AppBarLayout.Behavior()
        val behavior = (params.behavior as AppBarLayout.Behavior?)!!
        behavior.setDragCallback(object : DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
    }//Store the path of the image//Total number of images

    //Stores all the images from the gallery in Cursor
    private val imagesFromInternalMemory: Unit
        @SuppressLint("Range") get() {
            try {
                if (activity != null) {
                    val columns =
                        arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN)
                    val orderBy = MediaStore.Images.Media.DATE_TAKEN
                    //Stores all the images from the gallery in Cursor
                    val cursor: Cursor = requireActivity().contentResolver.query(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        orderBy
                    )!!
                    val count = cursor.count //Total number of images
                    for (i in 0 until count) {
                        cursor.moveToPosition(i)
                        val date =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))
                        val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                        listOfImages!!.add(cursor.getString(dataColumnIndex)) //Store the path of the image
                    }
                    cursor.close()
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }//Store the path of the image//Total number of images

    //Stores all the images from the gallery in Cursor
    private val imagesFromExternalMemory: Unit
        get() {
            try {
                if (activity != null) {
                    val columns =
                        arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN)
                    val orderBy = MediaStore.Images.Media.DATE_TAKEN
                    //Stores all the images from the gallery in Cursor
                    val cursor: Cursor = requireActivity().contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        orderBy
                    )!!
                    val count = cursor.count //Total number of images
                    for (i in 0 until count) {
                        cursor.moveToPosition(i)
                        val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                        listOfImages!!.add(cursor.getString(dataColumnIndex)) //Store the path of the image
                    }
                    listOfImages!!.reverse()
                    cursor.close()
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }//Store the path of the image//Total number of images

    //Stores all the images from the gallery in Cursor
    private val videosFromInternalMemory: Unit
        @SuppressLint("Range") get() {
            val columns = arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_TAKEN)
            val orderBy = MediaStore.Video.Media.DATE_TAKEN
            //Stores all the images from the gallery in Cursor
            val cursor: Cursor = requireActivity().contentResolver
                .query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, columns, null, null, orderBy)!!
            val count = cursor.count //Total number of images
            for (i in 0 until count) {
                cursor.moveToPosition(i)
                val date =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN))
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
                listOfImages!!.add(cursor.getString(dataColumnIndex)) //Store the path of the image
            }
            cursor.close()
        }//Store the path of the image//Total number of images

    //Stores all the images from the gallery in Cursor
    private val videosFromExternalMemory: Unit
        get() {
            val columns = arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_TAKEN)
            val orderBy = MediaStore.Video.Media.DATE_TAKEN
            //Stores all the images from the gallery in Cursor
            val cursor: Cursor = requireActivity().contentResolver
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy)!!
            val count = cursor.count //Total number of images
            for (i in 0 until count) {
                cursor.moveToPosition(i)
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
                listOfImages!!.add(cursor.getString(dataColumnIndex)) //Store the path of the image
            }
            listOfImages!!.reverse()
            cursor.close()
        }

    private fun goToAdapter() {
        if (listOfImages != null && listOfImages!!.size != 0) {
            imageLayoutVisible()
            initializeAdapter()
        } else {
            imageLayoutInVisible()
            Toasty.error(requireContext(), getString(R.string.no_images_found), Toasty.LENGTH_SHORT).show()
          //  showToast(getString(R.string.no_images_found))
        }
    }

    private fun initializeAdapter() {
        val imgUrl = listOfImages!![0]
        cropperView!!.visibility = View.VISIBLE
        Glide.with(requireContext())
            .asBitmap()
            .load(imgUrl)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    bitmap = resource
                    cropperView!!.setImageBitmap(resource)
                }
            })
        selectedImageCallBack!!.selectedImage(listOfImages!![0], true, "")
        val galleryAdapter = GalleryAdapter(requireActivity(), listOfImages)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 4)
        rvGallery!!.layoutManager = layoutManager
        rvGallery!!.itemAnimator = DefaultItemAnimator()
        galleryAdapter.setClickListener(this)
        rvGallery!!.adapter = galleryAdapter
    }

    private fun imageLayoutVisible() {
        appBarLayout!!.visibility = View.VISIBLE
        rvGallery!!.visibility = View.VISIBLE
        tvError!!.visibility = View.GONE
    }

    private fun imageLayoutInVisible() {
        appBarLayout!!.visibility = View.GONE
        rvGallery!!.visibility = View.GONE
        tvError!!.visibility = View.VISIBLE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivEnlarge -> snapImage()
            R.id.ivRotate -> rotateImage()
            else -> {}
        }
    }

    private fun snapImage() {
        if (isSnappedToCenter) { cropperView!!.cropToCenter() }
        else { cropperView!!.fitToCenter() }
        isSnappedToCenter = !isSnappedToCenter
    }

    private fun rotateImage() {
        if (bitmap == null) {
            Log.e("image", "bitmap is not loaded yet")
            return
        }
        angle += 90
        val mBitmap = rotateBitmap(bitmap!!, angle.toFloat())
        cropperView!!.setImageBitmap(mBitmap)
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    override fun onClick(view: View?, position: Int) {
        showProgressBar(getString(R.string.loading_please_wait))
        appBarLayout!!.setExpanded(true, true)
        val imgUrl = listOfImages!![position]
        selectedImageCallBack!!.selectedImage(imgUrl, true, "")
        cropperView!!.visibility = View.VISIBLE
        Glide.with(requireContext())
            .asBitmap()
            .load(imgUrl)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(resource: Bitmap,
                    transition: Transition<in Bitmap?>?) {
                    bitmap = resource
                    cropperView!!.setImageBitmap(resource)
                    closeProgressbar()                  }
            })
    }
}