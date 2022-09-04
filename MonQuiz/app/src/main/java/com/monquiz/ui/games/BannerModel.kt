package com.monquiz.ui.games

class BannerModel()  {

    var gametypeImage: Int = 0
    var gamename : String = ""
    var gameimage : Int = 0
    var gamedescription : String = ""

    constructor (gametypeImage: Int ,gamename : String , gameimage : Int , gamedescription : String) : this() {
        this.gametypeImage = gametypeImage
        this.gamename = gamename
        this.gameimage = gameimage
        this.gamedescription = gamedescription
    }

}