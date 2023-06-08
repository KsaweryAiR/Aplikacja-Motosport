package com.example.motosport2

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class MyGLRenderer : GLSurfaceView.Renderer {
    private var shapeType: Int = GLES20.GL_TRIANGLES

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Rysowanie kształtu
        val vertexCoords = when (shapeType) {
            GLES20.GL_TRIANGLES -> floatArrayOf(
                0.0f, 1.0f, 0.0f,  // Top
                -1.0f, -1.0f, 0.0f,  // Bottom left
                1.0f, -1.0f, 0.0f   // Bottom right
            )
            GLES20.GL_LINES -> floatArrayOf(
                -0.5f, -0.5f, 0.0f,  // Bottom left
                0.5f, 0.5f, 0.0f     // Top right
            )
            GLES20.GL_TRIANGLE_FAN -> {
                val numPoints = 100
                val angleStep = (2 * Math.PI / numPoints)
                val radius = 0.8f
                val center = floatArrayOf(0.0f, 0.0f, 0.0f)
                val vertexCoords = FloatArray(numPoints * 3 + 3)
                vertexCoords[0] = center[0]
                vertexCoords[1] = center[1]
                vertexCoords[2] = center[2]
                for (i in 0 until numPoints) {
                    val angle = i * angleStep
                    val x = (radius * cos(angle)).toFloat()
                    val y = (radius * sin(angle)).toFloat()
                    val index = i * 3 + 3
                    vertexCoords[index] = x
                    vertexCoords[index + 1] = y
                    vertexCoords[index + 2] = 0.0f
                }
                vertexCoords
            }
            GLES20.GL_TRIANGLE_STRIP -> floatArrayOf(
                -0.5f, -0.5f, 0.0f,  // Bottom left
                -0.5f, 0.5f, 0.0f,   // Top left
                0.5f, -0.5f, 0.0f,   // Bottom right
                0.5f, 0.5f, 0.0f     // Top right
            )
            else -> floatArrayOf()
        }

        if (vertexCoords.isNotEmpty()) {
            val vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply {
                    put(vertexCoords)
                    position(0)
                }

            val vertexShaderCode = """
                attribute vec4 vPosition;
                void main() {
                    gl_Position = vPosition;
                }
            """.trimIndent()

            val fragmentShaderCode = """
                precision mediump float;
                void main() {
                    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
                }
            """.trimIndent()

            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

            val program = GLES20.glCreateProgram().apply {
                GLES20.glAttachShader(this, vertexShader)
                GLES20.glAttachShader(this, fragmentShader)
                GLES20.glLinkProgram(this)
                GLES20.glUseProgram(this)
            }

            val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(
                positionHandle, 3, GLES20.GL_FLOAT, false,
                3 * 4, vertexBuffer
            )

            GLES20.glDrawArrays(shapeType, 0, vertexCoords.size / 3)

            GLES20.glDisableVertexAttribArray(positionHandle)
        }
    }

    fun changeShape(newShape: Int) {
        shapeType = newShape
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = MyGLRenderer()
        setRenderer(renderer)
    }

    fun changeShape(newShape: Int) {
        renderer.changeShape(newShape)
        requestRender()
    }
}

class ustaact3 : AppCompatActivity() {
    private var gLView: MyGLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val triangleButton = Button(this)
        triangleButton.text = "Trójkąt"
        triangleButton.setOnClickListener {
            gLView?.changeShape(GLES20.GL_TRIANGLES)
        }

        val lineButton = Button(this)
        lineButton.text = "Linie"
        lineButton.setOnClickListener {
            gLView?.changeShape(GLES20.GL_LINES)
        }

        val circleButton = Button(this)
        circleButton.text = "Koło"
        circleButton.setOnClickListener {
            gLView?.changeShape(GLES20.GL_TRIANGLE_FAN)
        }

        val squareButton = Button(this)
        squareButton.text = "Kwadrat"
        squareButton.setOnClickListener {
            gLView?.changeShape(GLES20.GL_TRIANGLE_STRIP)
        }

        layout.addView(triangleButton)
        layout.addView(lineButton)
        layout.addView(circleButton)
        layout.addView(squareButton)

        gLView = MyGLSurfaceView(this)
        layout.addView(gLView)

        setContentView(layout)
    }

    override fun onPause() {
        super.onPause()
        gLView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        gLView?.onResume()
    }
}





