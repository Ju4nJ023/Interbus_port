package com.juanjose.interbuspr.Myapp


import com.google.gson.annotations.SerializedName



data class RutaResponse(@SerializedName("features")val features:List<Feature>,
                        @SerializedName("paradas") val paradas: List<Parada>)

data class Feature(@SerializedName("geometry")val geometry:Geometry)
data class Geometry(@SerializedName("coordinates")val coordinates: List<List<Double>>)


data class Parada(val nombre: String,
                  val lat: Double,
                  val lng: Double
)
