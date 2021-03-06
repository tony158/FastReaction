package com.tonigames.reaction.findpair

import com.tonigames.reaction.R
import java.util.*

const val WRONG_SELECTION_MSG = "Wrong selection!"

interface IImageFragment {

    /** Returns a random element    */
    fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null

    companion object {
        val allDrawables: List<Int> = listOf(
            R.drawable.acorn,
            R.drawable.aircraft,
            R.drawable.allterrain,
            R.drawable.amazon,
            R.drawable.ameracan_football,
            R.drawable.american_football,
            R.drawable.android,
            R.drawable.ant,
            R.drawable.apple,
            R.drawable.asian,
            R.drawable.audi,
            R.drawable.badminton,
            R.drawable.bakery,
            R.drawable.basketball,
            R.drawable.bats,
            R.drawable.battery,
            R.drawable.battle,
            R.drawable.beach,
            R.drawable.bear,
            R.drawable.bee,
            R.drawable.beer,
            R.drawable.bike,
            R.drawable.bikini,
            R.drawable.biscuit,
            R.drawable.bmw,
            R.drawable.boat,
            R.drawable.bomb,
            R.drawable.book,
            R.drawable.bow,
            R.drawable.bowling,
            R.drawable.bug,
            R.drawable.burger,
            R.drawable.butterfly,
            R.drawable.cab,
            R.drawable.cake,
            R.drawable.camera,
            R.drawable.candy,
            R.drawable.cap,
            R.drawable.car,
            R.drawable.carnival,
            R.drawable.cat,
            R.drawable.chair,
            R.drawable.charging_station,
            R.drawable.chicken,
            R.drawable.china,
            R.drawable.china_fan,
            R.drawable.china_flag,
            R.drawable.china_temple,
            R.drawable.chinese,
            R.drawable.chinese_sword,
            R.drawable.christmas,
            R.drawable.church,
            R.drawable.clothes,
            R.drawable.cock,
            R.drawable.coffee_cup,
            R.drawable.construction_worker,
            R.drawable.crab,
            R.drawable.crab_red,
            R.drawable.crab_two_leg,
            R.drawable.crown,
            R.drawable.cut,
            R.drawable.deer,
            R.drawable.dinosaur,
            R.drawable.dinosaur_egg,
            R.drawable.dive,
            R.drawable.doctor,
            R.drawable.dog,
            R.drawable.dog_food,
            R.drawable.donut,
            R.drawable.double_sword,
            R.drawable.dragon,
            R.drawable.drink,
            R.drawable.dumbbell,
            R.drawable.eagle,
            R.drawable.easter,
            R.drawable.eat,
            R.drawable.einstein,
            R.drawable.electricity_tower,
            R.drawable.electric_car,
            R.drawable.elephant,
            R.drawable.espresso,
            R.drawable.fan,
            R.drawable.fashion,
            R.drawable.fashion_glass,
            R.drawable.fight,
            R.drawable.fire_extinguisher,
            R.drawable.fire_truck,
            R.drawable.fire_water,
            R.drawable.fish,
            R.drawable.flower,
            R.drawable.food_and_bbq,
            R.drawable.food_cart,
            R.drawable.food_restaurant,
            R.drawable.footwear,
            R.drawable.forest,
            R.drawable.fox,
            R.drawable.france,
            R.drawable.front_view,
            R.drawable.fuel,
            R.drawable.furniture_household,
            R.drawable.gas,
            R.drawable.geisha,
            R.drawable.germany_flag,
            R.drawable.gift,
            R.drawable.glue,
            R.drawable.goal,
            R.drawable.gold,
            R.drawable.gold_box,
            R.drawable.grape,
            R.drawable.guitar,
            R.drawable.halloween,
            R.drawable.hammer,
            R.drawable.hat,
            R.drawable.heart,
            R.drawable.heavy_equipment,
            R.drawable.hedgehog,
            R.drawable.hermit_crab,
            R.drawable.hibiscus,
            R.drawable.hippo,
            R.drawable.horsebak_riding,
            R.drawable.hotel,
            R.drawable.hot_air_balloon,
            R.drawable.hot_dog,
            R.drawable.huawei,
            R.drawable.iron,
            R.drawable.jacket,
            R.drawable.jean,
            R.drawable.kayak,
            R.drawable.key_room,
            R.drawable.kitty,
            R.drawable.korea,
            R.drawable.lamp,
            R.drawable.lantern,
            R.drawable.law,
            R.drawable.lifestyle_girl,
            R.drawable.lifestyle_gitar,
            R.drawable.lifting_weight,
            R.drawable.lighter,
            R.drawable.lion,
            R.drawable.lollipop,
            R.drawable.luggage,
            R.drawable.luggage_case,
            R.drawable.macaron,
            R.drawable.macbook,
            R.drawable.magic,
            R.drawable.magician,
            R.drawable.magic_lamp,
            R.drawable.magic_wand,
            R.drawable.magnet,
            R.drawable.mail,
            R.drawable.meat,
            R.drawable.miscellaneous,
            R.drawable.miscellaneous_fire,
            R.drawable.money,
            R.drawable.monkey,
            R.drawable.moon_star,
            R.drawable.mouse_robot,
            R.drawable.music_and_multimedia,
            R.drawable.ninja,
            R.drawable.ninja_head,
            R.drawable.octopus,
            R.drawable.one_bat,
            R.drawable.owl,
            R.drawable.painting,
            R.drawable.palm_tree,
            R.drawable.paper,
            R.drawable.paris,
            R.drawable.party,
            R.drawable.passport,
            R.drawable.penguin,
            R.drawable.pepper,
            R.drawable.photography,
            R.drawable.piano,
            R.drawable.pig_red,
            R.drawable.professor,
            R.drawable.ps4,
            R.drawable.pumpkin,
            R.drawable.rabbit,
            R.drawable.racket,
            R.drawable.radiation,
            R.drawable.radio,
            R.drawable.radio_old,
            R.drawable.rainbow,
            R.drawable.ramen,
            R.drawable.rank,
            R.drawable.raphael,
            R.drawable.reading_glasses,
            R.drawable.real_estate,
            R.drawable.ring,
            R.drawable.river,
            R.drawable.rocket,
            R.drawable.rodent,
            R.drawable.run,
            R.drawable.samurai,
            R.drawable.santa_claus,
            R.drawable.sashimi,
            R.drawable.satellite,
            R.drawable.scooter,
            R.drawable.shark,
            R.drawable.sheep,
            R.drawable.shell,
            R.drawable.ship,
            R.drawable.skate_shoes,
            R.drawable.slipper,
            R.drawable.iphone_sound,
            R.drawable.smart_watch,
            R.drawable.smoke,
            R.drawable.snake,
            R.drawable.snow,
            R.drawable.snowboard,
            R.drawable.snowman,
            R.drawable.soccer,
            R.drawable.socks,
            R.drawable.speaker,
            R.drawable.spider,
            R.drawable.sports_and_competition,
            R.drawable.sports_fishing_competition,
            R.drawable.squirrel,
            R.drawable.stand,
            R.drawable.statue_liberty,
            R.drawable.student,
            R.drawable.submarine,
            R.drawable.suit,
            R.drawable.summer,
            R.drawable.summer_ice,
            R.drawable.summer_icecream,
            R.drawable.summer_sea,
            R.drawable.sunflower,
            R.drawable.sunhat,
            R.drawable.supermarket,
            R.drawable.surfing,
            R.drawable.sushi,
            R.drawable.swans,
            R.drawable.swimsuit,
            R.drawable.swiss_knife,
            R.drawable.sword,
            R.drawable.sword_battle,
            R.drawable.tank,
            R.drawable.telephone,
            R.drawable.tiger,
            R.drawable.toilet,
            R.drawable.tourism,
            R.drawable.town,
            R.drawable.audio_headphone,
            R.drawable.tractor,
            R.drawable.train,
            R.drawable.trampoline,
            R.drawable.transportation,
            R.drawable.travel,
            R.drawable.treasure,
            R.drawable.truck,
            R.drawable.truck_construction,
            R.drawable.tv,
            R.drawable.ui,
            R.drawable.uk,
            R.drawable.umbrella,
            R.drawable.under_construction,
            R.drawable.unicorn,
            R.drawable.usa,
            R.drawable.chat_msg,
            R.drawable.usa_flag,
            R.drawable.vegetable,
            R.drawable.video_cameras,
            R.drawable.violin,
            R.drawable.volkswagen,
            R.drawable.volvo,
            R.drawable.war_canon,
            R.drawable.watermelon,
            R.drawable.weightlifting,
            R.drawable.wellness,
            R.drawable.whale,
            R.drawable.windmill,
            R.drawable.football_boots,
            R.drawable.virus,
            R.drawable.vacuum,
            R.drawable.wine,
            R.drawable.walkman,
            R.drawable.sales_sign,
            R.drawable.airpods,
            R.drawable.car_wash,
            R.drawable.car_tanking,
            R.drawable.telescope,
            R.drawable.lab_chemistry
        )
    }
}
