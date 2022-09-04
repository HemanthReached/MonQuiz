package com.monquiz.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gtomato.android.ui.transformer.ParameterizedViewTransformer
import com.gtomato.android.ui.widget.CarouselView
import com.monquiz.BuildConfig
import com.monquiz.R
import com.monquiz.eventbus.GameSelectEvent
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PreferenceConnector
import com.monquiz.utils.PrefsHelper
import java.util.*
import org.greenrobot.eventbus.EventBus




/**
 * @author sunny-chung
 */
class CarouselFragment : Fragment() {
    var carousel: CarouselView? = null
    var lblSelectedIndex: TextView? = null
    private var mListOfCategoryImages: LinkedList<String>? = null
    private var mListOfCategoryTitles: LinkedList<String>? = null
    private var titleCallback =
        CarouselTitle { position: Int -> }

    fun interface CarouselTitle {
        fun changePosition(position: Int)
    }

    //THIS HAS TO BE CALLED ALWAYS AFTER INITIALIZING THIS FRAGMENT
    fun setParams(context: FragmentActivity?, listOfCategoryImages: LinkedList<String>?,
        listOfCategoryTitles: LinkedList<String>?, titleCallback: CarouselTitle) {
        mListOfCategoryImages = listOfCategoryImages
        mListOfCategoryTitles = listOfCategoryTitles
        this.titleCallback = titleCallback
    }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.carousel_fragment_main, container, false)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: Bundle? = getArguments()
        carousel = view.findViewById(R.id.carousel)
        lblSelectedIndex = view.findViewById(R.id.lblSelectedIndex)
        val viewTransformer = ParameterizedViewTransformer()
        viewTransformer.setMinScaleY(1f)
        viewTransformer.setMinScaleX(1f)
        viewTransformer.setOffsetXPercent(1.1f)
        viewTransformer.setOffsetYPercent(0f)
        //		viewTransformer.setScaleXOffset(-1f);
        viewTransformer.setScaleLargestAtCenter(true)
        viewTransformer.setScaleXFactor(1.5f)
        viewTransformer.setScaleYFactor(1.5f)
        carousel!!.setTransformer(viewTransformer)
        carousel!!.setInfinite(true)
        carousel!!.setAdapter(getActivity()?.let { RandomPageAdapter(
                    it, mListOfCategoryImages!!, mListOfCategoryTitles, titleCallback)
            }
        )
        //TODO scroll and touch not working at start and end
        carousel!!.setOnScrollListener(object : CarouselView.OnScrollListener() {
            override fun onScrollEnd(carouselView: CarouselView) {
                super.onScrollEnd(carouselView)
                val adapterPosition = carouselView.currentAdapterPosition

                PrefsHelper().savePref(OwlizConstants.GameName,mListOfCategoryTitles!![adapterPosition])
                PrefsHelper().savePref(OwlizConstants.GameType,"0"+(adapterPosition+1))
                PreferenceConnector.writeString(getActivity(), getString(R.string.play_selected_category_number),
                    "0" + (adapterPosition + 1))
                PreferenceConnector.writeString(getActivity(), getString(R.string.play_selected_category_name),
                    mListOfCategoryTitles!![adapterPosition])
                //                titleCallback.changeTitle(mListOfCategoryTitles.get(adapterPosition));
                titleCallback.changePosition(adapterPosition)

                EventBus.getDefault().post(GameSelectEvent(mListOfCategoryTitles!![adapterPosition],
                    (adapterPosition+1).toString(),adapterPosition))
            }
        })
        carousel!!.setOnItemSelectedListener(object : CarouselView.OnItemSelectedListener {
            override fun onItemSelected(p0: CarouselView?, p1: Int, p2: Int,
                p3: RecyclerView.Adapter<*>?) {
                Log.i("TAG","SelectedGame")
            }

            override fun onItemDeselected(p0: CarouselView?, p1: Int, p2: Int,
                p3: RecyclerView.Adapter<*>?) {

            }
            /*override fun onItemSelected(
                carouselView: CarouselView?,
                position: Int,
                adapterPosition: Int,
                adapter: RecyclerView.Adapter<ViewHolder>?
            ) {
//				ivCategoryPic.setImageResource(listOfCategoryImages.get(position));
                //	lblSelectedIndex.setText("Selected Position " + position);
            }



            override fun onItemDeselected(
                carouselView: CarouselView?,
                position: Int,
                adapterPosition: Int,
                adapter: RecyclerView.Adapter<ViewHolder>?
            ) {
            }*/
        })
        if (!BuildConfig.PRO_VERSION) {
            carousel!!.smoothScrollToPosition(2)
        } else {
            carousel!!.smoothScrollToPosition(1)
        }
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        //@InjectView(R.id.carousel_child_container)
        var imageView: ImageView? = itemView!!.findViewById(R.id.carousel_child_container)

        //		TextView textView;
       /* init {
            ButterKnife.inject(this, itemView)
          		//imageView = itemView!!.findViewById(R.id.carousel_image);
//			textView = itemView.findViewById(R.id.carousel_text);
        }*/
    }

    class RandomPageAdapter(private val context: Context,
        private val mListOfCategoryImages: LinkedList<String>?,
        private val mListOfCategoryTitles: LinkedList<String>?,
        private val titleCallback: CarouselTitle) : RecyclerView.Adapter<ViewHolder?>() {
        var size = 0
        var width = 90
        var height = 90
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val view: View = RandomPageFragment.createView(context, width, height,
                mListOfCategoryImages!![0]!!)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//		    holder.imageView.setImageResource(mListOfCategoryImages.get(position));
//		    holder.textView.setText(""+position);
            titleCallback.changePosition(position)
            RandomPageFragment.initializeImageView(holder.imageView!!,
                mListOfCategoryImages!![position],context)
        }

        override fun getItemCount(): Int {
            return mListOfCategoryImages?.size ?: 0
        }
    }


}