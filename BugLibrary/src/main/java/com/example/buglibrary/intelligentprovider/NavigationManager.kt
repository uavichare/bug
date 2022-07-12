package com.example.buglibrary.intelligentprovider

import android.content.Context
import com.example.buglibrary.utils.FileUtils
import com.mapbox.mapboxsdk.geometry.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.*

class NavigationManager(context: Context) {
    private var verticesMap = JSONObject()
    private var edgesMap = JSONObject()

    init {
        val jsonString =
            FileUtils.loadJSONFromAsset(context, "alfahidi_routes.json")
        try {
            val resp = JSONObject(jsonString)
            val wayPoints: JSONArray = resp.getJSONArray("markers")
            val wayEdges: JSONArray = resp.getJSONArray("edges")
            addVertices(wayPoints)
            addEdges(wayEdges)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun addVertices(points: JSONArray) {
        for (i in 0 until points.length()) {
            try {
                val point = points.getJSONObject(i)
                verticesMap.put(point.getString("_id"), point)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun addEdges(edges: JSONArray) {
        for (k in 0 until edges.length()) {
            try {
                val edge = edges.getJSONObject(k)
                val fromId = edge.getString("from_point")
                if (!edgesMap.has(fromId)) {
                    edgesMap.put(fromId, JSONArray())
                }
                val edgesList = edgesMap.getJSONArray(fromId)
                edgesList.put(edge)
                edgesMap.put(fromId, edgesList)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }

    fun getShortestPath(
        sourceLatLng: LatLng?,
        destinationLat: LatLng?,
        sourceFloor: String?,
        destinationFloor: String?
    ): JSONArray? {
        return aStarShortestPath(sourceLatLng, destinationLat, sourceFloor, destinationFloor)
    }

    private fun aStarShortestPath(
        sourceLatLng: LatLng?, destinationLat: LatLng?,
        sourceFloor: String?, destinationFloor: String?
    ): JSONArray? {
        val closedSet = JSONArray()
        val openSet = JSONArray()
        val cameFrom = JSONObject()
        val wScoreFromSource = JSONObject()
        val fScoreFromSource = JSONObject()
        var result: JSONArray? = JSONArray()
        val keys = verticesMap.keys()
        val nearestSource = getNearestPoint(sourceLatLng!!, sourceFloor!!)
        val nearestDestination = getNearestPoint(
            destinationLat!!,
            destinationFloor!!
        )
        if (nearestSource == null || nearestDestination == null) {
            return JSONArray()
        }
        while (keys.hasNext()) {
            val key = keys.next()
            try {
                wScoreFromSource.put(key, Double.MAX_VALUE)
                fScoreFromSource.put(key, Double.MAX_VALUE)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        var sourceId: String? = null
        try {
            sourceId = nearestSource.getString("_id")
            wScoreFromSource.put(sourceId, 0)
            fScoreFromSource.put(
                sourceId,
                getHeuristicDistance(
                    nearestSource.getDouble("lat"),
                    nearestSource.getDouble("lng"),
                    sourceFloor,
                    nearestDestination.getDouble("lat"),
                    nearestDestination.getDouble("lng"),
                    destinationFloor
                )
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        openSet.put(nearestSource)
        try {
            cameFrom.put(sourceId, null)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        try {
            while (openSet.length() > 0) {
                var currNode = returnProbableNode(openSet, fScoreFromSource)
                if (currNode == null) {
                    currNode = openSet.getJSONObject(0)
                }
                if (currNode!!.getString("_id") == nearestDestination.getString("_id")) {
                    result = reconstructPath(cameFrom, currNode.getString("_id"))
                    break
                }
                val index = currNode.getInt("indexinclosedset")
                if (index > -1) {
                    openSet.remove(index)
                }
                closedSet.put(currNode)
                val edges = edgesMap.getJSONArray(currNode.getString("_id"))
                if (edges.length() == 0) {
                    continue
                }
                for (i in 0 until edges.length()) {
                    val edge = edges.getJSONObject(i)
                    val neighbour =
                        verticesMap.getJSONObject(edge.getString("to_point"))
                    val inClosed =
                        searchInArray(closedSet, neighbour.getString("_id"))
                    if (inClosed) {
                        continue
                    }
                    val inOpen = searchInArray(openSet, neighbour.getString("_id"))
                    if (!inOpen) {
                        openSet.put(neighbour)
                    }
                    val presentScore =
                        wScoreFromSource.getDouble(currNode.getString("_id")) + getWScore(edge)
                    val alreadyCalculatedScore =
                        wScoreFromSource.getDouble(neighbour.getString("_id"))
                    if (presentScore >= alreadyCalculatedScore) {
                        continue
                    }
                    cameFrom.put(neighbour.getString("_id"), currNode.getString("_id"))
                    wScoreFromSource.put(neighbour.getString("_id"), presentScore)
                    val fScore: Double =
                        wScoreFromSource.getDouble(neighbour.getString("_id")) + getHeuristicDistance(
                            neighbour.getDouble("lat"),
                            neighbour.getDouble("lng"),
                            neighbour.getString("level"),
                            nearestDestination.getDouble("lat"),
                            nearestDestination.getDouble("lng"),
                            nearestDestination.getString("level")
                        )
                    fScoreFromSource.put(neighbour.getString("_id"), fScore)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return result
    }

    private fun getWScore(edge: JSONObject): Double {
        var distanceFac = 0.0
        try {

            val toPointId = edge.getString("to_point")
            val fromPointId = edge.getString("from_point")
            val toPoint = verticesMap.getJSONObject(toPointId)
            val fromPoint = verticesMap.getJSONObject(fromPointId)

            val lng2 = toPoint.getDouble("lng")
            val lat2 = toPoint.getDouble("lat")
            val lng1 = fromPoint.getDouble("lng")
            val lat1 = fromPoint.getDouble("lat")
            val R = 6371000.0
            val dLat = Math.toRadians(lat2 - lat1) // deg2rad below
            val dLon = Math.toRadians(lng2 - lng1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(
                Math.toRadians(lat2)
            ) * sin(dLon / 2) * sin(dLon / 2)
            val c =
                2 * atan2(sqrt(a), sqrt(1 - a))
            val d = R * c
            distanceFac = d
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return distanceFac
    }

    private fun reconstructPath(cameFrom: JSONObject, id: String): JSONArray? {
        val result = JSONArray()
        try {
            result.put(verticesMap.getJSONObject(id))
            var key: String? = cameFrom.getString(id)
            while (key != null) {
                result.put(verticesMap.getJSONObject(key))
                key = cameFrom.getString(key)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return result
    }

    private fun searchInArray(array: JSONArray, id: String): Boolean {
        var found = false
        for (k in 0 until array.length()) {
            try {
                val obj = array.getJSONObject(k)
                if (obj.getString("_id") == id) {
                    found = true
                    return found
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return found
    }

    private fun returnProbableNode(openSet: JSONArray, fScoreObj: JSONObject): JSONObject? {
        var min = Double.MAX_VALUE
        var node: JSONObject? = null
        for (i in 0 until openSet.length()) {
            try {
                val point = openSet.getJSONObject(i)
                val fScore = fScoreObj.getDouble(point.getString("_id"))
                if (fScore < min) {
                    min = fScore
                    node = point
                    node.put("indexinclosedset", i)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return node
    }

    private fun getNearestPoint(sourceLatLng: LatLng, sourceFloor: String): JSONObject? {
        var minimum = Double.MAX_VALUE
        var nearestPoint: JSONObject? = null
        val keys = verticesMap.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            try {
                val point = verticesMap.getJSONObject(key)
                if (point.getString("level") == sourceFloor) {
                    val distance: Double = getHeuristicDistance(
                        sourceLatLng.latitude,
                        sourceLatLng.longitude,
                        sourceFloor,
                        point.getDouble("lat"),
                        point.getDouble("lng"),
                        point.getString("level")
                    )
                    if (distance < minimum) {
                        nearestPoint = point
                        minimum = distance
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return nearestPoint
    }

    private fun getHeuristicDistance(
        lat1: Double, lng1: Double,
        level1: String, lat2: Double,
        lng2: Double, level2: String
    ): Double {
        val R = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1) // deg2rad below
        val dLon = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(
            Math.toRadians(lat2)
        ) * sin(dLon / 2) * sin(dLon / 2)
        val c =
            2 * atan2(sqrt(a), sqrt(1 - a))
        val d = R * c // Distance in metres
        val levelDiff = abs(level1.toInt() - level2.toInt()) * 50.toDouble()
        return d + levelDiff
    }
}