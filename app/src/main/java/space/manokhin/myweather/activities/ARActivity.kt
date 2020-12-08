package space.manokhin.myweather.activities

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.collision.Plane
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import space.manokhin.myweather.R
import space.manokhin.myweather.utils.Constants


class ARActivity: AppCompatActivity() {
    private val MIN_OPENGL_VERSION = 3.0
    lateinit var arFragment: ArFragment
    lateinit var selectedObject: Uri
    private var total_count = 0
    private val ASSET =
        "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"

    private val GLTF_PATH =
        "https://github.com/andreyTheGreatest/gltf_assets/raw/main/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIsSupportedDeviceOrFinish(this)
        setContentView(R.layout.ar_activity)
        val weather = intent.getStringExtra("weather")
        val asset = Constants.get3DAssetByWeather(weather!!)
        arFragment = (supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?)!!
        arFragment.setOnTapArPlaneListener {hitResult, plane, motionEvent ->
            if (total_count > 0) return@setOnTapArPlaneListener
            val anchor = hitResult.createAnchor()
            total_count++

            placeObject(
                arFragment,
                anchor,
                RenderableSource.builder()
                    .setSource(
                        this,
                        Uri.parse(GLTF_PATH + asset),
                        RenderableSource.SourceType.GLTF2
                    )
                    .setScale(0.001f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
        }


    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor, model: RenderableSource) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .setRegistryId(GLTF_PATH)
            .build()
            .thenAccept {
                Log.d("STATUS", "model $model loaded successfully!")
                addNodeToScene(fragment, anchor, it)
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e("FragmentActivity", "Sceneform requires Android N or later")
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo
                .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e("FragmentActivity", "Sceneform requires OpenGL ES 3.0 later")
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        return true
    }

}