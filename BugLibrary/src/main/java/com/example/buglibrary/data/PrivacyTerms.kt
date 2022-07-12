package com.example.buglibrary.data

/**
 * Created by Furqan on 07-05-2019.
 */
class PrivacyTerms(
    val ScreenType: String,
    val Sections: List<Section>
)

class Section(
    val Type: String,
    val SectionName: String,
    val SectionName_AR: String,
    val SectionDetails: String,
    val SectionDetails_AR: String,
    val SubSections: List<Section>
)