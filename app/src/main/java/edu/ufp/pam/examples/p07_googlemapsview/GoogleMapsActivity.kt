package edu.ufp.pam.examples.p07_googlemapsview

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import edu.ufp.pam.examples.databinding.ActivityGoogleMapsBinding
import edu.ufp.pam.examples.R
import java.io.IOException

/**
 *  ================ ToDo Project Configs  ================
 *  1. Change App ID:
 *    - File > Project Structure:
 *          Modules > Default Config:
 *              Application ID: edu.ufp.pam.examples.p07_googlemapsview
 *
 *  2. Get API Key from Google Console:
 *    - Login to <https://console.developers.google.com/> to generate API Key
 *          Restrict key use to Android apps by setting
 *              * package name: edu.ufp.pam.examples.p07_googlemapsview
 *              * SHA-1 certificate fingerprint: <debug SHA-1 from gradlew command>
 *                How to obtain the SHA1 for debug:
 *                  1. Open Android Studio Terminal shell
 *                  2. Execute command: ./gradlew signingReport
 *
 *  3. Insert the API Key into local.properties file
 *      MAPS_API_KEY=<AIza...>
 *      NB:
 *          * Do NOT add local.properties into version control (git)
 *          * .gitignore should include local.properties
 *
 *  4. If App crashes on emulator, check if emulator has Google Play Services installed:
 *    - Tools > SDK Manager:
 *          > Select Tab *SDK Tools*
 *              > Check *Google Play Services* to install
 *    - Make sure the AVD Device supports Google Play
 *          e.g. Nexus 5x API 30 or above
 *
 *
 *  ================ ToDo Use Permissions   ================
 *  Permissions are classified into two categories:
 *  - normal categories and
 *  - dangerous categories (require run time permission from user, e.g. access to CONTACTS,
 *    CALENDAR, LOCATION, etc.).
 *
 *  Starting with Android 6.0, uses permissions may be handled differently than before, i.e.,
 *  - No need to request permission during app installation (as defined inside AndroidManifest);
 *  - Instead, request permissions at runtime (when actually required).
 *
 *  Permissions ACCESS_COARSE and FINE_LOCATION for location and map usage:
 *  - These permissions are NOT required to use Google Maps Android API v2
 *  - But required with "MyLocation" functionality.
 *
 *  This Activity will ask for permissions @ runtime...
 *      uses companion object with parameters to request location permission and draw lines.
 *
 */
