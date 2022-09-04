package com.monquiz.ui

import com.monquiz.utils.CustomViewPager
import android.os.Bundle
import com.monquiz.R
import com.monquiz.utils.PreferenceConnector

import android.view.LayoutInflater
import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.*
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import com.monquiz.network.InternetStateChecker
import com.monquiz.utils.CustomRotationAnimation
import java.util.*

class WelcomeActivity : BaseActivity(), View.OnClickListener {
    private var viewPager: CustomViewPager? = null
    private var llDots: LinearLayout? = null
    private var btnGetStarted: Button? = null
    private var listOfImages: LinkedList<Int>? = null
    private lateinit var internetchecker : InternetStateChecker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        prepareDetails()
    }

    private fun prepareDetails() {
        val isFirstTime = PreferenceConnector.readBoolean(this,
            getString(R.string.is_first_time_welcome_screen), true)
        val isFirstTimeLogin =
            PreferenceConnector.readBoolean(this, getString(R.string.is_first_time_login), true)
        if (isFirstTimeLogin) {
            if (isFirstTime) {
                initializeUi()
                initializeListeners()
                prepareImagesList()
                viewPagerInitialization()
                // enable sound by default.
                PreferenceConnector.writeBoolean(
                    this,
                    getString(R.string.is_audio_mode_enable),
                    true
                )
            } else {
                goToLoginScreen()
            }
        } else {
            goToDashboard()
        }
    }

    private fun initializeUi() {
        llDots = findViewById(R.id.llDots)
        viewPager = findViewById(R.id.viewPager)
        viewPager!!.offscreenPageLimit = 0
        btnGetStarted = findViewById(R.id.btnGetStarted)
        listOfImages = LinkedList()
    }

    private fun initializeListeners() {
        btnGetStarted!!.setOnClickListener(this)
    }

    private fun prepareImagesList() {
//        listOfImages.add(R.drawable.ic_onboarding1);
//        listOfImages.add(R.drawable.ic_onboarding2);
//        listOfImages.add(R.drawable.ic_onboarding3);
//        listOfImages.add(R.drawable.ic_onboarding4);
//        listOfImages.add(R.drawable.ic_onboarding5);
    }

    private fun viewPagerInitialization() {
        val onBoardingPagerAdapter = OnBoardingPagerAdapter()
        viewPager!!.adapter= onBoardingPagerAdapter
        //viewPager!!.adapter=onBoardingPagerAdapter
        for (i in 0 until onBoardingPagerAdapter.count) {
            val imgDot = ImageButton(this)
            imgDot.setTag(i)
            imgDot.setImageResource(R.drawable.dot_selector)
            imgDot.setBackgroundResource(0)
            imgDot.setPadding(0, 0, 0, 0)
            val params = LinearLayout.LayoutParams(20, 20)
            params.setMargins(0, 0, 5, 0)
            imgDot.layoutParams = params
            if (i == 0) imgDot.isSelected = true
            llDots!!.addView(imgDot)
        }
        viewPager!!.setOnPageChangeListener(object : CustomViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                for (i in 0 until onBoardingPagerAdapter.count) {
                    if (i != position) {
                        llDots!!.findViewWithTag<View>(i).isSelected = false
                    }
                }
                llDots!!.findViewWithTag<View>(position).isSelected = true
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun goToDashboard() {
        val dashboardIntent = Intent(this, DashboardActivity::class.java)
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(dashboardIntent)
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btnGetStarted -> { PreferenceConnector.writeBoolean(this,
                    getString(R.string.is_first_time_welcome_screen), false)
                goToLoginScreen()
            }
            else -> {}
        }
    }

    private fun goToLoginScreen() {
        val loginIntent = Intent(this, SignUpActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

     inner class OnBoardingPagerAdapter: PagerAdapter() {
         val inflater: LayoutInflater = layoutInflater
         override fun getCount(): Int {
            return 5
            //            return listOfImages.size();
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getItemPosition(`object`: Any): Int {
            return super.getItemPosition(`object`)
        }

        @SuppressLint("SetTextI18n")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var itemView: View? = null
            if (position == 0) {
                itemView =
                    inflater.inflate(R.layout.layout_on_boarding_screen_one, container, false)
                val view = itemView
                view!!.visibility = View.INVISIBLE
                view.post {
                    view.visibility = View.VISIBLE
                    val ivSlide1Girl = view.findViewById<ImageView>(R.id.ivSlide1Girl)
                    val ivSlide1Couch = view.findViewById<ImageView>(R.id.ivSlide1Couch)
                    val ivSlide1Phone = view.findViewById<ImageView>(R.id.ivSlide1Phone)
                    val ivSlide1Icon1 = view.findViewById<ImageView>(R.id.ivSlide1Icon1)
                    val ivSlide1Icon2 = view.findViewById<ImageView>(R.id.ivSlide1Icon2)
                    val ivSlide1Icon3 = view.findViewById<ImageView>(R.id.ivSlide1Icon3)
                    val ivSlide1Icon4 = view.findViewById<ImageView>(R.id.ivSlide1Icon4)
                    val ivSlide1Icon5 = view.findViewById<ImageView>(R.id.ivSlide1Icon5)
                    val ivSlide1Icon6 = view.findViewById<ImageView>(R.id.ivSlide1Icon6)
                    val ivSlide1Circle1 = view.findViewById<ImageView>(R.id.ivSlide1Circle1)
                    val ivSlide1Circle2 = view.findViewById<ImageView>(R.id.ivSlide1Circle2)
                    val ivSlide1Circle3 = view.findViewById<ImageView>(R.id.ivSlide1Circle3)
                    val ivSlide1PhoneMoney = view.findViewById<ImageView>(R.id.ivSlide1PhoneMoney)
                    val ivSlide1PhoneSplash = view.findViewById<ImageView>(R.id.ivSlide1PhoneSplash)
                    val ivSlide1CirclesCenter =
                        view.findViewById<ImageView>(R.id.ivSlide1CirclesCenter)
                    val tvSlideOneDescriptionOne =
                        view.findViewById<TextView>(R.id.tvSlideOneDescriptionOne)
                    val tvSlideOneDescriptionTwo =
                        view.findViewById<TextView>(R.id.tvSlideOneDescriptionTwo)
                    slideOneAnimation(
                        ivSlide1Girl,
                        ivSlide1Couch,
                        ivSlide1Phone,
                        ivSlide1Circle1,
                        ivSlide1Circle2,
                        ivSlide1Circle3,
                        ivSlide1CirclesCenter,
                        ivSlide1PhoneMoney,
                        ivSlide1PhoneSplash,
                        ivSlide1Icon1,
                        ivSlide1Icon2,
                        ivSlide1Icon3,
                        ivSlide1Icon4,
                        ivSlide1Icon5,
                        ivSlide1Icon6,
                        tvSlideOneDescriptionOne,
                        tvSlideOneDescriptionTwo
                    )
                }
            } else if (position == 1) {
                itemView =
                    inflater.inflate(R.layout.layout_on_boarding_screen_two, container, false)
                val view = itemView
                view!!.visibility = View.INVISIBLE
                view.post {
                    view.visibility = View.VISIBLE
                    val ivSlideTwoGirl = view.findViewById<ImageView>(R.id.ivSlideTwoGirl)
                    val ivSlideTwoGirlLeft = view.findViewById<ImageView>(R.id.ivSlideTwoGirlLeft)
                    val ivSlideTwoBoyRight = view.findViewById<ImageView>(R.id.ivSlideTwoBoyRight)
                    val ivSlideTwoCrown = view.findViewById<ImageView>(R.id.ivSlideTwoCrown)
                    val tvTittle = view.findViewById<TextView>(R.id.tvTittle)
                    slideTwoAnimation(
                        ivSlideTwoGirl,
                        ivSlideTwoGirlLeft,
                        ivSlideTwoBoyRight,
                        ivSlideTwoCrown,
                        tvTittle
                    )
                }
            } else if (position == 2) {
                itemView =
                    inflater.inflate(R.layout.layout_on_boarding_screen_three, container, false)
                val view = itemView
                view!!.visibility = View.INVISIBLE
                view.post {
                    view.visibility = View.VISIBLE
                    val ivSlideThreeCoinsOne =
                        view.findViewById<ImageView>(R.id.ivSlideThreeCoinsOne)
                    val ivSlideThreeCoinsTwo =
                        view.findViewById<ImageView>(R.id.ivSlideThreeCoinsTwo)
                    val ivSlideThreeCoinsThree =
                        view.findViewById<ImageView>(R.id.ivSlideThreeCoinsThree)
                    val ivSlideThreeCoinsD1 = view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD1)
                    val ivSlideThreeCoinsD2 = view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD2)
                    val ivSlideThreeCoinsD3 = view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD3)
                    val ivSlideThreeCoinsD4 = view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD4)
                    val ivSlideThreeCoinsD5 = view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD5)
                    val ivSlideThreeCoinsD6 = view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD6)
                    val ivSlideThreeCoinsD7 = view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD7)
                    val ivSlideThreeCoinsD8 = view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD8)
                    val ivSlideThreeCoinsD9 = view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD9)
                    val ivSlideThreeCoinsD10 =
                        view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD10)
                    val ivSlideThreeCoinsD11 =
                        view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD11)
                    val ivSlideThreeCoinsD12 =
                        view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD12)
                    val ivSlideThreeCoinsD13 =
                        view.findViewById<ImageView>(R.id.ivSlideThreeCoinsD13)
                    val tvSlideThreeDescriptionOne = view.findViewById<TextView>(R.id.tvTitleOne)
                    val tvSlideThreeDescriptionTwo = view.findViewById<TextView>(R.id.tvTitleTwo)
                    slideThreeAnimation(
                        ivSlideThreeCoinsOne,
                        ivSlideThreeCoinsTwo,
                        ivSlideThreeCoinsThree,
                        ivSlideThreeCoinsD1,
                        ivSlideThreeCoinsD2,
                        ivSlideThreeCoinsD3,
                        ivSlideThreeCoinsD4,
                        ivSlideThreeCoinsD5,
                        ivSlideThreeCoinsD6,
                        ivSlideThreeCoinsD7,
                        ivSlideThreeCoinsD8,
                        ivSlideThreeCoinsD9,
                        ivSlideThreeCoinsD10,
                        ivSlideThreeCoinsD11,
                        ivSlideThreeCoinsD12,
                        ivSlideThreeCoinsD13,
                        tvSlideThreeDescriptionOne,
                        tvSlideThreeDescriptionTwo
                    )
                }
            } else if (position == 4) {
                itemView =
                    inflater.inflate(R.layout.layout_on_boarding_screen_four, container, false)
                val view = itemView
                view!!.visibility = View.INVISIBLE
                view.post {
                    view.visibility = View.VISIBLE
                    val ivSlideFourGirl = view.findViewById<ImageView>(R.id.ivSlideFourGirl)
                    val ivSlideFourClock = view.findViewById<ImageView>(R.id.ivSlideFourClock)
                    val ivSlideFourHour = view.findViewById<ImageView>(R.id.ivSlideFourHour)
                    val ivSlideFourMinute = view.findViewById<ImageView>(R.id.ivSlideFourMinute)
                    val tvTitleSlideFourOne = view.findViewById<TextView>(R.id.tvTitleSlideFourOne)
                    val tvTitleSlideFourTwo = view.findViewById<TextView>(R.id.tvTitleSlideFourTwo)
                    val ivSlideFourOn247 = view.findViewById<ImageView>(R.id.ivSlideFourOn247)
                    val ivSlideFourOff247 = view.findViewById<ImageView>(R.id.ivSlideFourOff247)
                    slideFourAnimation(
                        ivSlideFourGirl,
                        ivSlideFourClock,
                        ivSlideFourHour,
                        ivSlideFourMinute,
                        tvTitleSlideFourOne,
                        tvTitleSlideFourTwo,
                        ivSlideFourOn247,
                        ivSlideFourOff247
                    )
                }
            } else if (position == 3) {
                itemView =
                    inflater.inflate(R.layout.layout_on_boarding_screen_five, container, false)
                val view = itemView
                view!!.visibility = View.INVISIBLE
                view.post {
                    view.visibility = View.VISIBLE
                    val ivSlideFiveGirl = view.findViewById<ImageView>(R.id.ivSlideFiveGirl)
                    val ivSlideFiveMobile = view.findViewById<ImageView>(R.id.ivSlideFiveMobile)
                    val tvSlideFiveDescriptionOne =
                        view.findViewById<TextView>(R.id.tvTitleSlideFiveOne)
                    val tvSlideFiveDescriptionTwo =
                        view.findViewById<TextView>(R.id.tvTitleSlideFiveTwo)
                    slideFiveAnimation(
                        ivSlideFiveGirl,
                        ivSlideFiveMobile,
                        tvSlideFiveDescriptionOne,
                        tvSlideFiveDescriptionTwo
                    )
                }
            }
            container.addView(itemView)
            return itemView!!
        }

        private fun slideOneAnimation(
            ivSlide1Girl: ImageView, ivSlide1Couch: ImageView,
            ivSlide1Phone: ImageView, ivSlide1Circle1: ImageView,
            ivSlide1Circle2: ImageView, ivSlide1Circle3: ImageView,
            ivSlide1CirclesCenter: ImageView, ivSlide1PhoneMoney: ImageView,
            ivSlide1PhoneSplash: ImageView, ivSlide1Icon1: ImageView,
            ivSlide1Icon2: ImageView, ivSlide1Icon3: ImageView,
            ivSlide1Icon4: ImageView, ivSlide1Icon5: ImageView,
            ivSlide1Icon6: ImageView, tvSlideOneDescriptionOne: TextView,
            tvSlideOneDescriptionTwo: TextView
        ) {
            ivSlide1Girl.clearAnimation()
            ivSlide1Couch.clearAnimation()
            ivSlide1Phone.clearAnimation()
            ivSlide1Circle1.clearAnimation()
            ivSlide1Circle2.clearAnimation()
            ivSlide1Circle3.clearAnimation()
            ivSlide1CirclesCenter.clearAnimation()
            ivSlide1Icon1.clearAnimation()
            ivSlide1Icon2.clearAnimation()
            ivSlide1Icon3.clearAnimation()
            ivSlide1Icon4.clearAnimation()
            ivSlide1Icon5.clearAnimation()
            ivSlide1Icon6.clearAnimation()
            ivSlide1PhoneMoney.clearAnimation()
            tvSlideOneDescriptionOne.clearAnimation()
            tvSlideOneDescriptionTwo.clearAnimation()
            ivSlide1Circle1.visibility = View.INVISIBLE
            ivSlide1Circle2.visibility = View.INVISIBLE
            ivSlide1Circle3.visibility = View.INVISIBLE
            ivSlide1CirclesCenter.visibility = View.INVISIBLE
            ivSlide1Icon1.visibility = View.INVISIBLE
            ivSlide1Icon2.visibility = View.INVISIBLE
            ivSlide1Icon3.visibility = View.INVISIBLE
            ivSlide1Icon4.visibility = View.INVISIBLE
            ivSlide1Icon5.visibility = View.INVISIBLE
            ivSlide1Icon6.visibility = View.INVISIBLE
            tvSlideOneDescriptionOne.visibility = View.INVISIBLE
            tvSlideOneDescriptionTwo.visibility = View.INVISIBLE
            ivSlide1Phone.clearAnimation()
            ivSlide1Phone.visibility = View.INVISIBLE
            ivSlide1PhoneMoney.visibility = View.INVISIBLE
            ivSlide1PhoneSplash.visibility = View.INVISIBLE
            val animate1 = TranslateAnimation(0f, 0f,
                (-2 * ivSlide1Girl.height).toFloat(), 0.15f * ivSlide1Girl.height)
            animate1.duration = 1000
            animate1.fillAfter = true
            animate1.interpolator = LinearInterpolator()
            //                animate1.setInterpolator(new BounceInterpolator());
            val animate11 = TranslateAnimation(0f, 0f, 0.15f * ivSlide1Girl.height,
                -0.1f * ivSlide1Girl.height)
            animate11.duration = 200
            animate11.startOffset = 0
            animate11.fillAfter = true
            val animate12 = TranslateAnimation(0f, 0f, -0.1f * ivSlide1Girl.height,
                0f)
            animate12.duration = 200
            animate12.startOffset = 0
            animate12.fillAfter = true
            animate11.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    ivSlide1Girl.startAnimation(animate12)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            animate1.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    ivSlide1Girl.startAnimation(animate11)
                    val animation4 = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    animation4.duration = 500
                    animation4.fillAfter = true
                    animation4.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            ivSlide1Icon2.visibility = View.VISIBLE
                            val animation10 = CustomRotationAnimation(ivSlide1Icon2,
                                ivSlide1Circle3.x + ivSlide1Circle3.width / 2,
                                ivSlide1Circle3.y + ivSlide1Circle3.height / 2, 0F)
                            animation10.duration = 20000
                            animation10.fillAfter = true
                            animation10.repeatCount = Animation.INFINITE
                            animation10.interpolator = LinearInterpolator()
                            animation10.startOffset = 0
                            ivSlide1Icon2.startAnimation(animation10)
                            ivSlide1Icon6.visibility = View.VISIBLE
                            val animation15 = CustomRotationAnimation(
                                ivSlide1Icon6,
                                ivSlide1Circle3.x + ivSlide1Circle3.width / 2,
                                ivSlide1Circle3.y + ivSlide1Circle3.height / 2,
                                250F
                            )
                            animation15.duration = 20000
                            animation15.fillAfter = true
                            animation15.repeatCount = Animation.INFINITE
                            animation15.interpolator = LinearInterpolator()
                            animation15.startOffset = 0
                            ivSlide1Icon6.startAnimation(animation15)
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    val animation5 = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    animation5.duration = 1000
                    animation5.fillAfter = true
                    animation5.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            ivSlide1Icon3.visibility = View.VISIBLE
                            val animation9 = CustomRotationAnimation(
                                ivSlide1Icon3,
                                ivSlide1Circle2.x + ivSlide1Circle2.width / 2,
                                ivSlide1Circle2.y + ivSlide1Circle2.height / 2,
                                180F
                            )
                            animation9.duration = 15000
                            animation9.fillAfter = true
                            animation9.repeatCount = Animation.INFINITE
                            animation9.interpolator = LinearInterpolator()
                            animation9.startOffset = 0
                            ivSlide1Icon3.startAnimation(animation9)
                            ivSlide1Icon4.visibility = View.VISIBLE
                            val animation13 = CustomRotationAnimation(
                                ivSlide1Icon4,
                                ivSlide1Circle2.x + ivSlide1Circle2.width / 2,
                                ivSlide1Circle2.y + ivSlide1Circle2.height / 2,
                                310F
                            )
                            animation13.duration = 15000
                            animation13.fillAfter = true
                            animation13.repeatCount = Animation.INFINITE
                            animation13.interpolator = LinearInterpolator()
                            animation13.startOffset = 0
                            ivSlide1Icon4.startAnimation(animation13)
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    val animation6 = ScaleAnimation(0f, 1f, 0f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f)
                    animation6.duration = 1500
                    animation6.fillAfter = true
                    animation6.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            ivSlide1Circle3.clearAnimation()
                            ivSlide1Icon1.visibility = View.VISIBLE
                            val animation8 = CustomRotationAnimation(
                                ivSlide1Icon1,
                                ivSlide1Circle3.x + ivSlide1Circle3.width / 2,
                                ivSlide1Circle3.y + ivSlide1Circle3.height / 2,
                                180F
                            )
                            animation8.duration = 25000
                            animation8.fillAfter = true
                            animation8.repeatCount = Animation.INFINITE
                            animation8.interpolator = LinearInterpolator()
                            animation8.startOffset = 0
                            ivSlide1Icon1.startAnimation(animation8)
                            ivSlide1Icon5.visibility = View.VISIBLE
                            val animation14 = CustomRotationAnimation(
                                ivSlide1Icon5,
                                ivSlide1Circle3.x + ivSlide1Circle3.width / 2,
                                ivSlide1Circle3.y + ivSlide1Circle3.height / 2,
                                28F
                            )
                            animation14.duration = 25000
                            animation14.fillAfter = true
                            animation14.repeatCount = Animation.INFINITE
                            animation14.interpolator = LinearInterpolator()
                            animation14.startOffset = 0
                            ivSlide1Icon5.startAnimation(animation14)
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    val animation7 = ScaleAnimation(0f, 1f, 0f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f)
                    animation7.duration = 1000
                    animation7.fillAfter = true
                    val animation3 =
                        TranslateAnimation(0F, 0F, (-2 * ivSlide1Phone.height).toFloat(), 0f)
                    //                        o1orbits.setVisibility(View.VISIBLE);
                    ivSlide1Circle1.visibility = View.VISIBLE
                    ivSlide1Circle2.visibility = View.VISIBLE
                    ivSlide1Circle3.visibility = View.VISIBLE
                    ivSlide1CirclesCenter.visibility = View.VISIBLE
                    ivSlide1Circle1.startAnimation(animation4)
                    ivSlide1Circle2.startAnimation(animation5)
                    ivSlide1Circle3.startAnimation(animation6)
                    ivSlide1CirclesCenter.startAnimation(animation7)
                    //                        o1orbits.startAnimation(animation4);
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            ivSlide1Girl.startAnimation(animate1)
            ivSlide1Phone.visibility = View.VISIBLE
            val animation3 = TranslateAnimation(0F, 0F, (-2 * ivSlide1Phone.height).toFloat(), 0f)
            animation3.duration = 1500
            animation3.fillAfter = true
            animation3.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    ivSlide1PhoneMoney.visibility = View.VISIBLE
                    ivSlide1PhoneSplash.visibility = View.VISIBLE
                    val animation16 = ScaleAnimation(
                        1f,
                        1.5f,
                        1f,
                        1.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    animation16.duration = 250
                    animation16.fillAfter = true
                    animation16.interpolator = LinearInterpolator()
                    val animation17 = ScaleAnimation(
                        1.5f,
                        1f,
                        1.5f,
                        1f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    animation17.duration = 250
                    animation17.fillAfter = true
                    animation17.startOffset = 0
                    animation16.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            //o1phoneMoney.clearAnimation();
                            ivSlide1PhoneMoney.startAnimation(animation17)
                            tvSlideOneDescriptionOne.visibility = View.VISIBLE
                            tvSlideOneDescriptionTwo.visibility = View.VISIBLE
                            val animation18 = TranslateAnimation(
                                (-2 * tvSlideOneDescriptionOne.width).toFloat(), 0f,
                                0f, 0f
                            )
                            animation18.duration = 500
                            animation18.fillAfter = true
                            val animation19 = TranslateAnimation(
                                (2 * tvSlideOneDescriptionTwo.width).toFloat(), 0f,
                                0f, 0f
                            )
                            animation19.duration = 1000
                            animation19.fillAfter = true
                            tvSlideOneDescriptionOne.startAnimation(animation18)
                            tvSlideOneDescriptionTwo.startAnimation(animation19)
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    ivSlide1PhoneMoney.startAnimation(animation16)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            ivSlide1Phone.startAnimation(animation3)
            val animate2 = TranslateAnimation((-2 * ivSlide1Couch.width).toFloat(), 0f, 0f, 0f)
            animate2.duration = 750
            animate2.fillAfter = true
            ivSlide1Couch.startAnimation(animate2)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

     }

    private fun slideFourAnimation(
        ivSlideFourGirl: ImageView, ivSlideFourClock: ImageView,
        ivSlideFourHour: ImageView, ivSlideFourMinute: ImageView,
        tvTitleSlideFourOne: TextView, tvTitleSlideFourTwo: TextView,
        ivSlideFourOn247: ImageView, ivSlideFourOff247: ImageView
    ) {
        ivSlideFourMinute.clearAnimation()
        ivSlideFourHour.clearAnimation()
        ivSlideFourOn247.clearAnimation()
        ivSlideFourOff247.clearAnimation()
        //remove call backs so that post delayed doesnt get executed
        ivSlideFourOn247.removeCallbacks(offRunnable)
        ivSlideFourOff247.removeCallbacks(onRunnable)
        ivSlideFourClock.clearAnimation()
        ivSlideFourGirl.visibility = View.INVISIBLE
        ivSlideFourClock.visibility = View.INVISIBLE
        ivSlideFourHour.visibility = View.INVISIBLE
        ivSlideFourMinute.visibility = View.INVISIBLE
        tvTitleSlideFourOne.visibility = View.INVISIBLE
        tvTitleSlideFourTwo.visibility = View.INVISIBLE
        ivSlideFourOn247.visibility = View.INVISIBLE
        ivSlideFourOff247.visibility = View.INVISIBLE
        val animationSet1 = AnimationSet(true)
        animationSet1.fillAfter = true
        animationSet1.duration = 300
        animationSet1.interpolator = LinearInterpolator()
        val animation6 = TranslateAnimation(
            ivSlideFourGirl.width.toFloat(),
            ivSlideFourGirl.width.toFloat(),
            0f,
            0f
        )
        val animation7 = ScaleAnimation(
            3f, 3f, 3f, 3f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animationSet1.addAnimation(animation7)
        animationSet1.addAnimation(animation6)
        animationSet1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                val animationSet = AnimationSet(true)
                animationSet.fillAfter = true
                animationSet.duration = 700
                animationSet.interpolator = LinearInterpolator()
                val animation1 = TranslateAnimation(
                    ivSlideFourGirl.width.toFloat(),
                    0f,
                    0f,
                    0f
                )
                val animation4 = ScaleAnimation(
                    3f, 1f, 3f, 1f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                animationSet.addAnimation(animation4)
                animationSet.addAnimation(animation1)
                ivSlideFourClock.visibility = View.VISIBLE
                val animation5 = TranslateAnimation(
                    1.2f * ivSlideFourClock.width,
                    0f,
                    0f,
                    0f
                )
                animation5.fillAfter = true
                animation5.duration = 1000
                animation5.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        ivSlideFourHour.visibility = View.VISIBLE
                        ivSlideFourMinute.visibility = View.VISIBLE
                        tvTitleSlideFourOne.visibility = View.VISIBLE
                        tvTitleSlideFourTwo.visibility = View.VISIBLE
                        val animation18 = TranslateAnimation(
                            (-2 * tvTitleSlideFourOne.width).toFloat(), 0f,
                            0f, 0f
                        )
                        animation18.duration = 500
                        animation18.fillAfter = true
                        val animation19 = TranslateAnimation(
                            (2 * tvTitleSlideFourTwo.width).toFloat(), 0f,
                            0f, 0f
                        )
                        animation19.duration = 1000
                        animation19.fillAfter = true
                        tvTitleSlideFourOne.startAnimation(animation18)
                        tvTitleSlideFourTwo.startAnimation(animation19)
                        val animation3 = RotateAnimation(
                            0f,
                            360f,
                            Animation.RELATIVE_TO_SELF,
                            0.05f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f
                        )
                        animation3.duration = 6000
                        animation3.fillAfter = true
                        animation3.repeatCount = Animation.INFINITE
                        animation3.interpolator = LinearInterpolator()
                        animation3.startOffset = 0
                        val animation2 = RotateAnimation(
                            0f,
                            360f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.9f
                        )
                        animation2.duration = 30000
                        animation2.fillAfter = true
                        animation2.repeatCount = Animation.INFINITE
                        animation2.interpolator = LinearInterpolator()
                        animation2.startOffset = 0
                        ivSlideFourHour.startAnimation(animation2)
                        ivSlideFourMinute.startAnimation(animation3)
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
                ivSlideFourClock.startAnimation(animation5)
                val animation6 = TranslateAnimation(
                    (2 * ivSlideFourClock.width).toFloat(),
                    0f,
                    0f,
                    0f
                )
                animation6.fillAfter = true
                animation6.duration = 1000
                animation6.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        ivSlideFourOn247.clearAnimation()
                        ivSlideFourOn247.visibility = View.INVISIBLE
                        ivSlideFourOff247.visibility = View.INVISIBLE
                        onRunnable = Runnable {
                            ivSlideFourOn247.visibility = View.VISIBLE
                            ivSlideFourOff247.visibility = View.INVISIBLE
                            offRunnable = Runnable {
                                ivSlideFourOn247.visibility = View.INVISIBLE
                                ivSlideFourOff247.visibility = View.VISIBLE
                                ivSlideFourOff247.postDelayed(onRunnable, 350)
                            }
                            ivSlideFourOn247.postDelayed(offRunnable, 700)
                        }
                        onRunnable.run()
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
                ivSlideFourOn247.visibility = View.VISIBLE
                ivSlideFourOn247.startAnimation(animation6)
                ivSlideFourGirl.visibility = View.VISIBLE
                ivSlideFourGirl.startAnimation(animationSet)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        ivSlideFourGirl.visibility = View.VISIBLE
        ivSlideFourGirl.postDelayed({
            ivSlideFourOn247.visibility = View.INVISIBLE
            ivSlideFourOff247.visibility = View.INVISIBLE
        }, 100)
        ivSlideFourGirl.animation = animationSet1
    }

    private fun slideThreeAnimation(
        ivSlideThreeCoinsOne: ImageView, ivSlideThreeCoinsTwo: ImageView,
        ivSlideThreeCoinsThree: ImageView, ivSlideThreeCoinsD1: ImageView,
        ivSlideThreeCoinsD2: ImageView, ivSlideThreeCoinsD3: ImageView,
        ivSlideThreeCoinsD4: ImageView, ivSlideThreeCoinsD5: ImageView,
        ivSlideThreeCoinsD6: ImageView, ivSlideThreeCoinsD7: ImageView,
        ivSlideThreeCoinsD8: ImageView, ivSlideThreeCoinsD9: ImageView,
        ivSlideThreeCoinsD10: ImageView, ivSlideThreeCoinsD11: ImageView,
        ivSlideThreeCoinsD12: ImageView, ivSlideThreeCoinsD13: ImageView,
        tvSlideThreeDescriptionOne: TextView,
        tvSlideThreeDescriptionTwo: TextView
    ) {
        ivSlideThreeCoinsOne.clearAnimation()
        ivSlideThreeCoinsTwo.clearAnimation()
        ivSlideThreeCoinsThree.clearAnimation()
        ivSlideThreeCoinsD1.clearAnimation()
        ivSlideThreeCoinsD2.clearAnimation()
        ivSlideThreeCoinsD3.clearAnimation()
        ivSlideThreeCoinsD4.clearAnimation()
        ivSlideThreeCoinsD5.clearAnimation()
        ivSlideThreeCoinsD6.clearAnimation()
        ivSlideThreeCoinsD7.clearAnimation()
        ivSlideThreeCoinsD8.clearAnimation()
        ivSlideThreeCoinsD9.clearAnimation()
        ivSlideThreeCoinsD10.clearAnimation()
        ivSlideThreeCoinsD11.clearAnimation()
        ivSlideThreeCoinsD12.clearAnimation()
        ivSlideThreeCoinsD13.clearAnimation()
        tvSlideThreeDescriptionOne.clearAnimation()
        tvSlideThreeDescriptionTwo.clearAnimation()
        ivSlideThreeCoinsOne.visibility = View.INVISIBLE
        ivSlideThreeCoinsTwo.visibility = View.INVISIBLE
        ivSlideThreeCoinsThree.visibility = View.INVISIBLE
        ivSlideThreeCoinsD1.visibility = View.INVISIBLE
        ivSlideThreeCoinsD2.visibility = View.INVISIBLE
        ivSlideThreeCoinsD3.visibility = View.INVISIBLE
        ivSlideThreeCoinsD4.visibility = View.INVISIBLE
        ivSlideThreeCoinsD5.visibility = View.INVISIBLE
        ivSlideThreeCoinsD6.visibility = View.INVISIBLE
        ivSlideThreeCoinsD7.visibility = View.INVISIBLE
        ivSlideThreeCoinsD8.visibility = View.INVISIBLE
        ivSlideThreeCoinsD9.visibility = View.INVISIBLE
        ivSlideThreeCoinsD10.visibility = View.INVISIBLE
        ivSlideThreeCoinsD11.visibility = View.INVISIBLE
        ivSlideThreeCoinsD12.visibility = View.INVISIBLE
        ivSlideThreeCoinsD13.visibility = View.INVISIBLE
        tvSlideThreeDescriptionOne.visibility = View.INVISIBLE
        tvSlideThreeDescriptionTwo.visibility = View.INVISIBLE
        ivSlideThreeCoinsOne.postDelayed({
            val animation1 = TranslateAnimation(
                0f,
                0f,
                (-3 * ivSlideThreeCoinsOne.height).toFloat(),
                0f
            )
            animation1.duration = 1300
            animation1.fillAfter = true
            animation1.interpolator = DecelerateInterpolator()
            animation1.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    tvSlideThreeDescriptionOne.visibility = View.VISIBLE
                    tvSlideThreeDescriptionTwo.visibility = View.VISIBLE
                    val animation18 = TranslateAnimation(
                        (-2 * tvSlideThreeDescriptionOne.width).toFloat(), 0f,
                        0f, 0f
                    )
                    animation18.duration = 500
                    animation18.fillAfter = true
                    val animation19 = TranslateAnimation(
                        (2 * tvSlideThreeDescriptionTwo.width).toFloat(), 0f,
                        0f, 0f
                    )
                    animation19.duration = 1000
                    animation19.fillAfter = true
                    tvSlideThreeDescriptionOne.startAnimation(animation18)
                    tvSlideThreeDescriptionTwo.startAnimation(animation19)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            ivSlideThreeCoinsOne.visibility = View.VISIBLE
            ivSlideThreeCoinsOne.startAnimation(animation1)
            val animation2 = TranslateAnimation(
                0f,
                0f,
                (-3 * ivSlideThreeCoinsTwo.height).toFloat(),
                0f
            )
            animation2.duration = 1200
            animation2.fillAfter = true
            animation2.interpolator = DecelerateInterpolator()
            ivSlideThreeCoinsTwo.visibility = View.VISIBLE
            ivSlideThreeCoinsTwo.startAnimation(animation2)
            val animation3 = TranslateAnimation(
                0f,
                0f,
                (-3 * ivSlideThreeCoinsThree.height).toFloat(),
                0f
            )
            animation3.duration = 900
            animation3.interpolator = DecelerateInterpolator()
            animation3.fillAfter = true
            ivSlideThreeCoinsThree.visibility = View.VISIBLE
            ivSlideThreeCoinsThree.startAnimation(animation3)
        }, 600)
        val animation9 = TranslateAnimation(
            0f,
            0f,
            (-3 * ivSlideThreeCoinsOne.height).toFloat(),
            (3 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet3 = AnimationSet(false)
        val alphaAnimation3 = AlphaAnimation(1f, 0f)
        alphaAnimation3.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation9.interpolator = LinearInterpolator()
        animationSet3.addAnimation(animation9)
        animationSet3.addAnimation(alphaAnimation3)
        animationSet3.fillAfter = true
        animationSet3.duration = 2000
        ivSlideThreeCoinsD1.visibility = View.VISIBLE
        ivSlideThreeCoinsD1.startAnimation(animationSet3)
        val animation10 = TranslateAnimation(
            0f,
            0f,
            (-3 * ivSlideThreeCoinsOne.height).toFloat(),
            (3 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet4 = AnimationSet(false)
        val alphaAnimation4 = AlphaAnimation(1f, 0f)
        alphaAnimation4.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation10.interpolator = LinearInterpolator()
        animationSet4.addAnimation(animation10)
        animationSet4.addAnimation(alphaAnimation4)
        animationSet4.fillAfter = true
        animationSet4.duration = 1000
        ivSlideThreeCoinsD8.visibility = View.VISIBLE
        ivSlideThreeCoinsD8.startAnimation(animationSet4)
        val animation11 = TranslateAnimation(
            0f,
            0f,
            (-3 * ivSlideThreeCoinsOne.height).toFloat(),
            (3 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet5 = AnimationSet(false)
        val alphaAnimation5 = AlphaAnimation(1f, 0f)
        alphaAnimation5.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation11.interpolator = LinearInterpolator()
        animationSet5.addAnimation(animation11)
        animationSet5.addAnimation(alphaAnimation5)
        animationSet5.fillAfter = true
        animationSet5.duration = 1500
        ivSlideThreeCoinsD12.visibility = View.VISIBLE
        ivSlideThreeCoinsD12.startAnimation(animationSet5)
        val animation8 = TranslateAnimation(
            0f,
            0f,
            (-3 * ivSlideThreeCoinsTwo.height).toFloat(),
            (3 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet2 = AnimationSet(false)
        val alphaAnimation2 = AlphaAnimation(1f, 0f)
        alphaAnimation2.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation8.interpolator = LinearInterpolator()
        animationSet2.addAnimation(animation8)
        animationSet2.addAnimation(alphaAnimation2)
        animationSet2.fillAfter = true
        animationSet2.duration = 1200
        ivSlideThreeCoinsD4.visibility = View.VISIBLE
        ivSlideThreeCoinsD4.startAnimation(animationSet2)
        val animation13 = TranslateAnimation(
            0f,
            0f,
            (-3 * ivSlideThreeCoinsTwo.height).toFloat(),
            (3 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet7 = AnimationSet(false)
        val alphaAnimation7 = AlphaAnimation(1f, 0f)
        alphaAnimation4.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation13.interpolator = LinearInterpolator()
        animationSet7.addAnimation(animation13)
        animationSet7.addAnimation(alphaAnimation4)
        animationSet7.fillAfter = true
        animationSet7.duration = 1000
        ivSlideThreeCoinsD5.visibility = View.VISIBLE
        ivSlideThreeCoinsD5.startAnimation(animationSet7)
        val animation14 = TranslateAnimation(
            0f,
            0f,
            (-1 * ivSlideThreeCoinsTwo.height).toFloat(),
            (4 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet8 = AnimationSet(false)
        val alphaAnimation8 = AlphaAnimation(1f, 0f)
        alphaAnimation4.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation14.interpolator = LinearInterpolator()
        animationSet8.addAnimation(animation13)
        animationSet8.addAnimation(alphaAnimation8)
        animationSet8.fillAfter = true
        animationSet8.duration = 2000
        ivSlideThreeCoinsD9.visibility = View.VISIBLE
        ivSlideThreeCoinsD9.startAnimation(animationSet5)
        val animation12 = TranslateAnimation(
            0f,
            0f,
            (-1 * ivSlideThreeCoinsTwo.height).toFloat(),
            (4 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet6 = AnimationSet(false)
        val alphaAnimation6 = AlphaAnimation(1f, 0f)
        alphaAnimation6.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation12.interpolator = LinearInterpolator()
        animationSet6.addAnimation(animation12)
        animationSet6.addAnimation(alphaAnimation6)
        animationSet6.fillAfter = true
        animationSet6.duration = 1000
        ivSlideThreeCoinsD13.visibility = View.VISIBLE
        ivSlideThreeCoinsD13.startAnimation(animationSet6)
        val animation15 = TranslateAnimation(
            0f,
            0f,
            (-16 * ivSlideThreeCoinsD6.height).toFloat(),
            0f
        )
        val animationSet9 = AnimationSet(false)
        val alphaAnimation9 = AlphaAnimation(1f, 0f)
        alphaAnimation9.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation15.interpolator = LinearInterpolator()
        animationSet9.addAnimation(animation15)
        animationSet9.addAnimation(alphaAnimation9)
        animationSet9.fillAfter = true
        animationSet9.duration = 1000
        ivSlideThreeCoinsD6.visibility = View.VISIBLE
        ivSlideThreeCoinsD6.startAnimation(animationSet9)
        val animation16 = TranslateAnimation(
            0f,
            0f,
            (-3 * ivSlideThreeCoinsThree.height).toFloat(),
            (3 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet10 = AnimationSet(false)
        val alphaAnimation10 = AlphaAnimation(1f, 0f)
        alphaAnimation10.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation16.interpolator = LinearInterpolator()
        animationSet10.addAnimation(animation16)
        animationSet10.addAnimation(alphaAnimation10)
        animationSet10.fillAfter = true
        animationSet10.duration = 1500
        ivSlideThreeCoinsD3.visibility = View.VISIBLE
        ivSlideThreeCoinsD3.startAnimation(animationSet10)
        val animation17 = TranslateAnimation(
            0f,
            0f,
            (-3 * ivSlideThreeCoinsThree.height).toFloat(),
            (3 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet11 = AnimationSet(false)
        val alphaAnimation11 = AlphaAnimation(1f, 0f)
        alphaAnimation11.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation17.interpolator = LinearInterpolator()
        animationSet11.addAnimation(animation17)
        animationSet11.addAnimation(alphaAnimation11)
        animationSet11.fillAfter = true
        animationSet11.duration = 1500
        ivSlideThreeCoinsD2.visibility = View.VISIBLE
        ivSlideThreeCoinsD2.startAnimation(animationSet11)
        val animation18 = TranslateAnimation(
            0f,
            0f,
            (-1 * ivSlideThreeCoinsThree.height).toFloat(),
            (4 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet12 = AnimationSet(false)
        val alphaAnimation12 = AlphaAnimation(1f, 0f)
        alphaAnimation12.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation18.interpolator = LinearInterpolator()
        animationSet12.addAnimation(animation18)
        animationSet12.addAnimation(alphaAnimation12)
        animationSet12.fillAfter = true
        animationSet12.duration = 2000
        ivSlideThreeCoinsD10.visibility = View.VISIBLE
        ivSlideThreeCoinsD10.startAnimation(animationSet12)
        val animation19 = TranslateAnimation(
            0f,
            0f,
            (-1 * ivSlideThreeCoinsThree.height).toFloat(),
            (4 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet13 = AnimationSet(false)
        val alphaAnimation13 = AlphaAnimation(1f, 0f)
        alphaAnimation13.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation19.interpolator = LinearInterpolator()
        animationSet13.addAnimation(animation19)
        animationSet13.addAnimation(alphaAnimation13)
        animationSet13.fillAfter = true
        animationSet13.duration = 2000
        ivSlideThreeCoinsD11.visibility = View.VISIBLE
        ivSlideThreeCoinsD11.startAnimation(animationSet13)
        val animation20 = TranslateAnimation(
            0f,
            0f,
            (-1 * ivSlideThreeCoinsThree.height).toFloat(),
            (4 * ivSlideThreeCoinsOne.height).toFloat()
        )
        val animationSet14 = AnimationSet(false)
        val alphaAnimation14 = AlphaAnimation(1f, 0f)
        alphaAnimation14.interpolator = Interpolator { v ->
            if (v < 0.5f) {
                0f
            } else 2 * v - 1
        }
        animation20.interpolator = LinearInterpolator()
        animationSet14.addAnimation(animation20)
        animationSet14.addAnimation(alphaAnimation14)
        animationSet14.fillAfter = true
        animationSet14.duration = 1500
        ivSlideThreeCoinsD7.visibility = View.VISIBLE
        ivSlideThreeCoinsD7.startAnimation(animationSet14)
    }

    private fun slideTwoAnimation(ivSlideTwoGirl: ImageView, ivSlideTwoGirlLeft: ImageView,
        ivSlideTwoBoyRight: ImageView, ivSlideTwoCrown: ImageView, tvTittle: TextView) {
        ivSlideTwoGirl.clearAnimation()
        ivSlideTwoGirlLeft.clearAnimation()
        ivSlideTwoBoyRight.clearAnimation()
        ivSlideTwoCrown.clearAnimation()
        tvTittle.clearAnimation()
        ivSlideTwoGirl.visibility = View.INVISIBLE
        ivSlideTwoGirlLeft.visibility = View.INVISIBLE
        ivSlideTwoBoyRight.visibility = View.INVISIBLE
        ivSlideTwoCrown.visibility = View.INVISIBLE
        tvTittle.visibility = View.INVISIBLE
        ivSlideTwoGirlLeft.visibility = View.VISIBLE
        val animation3 = TranslateAnimation((-2 * ivSlideTwoGirlLeft.width).toFloat(), 0f, -0f, 0f)
        animation3.duration = 500
        animation3.fillAfter = true
        animation3.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                ivSlideTwoGirl.visibility = View.VISIBLE
                val animation1 = TranslateAnimation(0f, 0f, (-2 * ivSlideTwoGirl.height).toFloat(), 0f)
                animation1.duration = 700
                animation1.fillAfter = true
                ivSlideTwoGirl.startAnimation(animation1)
                ivSlideTwoCrown.visibility = View.VISIBLE
                val animation2 = TranslateAnimation(0f, 0f, (-2 * ivSlideTwoGirl.height).toFloat(), 0f)
                animation2.duration = 1000
                animation2.fillAfter = true
                animation2.interpolator = LinearInterpolator()
                animation2.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        tvTittle.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
                ivSlideTwoCrown.startAnimation(animation2)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        ivSlideTwoGirlLeft.startAnimation(animation3)
        ivSlideTwoBoyRight.visibility = View.VISIBLE
        val animation4 = TranslateAnimation((2 * ivSlideTwoBoyRight.width).toFloat(), 0f, -0f, 0f)
        animation4.duration = 500
        animation4.fillAfter = true
        ivSlideTwoBoyRight.startAnimation(animation4)
    }

    private fun slideFiveAnimation(ivSlideFiveGirl: ImageView, ivSlideFiveMobile: ImageView,
        tvSlideFiveDescriptionOne: TextView, tvSlideFiveDescriptionTwo: TextView) {
        tvSlideFiveDescriptionOne.clearAnimation()
        tvSlideFiveDescriptionTwo.clearAnimation()
        tvSlideFiveDescriptionOne.visibility = View.INVISIBLE
        tvSlideFiveDescriptionTwo.visibility = View.INVISIBLE
        val animation1 = TranslateAnimation(ivSlideFiveGirl.width.toFloat(), 0f,
            0f, 0f)
        animation1.duration = 1000
        animation1.fillAfter = true
        animation1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                tvSlideFiveDescriptionOne.visibility = View.VISIBLE
                tvSlideFiveDescriptionTwo.visibility = View.VISIBLE
                val animation18 = TranslateAnimation(
                    (-2 * tvSlideFiveDescriptionOne.width).toFloat(), 0f,
                    0f, 0f
                )
                animation18.duration = 500
                animation18.fillAfter = true
                val animation19 = TranslateAnimation(
                    (-2 * tvSlideFiveDescriptionTwo.width).toFloat(), 0f,
                    0f, 0f
                )
                animation19.duration = 500
                animation19.fillAfter = true
                tvSlideFiveDescriptionOne.startAnimation(animation18)
                tvSlideFiveDescriptionTwo.startAnimation(animation19)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        ivSlideFiveGirl.visibility = View.VISIBLE
        ivSlideFiveGirl.startAnimation(animation1)
        val animation2 = TranslateAnimation(
            ivSlideFiveGirl.width.toFloat(), 0f,
            0f, 0f
        )
        animation2.duration = 1000
        animation2.fillAfter = true
        ivSlideFiveMobile.visibility = View.VISIBLE
        ivSlideFiveMobile.startAnimation(animation1)
    }

    private var offRunnable = Runnable { }
    private var onRunnable = Runnable { }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        internetchecker.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        internetchecker.stop()
    }

    override fun onStop() {
        super.onStop()
        internetchecker.stop()
    }

    override fun onPause() {
        super.onPause()
        internetchecker.stop()
    }
}