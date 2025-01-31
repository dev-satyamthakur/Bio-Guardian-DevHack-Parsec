package com.satyamthakur.bio_guardian.data.entity


import com.google.gson.annotations.SerializedName

class AnimalInfoResponse : ArrayList<AnimalInfoResponse.AnimalInfoResponseItem>(){
    data class AnimalInfoResponseItem(
        @SerializedName("characteristics")
        val characteristics: Characteristics? = null,
        @SerializedName("locations")
        val locations: List<String?>? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("taxonomy")
        val taxonomy: Taxonomy? = null
    ) {
        data class Characteristics(
            @SerializedName("age_of_sexual_maturity")
            val ageOfSexualMaturity: String? = null,
            @SerializedName("age_of_weaning")
            val ageOfWeaning: String? = null,
            @SerializedName("biggest_threat")
            val biggestThreat: String? = null,
            @SerializedName("color")
            val color: String? = null,
            @SerializedName("diet")
            val diet: String? = null,
            @SerializedName("estimated_population_size")
            val estimatedPopulationSize: String? = null,
            @SerializedName("gestation_period")
            val gestationPeriod: String? = null,
            @SerializedName("group_behavior")
            val groupBehavior: String? = null,
            @SerializedName("habitat")
            val habitat: String? = null,
            @SerializedName("height")
            val height: String? = null,
            @SerializedName("length")
            val length: String? = null,
            @SerializedName("lifespan")
            val lifespan: String? = null,
            @SerializedName("litter_size")
            val litterSize: String? = null,
            @SerializedName("location")
            val location: String? = null,
            @SerializedName("most_distinctive_feature")
            val mostDistinctiveFeature: String? = null,
            @SerializedName("name_of_young")
            val nameOfYoung: String? = null,
            @SerializedName("other_name(s)")
            val otherNames: String? = null,
            @SerializedName("predators")
            val predators: String? = null,
            @SerializedName("prey")
            val prey: String? = null,
            @SerializedName("skin_type")
            val skinType: String? = null,
            @SerializedName("top_speed")
            val topSpeed: String? = null,
            @SerializedName("type")
            val type: String? = null,
            @SerializedName("weight")
            val weight: String? = null
        )
    
        data class Taxonomy(
            @SerializedName("class")
            val classX: String? = null,
            @SerializedName("family")
            val family: String? = null,
            @SerializedName("genus")
            val genus: String? = null,
            @SerializedName("kingdom")
            val kingdom: String? = null,
            @SerializedName("order")
            val order: String? = null,
            @SerializedName("phylum")
            val phylum: String? = null,
            @SerializedName("scientific_name")
            val scientificName: String? = null
        )
    }
}