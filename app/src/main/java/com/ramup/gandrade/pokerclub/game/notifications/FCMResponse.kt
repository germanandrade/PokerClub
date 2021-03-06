package com.ramup.gandrade.pokerclub.game.notifications

data class FCMResponse (val multicast_id:Long, val success:Int, val failure:Int, val canonical_ids:Int, val results:Array<Message>)

data class Message(val message_id:String)
