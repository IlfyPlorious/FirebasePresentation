package com.example.firebasedemo.util

import androidx.compose.ui.graphics.Color
import com.example.firebasedemo.R

enum class Brand(val id: Int, val color: Color, val website: String, val icon: Int) {
    Acura(2, Color(0xFFe08d3c), "https://www.acura.com", R.drawable.acura),
    AstonMartini(30, Color(0xFF0a7968), "https://www.astonmartin.com", R.drawable.astonmartin),
    Audi(1, Color(0xFFf50537), "https://www.audi.com", R.drawable.audi),
    Bentley(26, Color(0xFF619fb2), "https://www.bentleymotors.com", R.drawable.bentley),
    BMW(15, Color(0xFF280137), "https://www.bmw.com", R.drawable.bmw),
    Bugatti(38, Color(0xFFDDB63D), "https://www.bugatti.com", R.drawable.bugatii),
    Buick(9, Color(0xFFffffff), "https://www.buick.com", R.drawable.buick),
    Cadillac(28, Color(0xFFE38AAE), "https://www.cadillac.com", R.drawable.cadillac),
    Chevrolet(8, Color(0xFFD3DADF), "https://www.chevrolet.com", R.drawable.chevrolet),
    Chrysler(14, Color(0xFF25285a), "https://www.chrysler.com", R.drawable.chrysler),
    Daewoo(47, Color(0xFF476A50), "https://www.daihatsu.com", R.drawable.daewoo),
    Dodge(3, Color(0xFF972626), "https://www.dodge.com", R.drawable.dodge),
    EagleTalon(46, Color(0xFF000000), "https://autovit.ro/autoturisme/eagletalon", R.drawable.eagle),
    Ferrari(21, Color(0xFFF70D1A), "https://www.ferrari.com", R.drawable.ferrari),
    Fiat(40, Color(0xFF96172e), "https://www.fiat.com", R.drawable.fiat),
    Fisker(22, Color(0xFFD3B6AA), "https://www.fiskerinc.com", R.drawable.fisker),
    Ford(5, Color(0xFFc5ced4), "https://www.ford.com", R.drawable.ford),
    GMC(16, Color(0xFF003366), "https://www.gmc.com", R.drawable.gmc),
    Honda(31, Color(0xFFCC0000), "https://www.honda.com", R.drawable.honda),
    Hummer(29, Color(0xFFcecece), "https://autovit.ro/autoturisme/hummer", R.drawable.hummer),
    Hyundai(4, Color(0xFF005AAA), "https://www.hyundai.com", R.drawable.hyundai),
    Infiniti(23, Color(0xFF7F007F), "https://www.infiniti.com", R.drawable.infiniti),
    Jaguar(33, Color(0xFF006600), "https://www.jaguar.com", R.drawable.jaguar),
    Jeep(27, Color(0xFF194E84), "https://www.jeep.com", R.drawable.jeep),
    Kia(25, Color(0xFFCC0000), "https://www.kia.com", R.drawable.kia),
    Lamborghini(41, Color(0xFFFFD700), "https://www.lamborghini.com", R.drawable.lamborghini),
    Lincoln(43, Color(0xFF000000), "https://www.lincoln.com", R.drawable.lincoln),
    Maybach(44, Color(0xFF000000), "https://www.maybach.com", R.drawable.maybach),
    Mazda(45, Color(0xFF8E1B13), "https://www.mazda.ro", R.drawable.mazda),
    McLaren(37, Color(0xFFFF8700), "https://www.mclaren.com", R.drawable.mclaren),
    Mercedes(32, Color(0xFFA0A0A0), "https://www.mercedes-benz.com", R.drawable.mercedes),
    Mini(48, Color(0xFFA2A5A2), "https://www.mini.com", R.drawable.mini),
    Mitsubishi(7, Color(0xFFcecece), "https://www.mitsubishi-motors.com", R.drawable.mitsubishi),
    Nissan(17, Color(0xFFE37300), "https://www.nissan.ro", R.drawable.nissan),
    Porsche(25, Color(0xFFC0C0C0), "https://www.porsche.com", R.drawable.porsche),
    RangeRover(20, Color(0xFF726758), "https://www.landrover.com", R.drawable.landrover),
    RollsRoyce(24, Color(0xFF4A156B), "https://www.rolls-roycemotorcars.com", R.drawable.rollsroyce),
    Scion(35, Color(0xFF000000), "https://www.toyota.com", R.drawable.scion),
    Smart(39, Color(0xFFFFA500), "https://www.smart.com", R.drawable.smart),
    Spyker(19, Color(0xFFffffff), "https://www.spykercars.com", R.drawable.spyker),
    Suzuki(18, Color(0xFF1E272E), "https://www.suzuki.com", R.drawable.suzuki),
    Tesla(42, Color(0xFFCC0000), "https://www.tesla.com", R.drawable.tesla),
    Toyota(10, Color(0xFFF7F7F7), "https://www.toyota.com", R.drawable.toyota),
    Volkswagen(13, Color(0xFF007F00), "https://www.volkswagen.com", R.drawable.volkswagen),
    Volvo(11, Color(0xFFF2F4F3), "https://www.volvo.com", R.drawable.volvo),
    Geo(6, Color(0xFFFFFFFF), "https://en.wikipedia.org/wiki/Geo_(automobile)", R.drawable.geo_logo),
    Plymouth(12, Color(0xFFFFFFFF), "https://www.chrysler.com", R.drawable.plymouth_logo),
    Isuzu(34, Color(0xFFFFFFFF), "https://www.isuzu.com", R.drawable.isuzu_logo),
    Ram(36, Color(0xFFF7F7F7), "https://www.ram.com", R.drawable.ram)
}

sealed class GeminiQuery {
    abstract fun getQuery() : String
    class CarBrandInfo(val brand: Brand) : GeminiQuery() {
        override fun getQuery(): String {
            return "I am a car enthusiast and I want to learn more about ${brand.name}. Tell me in a paragraph about this brand's history, in another paragraph what's the most popular car model and in another paragraph if I should buy it."
        }
    }
}