class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityGoogleMapsBinding

    //ToDo:
    // Set a companion object (static field) with several codes (constants):
    companion object {
        //Codes to request location permission:
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE = 1
        private const val PERMISSION_REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
        private const val REQUESTING_LOCATION_UPDATES_KEY = "LOCATION_UPDATES_KEY"

        //Codes for Color of polyline and polygon
        private const val COLOR_BLACK_ARGB = 0xff000000
        private const val COLOR_WHITE_ARGB = -0x1
        private const val COLOR_GREEN_ARGB = -0xc771c4
        private const val COLOR_PURPLE_ARGB = -0x7e387c
        private const val COLOR_ORANGE_ARGB = -0xa80e9
        private const val COLOR_BLUE_ARGB = -0x657db
        //Codes for Thickness of polyline and polygon
        private const val POLYLINE_STROKE_WIDTH_PX = 12
        private const val POLYGON_STROKE_WIDTH_PX = 8
        //Codes for Pattern of polyline and polygon
        private const val PATTERN_DASH_LENGTH_PX = 20.0f
        private const val PATTERN_GAP_LENGTH_PX = 20.0f
    }

    //ToDo:
    // Create patterns for polylines drawing
    private val DOT: PatternItem = Dot()
    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX)
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX)

    //ToDo:
    // Create stroke patterns for drawing map elements
    //Create a stroke pattern of a gap followed by a dot.
    private val PATTERN_POLYLINE_DOTTED: List<PatternItem> = listOf(GAP, DOT)
    // Create a stroke pattern of a gap followed by a dash.
    private val PATTERN_POLYGON_ALPHA = listOf(GAP, DASH)
    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private val PATTERN_POLYGON_BETA = listOf(DOT, GAP, DASH, GAP)

    //ToDo:
    // Properties enabling to receive device location updates
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationRequestsActivated = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //================ Activate Location Client ===============
        //Get data saved previously into Bundle
        updateValuesFromBundle(savedInstanceState)

        // Construct a FusedLocationProviderClient to be able to obtain current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Setup LocationCallback() to receive current location updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(localRes: LocationResult) {
                super.onLocationResult(localRes)
                lastLocation = localRes.lastLocation!!
                Log.i(this.javaClass.simpleName,
                    "onLocationResult(): localRes=${lastLocation.toString()}"
                )
                //Put marker on the lastly received known location
                placeMarkerOnMapWithUserIcon(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

        //Create LocationRequest so that when app/device changes location the map
        // will be updated with a new marker
        createLocationRequest()

    }

    /**
     * This callback is triggered when the map is ready to be used,
     * i.e. manipulates the map once available.
     *
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        //drawSydneyMarker();

        Log.i(this.javaClass.simpleName, "onMapReady(): going to draw markers on map...")
        //=============== STEP 1: Zoom over a city ===============
        //drawRotundaBoavistaMarker(16.0f)

        //=============== STEP 2: Draw a polyline ===============
        //drawPolyLineRotunda2Palacio()

        //=============== STEP 3: Draw a polygon ===============
        drawPolygonAroundCasaMusica()

        //=============== STEP 4: Get user permissions an show current location ===============
        //setUpMapTypeAndDrawCurrentLocationMarker()
    }

    private fun drawSydneyMarker(){
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun drawRotundaBoavistaMarker(zoom: Float){
        //Add a marker in Rotunda Boavista @ Porto and zoom it
        val rotundaBoavista = LatLng(41.157921, -8.629162)

        mMap.addMarker(MarkerOptions().position(rotundaBoavista).title("Lion over Eagle! :)"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rotundaBoavista, zoom))

        //Enable zoom controls on the map (Zoom level 0..20)
        mMap.getUiSettings().setZoomControlsEnabled(true)
        //Declare instance callback triggered when user clicks a marker on this map
        mMap.setOnMarkerClickListener(this)
    }

    private fun stylePolyline(polyline: Polyline) {
        var type = ""
        // Get the data object stored with the polyline.
        if (polyline.tag != null) {
            type = polyline.tag.toString()
        }
        when (type) {
            "A" -> // Use a custom bitmap as the cap at the start of the line
                polyline.startCap = CustomCap(
                    BitmapDescriptorFactory.fromResource(R.mipmap.ic_user_location),
                    10F
                )
            "B" -> // Use a round cap at the start of the line
                polyline.startCap = RoundCap()
        }
        polyline.endCap = RoundCap()
        polyline.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
        polyline.color = COLOR_BLACK_ARGB.toInt()
        polyline.jointType = JointType.ROUND
    }

    private fun drawPolyLineRotunda2Palacio(){
        val polyline: Polyline = mMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(41.157921, -8.629162),
                    LatLng(41.155013, -8.626934),
                    LatLng(41.152627, -8.626193),
                    LatLng(41.151305, -8.625782),
                    LatLng(41.150883, -8.625931),
                    LatLng(41.148773, -8.625408)
                )
        )
        // Store a data object with the polyline to associate a tag
        polyline.setTag("A");
        //Set properties
        stylePolyline(polyline)

        // Set listeners for click events
        mMap.setOnPolylineClickListener(this);
        //map.setOnPolygonClickListener(this);
    }

    private fun stylePolygon(polygon: Polygon) {
        var type = ""
        // Get the data object stored with the polygon.
        if (polygon.tag != null) {
            type = polygon.tag.toString()
        }
        var pattern: List<PatternItem?>? = null
        var strokeColor = COLOR_BLACK_ARGB.toInt()
        var fillColor = COLOR_WHITE_ARGB
        when (type) {
            "A" -> {
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA
                strokeColor = COLOR_GREEN_ARGB
                fillColor = COLOR_PURPLE_ARGB
            }
            "B" -> {
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA
                strokeColor = COLOR_ORANGE_ARGB
                fillColor = COLOR_BLUE_ARGB
            }
        }
        polygon.strokePattern = pattern
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }

    private fun drawPolygonAroundCasaMusica(){
        // Add polygons to indicate areas on the map.
        val polygon = mMap.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(41.158352, -8.630419),
                    LatLng(41.158787, -8.629968),
                    LatLng(41.159333, -8.630409),
                    LatLng(41.159466, -8.630860),
                    LatLng(41.159141, -8.631350),
                    LatLng(41.158514, -8.631674)
                )
        )
        // Store a data object with the polygon to associate a type.
        polygon.tag = "A"
        //Set style properties
        stylePolygon(polygon)
        // Set listeners for click events
        mMap.setOnPolygonClickListener(this);
    }

    /**
     * Check if app has been granted ACCESS_FINE_LOCATION permission,
     * and if it does not then request it from the user.
     */
    private fun askUserPermissionToAccessFineLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE
            )
            return
        }
    }

    /**
     * Method called after asking user permission:
     *  Start update request if it has RESULT_OK for the REQUEST_CHECK_SETTINGS
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //If receives code for permission request
        if (requestCode == PERMISSION_REQUEST_CHECK_SETTINGS) {
            Log.e(
                this.javaClass.simpleName,
                "onActivityResult(): requestCode = REQUEST_CHECK_SETTINGS"
            )
            //If permission granted
            if (resultCode == Activity.RESULT_OK) {
                locationRequestsActivated = true
                startLocationUpdates()
            }
        }
    }

    private fun setUpMapTypeAndDrawCurrentLocationMarker() {
        //Get permissions from user
        askUserPermissionToAccessFineLocation()

        //Android Maps API provides different map types:
        //  MAP_TYPE_NORMAL: typical road map with labels
        //  MAP_TYPE_SATELLITE: satellite view of an area with no labels
        //  MAP_TYPE_TERRAIN: detailed view of area (e.g.show elevation)
        //  MAP_TYPE_HYBRID: combination of the satellite and normal mode
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        //Get zoom at current location
        zoomCurrentLocationMarker()
    }

    @Throws(SecurityException::class)
    private fun zoomCurrentLocationMarker(){
        // Enable my-location layer which draws a light blue dot on the user’s location.
        // Also adds Button to map to center on user’s location.
        mMap.isMyLocationEnabled = true

        // Gives most recent available location
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Move camera to the user’s current location.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                //Draw marker with User Icon OR Address
                //placeMarkerOnMapWithUserIcon(currentLatLng)
                placeMarkerOnMapWithAddr(currentLatLng)
                //Zoom in
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            }
        }
    }

    private fun placeMarkerOnMapWithAddr(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        val titleStr = getAddress(location)
        Log.e(this.javaClass.simpleName, "placeMarkerOnMapWithAddr(): titleStr = ${titleStr}")
        markerOptions.title(titleStr)
        mMap.addMarker(markerOptions)
    }

    //Show address of location when the user clicks on marker
    private fun getAddress(latLng: LatLng): String {
        //Geocoder allows turning latitude and longitude coordinate into an address and vice versa.
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            //Get address from given location (lat/long)
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            Log.i(this.javaClass.simpleName, "getAddress(): addresses = ${addresses}")

            //If response contains an address then append to string and return.
            if (addresses != null  && !addresses.isEmpty()) {
                address = addresses[0]
                Log.i(this.javaClass.simpleName, "getAddress(): address = ${address}")
                Log.i(
                    this.javaClass.simpleName,
                    "getAddress(): address.maxAddressLineIndex = ${address.maxAddressLineIndex}"
                )
                Log.i(
                    this.javaClass.simpleName, "getAddress(): address.getAddressLine = " +
                            address.getAddressLine(0)
                )
                for (i in 0..address.maxAddressLineIndex) {
                    addressText +=
                        if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                    Log.i(this.javaClass.simpleName, "getAddress(): addressText = ${addressText}")
                }
            }
        } catch (e: IOException) {
            val msg = e.localizedMessage
            Log.i(this.javaClass.simpleName, msg)
        }
        return addressText
    }

    override fun onMarkerClick(marker: Marker): Boolean = false

    override fun onPolylineClick(polyline: Polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if (polyline.pattern == null || !polyline.pattern!!.contains(DOT)) {
            polyline.pattern = PATTERN_POLYLINE_DOTTED
        } else {
            // The default pattern is a solid stroke.
            polyline.pattern = null
        }
        Toast.makeText(this,
            "Route type " + polyline.tag.toString(),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPolygonClick(polygon: Polygon) {
        Log.i(this.javaClass.simpleName, "onPolygonClick(): current polygonFillColor = ${polygon.fillColor}")
        // Switch Polygon FillColor between COLOR_WHITE_ARGB<->COLOR_BLUE_ARGB
        /*"A" -> {
            // Apply a stroke pattern to render a dashed line, and define colors.
            pattern = PATTERN_POLYGON_ALPHA
            strokeColor = COLOR_GREEN_ARGB
            fillColor = COLOR_PURPLE_ARGB
        }
        "B" -> {
            // Apply a stroke pattern to render a line of dots and dashes, and define colors.
            pattern = PATTERN_POLYGON_BETA
            strokeColor = COLOR_ORANGE_ARGB
            fillColor = COLOR_BLUE_ARGB
            */
        if (polygon.tag == "A") {
            if (polygon.strokeColor==COLOR_GREEN_ARGB) {
                polygon.strokeColor = COLOR_BLUE_ARGB
            } else {
                polygon.strokeColor = COLOR_GREEN_ARGB
            }
        } else if (polygon.tag == "B" ){
            if (polygon.strokeColor==COLOR_ORANGE_ARGB) {
                polygon.strokeColor = COLOR_BLUE_ARGB
            } else {
                polygon.strokeColor = COLOR_ORANGE_ARGB
            }
        }
        Toast.makeText(this,
            "Polygon click: $polygon",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun placeMarkerOnMapWithUserIcon(location: LatLng) {
        //Set user’s current location as the position for the marker
        val markerOptions = MarkerOptions().position(location)

        //ToDo:
        // Step 1: place marker with different color
        /*
        markerOptions.icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )
        */

        //ToDo:
        // Step 2: place marker with different icon
        //  2.1. Simply copy/paste each *ic_user_location.png* (one by one), from each directory
        //       *res>mipmap-...* of Reference Project, into same directory of Classes Project:
        //          Check Project Source Files:
        //              app>src>main>res>mipmap-hdpi
        //              app>src>main>res>mipmap-mdpi
        //              app>src>main>res>mipmap-xdpi
        //              app>src>main>res>mipmap-xxdpi
        //              app>src>main>res>mipmap-xxxdpi
        //  OR
        //  2.2. Dowmload custom pins (e.g. ic_user_location) from
        //       <https://koenig-media.raywenderlich.com/uploads/2016/09/ic_user_location.zip>
        //       Then, unzip and copy/paste each *ic_user_location.png* (from each directory)
        //       into respective same *res>mipmap-...* of Classes Project.
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources,R.mipmap.ic_user_location)
            )
        )

        //Add the marker to the map
        mMap.addMarker(markerOptions)
    }

    private fun createLocationRequest() {
        //Create and set locationRequest attributes
        locationRequest = LocationRequest()
        //Rate at which app wants to receive updates
        locationRequest.interval = 10000
        //Rate at which app can handle updates.
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //Builder of request
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        //Check state of user’s location settings
        val client = LocationServices.getSettingsClient(this)
        //Create task to check location settings
        val task = client.checkLocationSettings(builder.build())

        //On task success init location request
        task.addOnSuccessListener {
            locationRequestsActivated = true
            startLocationUpdates()
        }

        //On task failure means location settings have issues (e.g. location settings turned off)
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // User location settings are not satisfied... show dialog to set it
                try {
                    // Show dialog by calling startResolutionForResult() and
                    // check result in onActivityResult()
                    e.startResolutionForResult(
                        this@GoogleMapsActivity,
                        PERMISSION_REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e(this.javaClass.simpleName,
                        "createLocationRequest(): sendEx = ${sendEx}"
                    )
                }
            }
        }
    }

    @Throws(SecurityException::class)
    private fun startLocationUpdates() {
        //If does not have ACCESS_FINE_LOCATION then request for it
        if (!locationRequestsActivated) {
            askUserPermissionToAccessFineLocation()
        }
        Log.e(this.javaClass.simpleName,
            "startLocationUpdates(): going to request location update..."
        )
        //Request for location updates with locationRequest, locationCallback, and null Looper
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    //Callback to save the Activity instance state
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, locationRequestsActivated)
        super.onSaveInstanceState(outState)
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            locationRequestsActivated = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY)
        }
        //...
        // Update UI to match restored state
        //updateUI()
    }

} // End GoogleMapsActivity class