package com.example.buglibrary.data

class ContactMenu(
    val _id: String,
    val __v: String,
    val appname: String,
    val fieldvalue: FieldValue,
    val masterParentId: String,
    val parentId: String,
    val editeddAt: String,
    val createdAt: String,
    val status: String,
    val brandImg: String,
    val bagroundImg: String,
    val menuIcon: String,
    val parentMenu: String
)

data class FieldValue(
    val en: List<ContactLocal>
)

data class ContactLocal(
    val label: String,
    val lblvalus: List<String>,
    val iconurl: String
)