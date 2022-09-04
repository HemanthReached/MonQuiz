package com.monquiz.interfaces

interface SelectedImageCallBack {
    fun selectedImage(imageUrl: String?, isFromGallery: Boolean, comingFrom: String?)
}