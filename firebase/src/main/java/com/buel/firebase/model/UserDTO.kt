package com.buel.sknmethodist.manager.firebase.model

import com.google.gson.annotations.SerializedName

data class UserDTO(
        @SerializedName("uid")
        var uid: String? = "",

        @SerializedName("push_token")
        var pushToken: String? = "",

        @SerializedName("email")
        var email: String? = "",

        @SerializedName("tell")
        var tell: String? = "",

        @SerializedName("photo_uri")
        var photoUri: String? = "",

        @SerializedName("type")
        var type: String? = "",

        @SerializedName("mac_address")
        var macAddress: String? = "",

        @SerializedName("name")
        var name: String? = ""


) {
    override fun toString(): String {
        return """           
        ::::::::::::::: USER DATA ::::::::::::::::::
        :: uid         ===   $uid 
        :: pushToken   ===   $pushToken 
        :: email       ===   $email 
        :: tell        ===   $tell 
        :: photoUri    ===   $photoUri 
        :: type        ===   $type 
        :: macAddress  ===   $macAddress 
        :: name        ===   $name 
        :::::::::::::::::::::::::::::::::::::::::::::
        """
    }
}