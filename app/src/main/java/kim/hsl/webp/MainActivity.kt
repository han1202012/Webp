package kim.hsl.webp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 测试 WebP 解码速度
        decodeWebP()

        // 测试 WebP 编码速度
        encodeWebP()
    }

    fun encodeWebP(){
        // 读取一张本地图片
        var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_png)

        var pngStart = System.currentTimeMillis()
        var fos = FileOutputStream("${cacheDir}/icon_png.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 75, fos)
        fos.close()
        Log.e(TAG, "编码 png 格式图片时间 : ${System.currentTimeMillis() - pngStart} ms , " +
                "输出文件 : ${cacheDir}/icon_png.png")

        var webPStart = System.currentTimeMillis()
        fos = FileOutputStream("${cacheDir}/icon_webp.webp")
        bitmap.compress(Bitmap.CompressFormat.WEBP, 75, fos)
        fos.close()
        Log.e(TAG, "编码 WebP 格式图片时间 : ${System.currentTimeMillis() - webPStart} ms , " +
                "输出文件 : ${cacheDir}/icon_webp.webp")
    }

    fun decodeWebP(){
        var pngStart = System.currentTimeMillis()
        BitmapFactory.decodeResource(resources, R.mipmap.icon_png)
        Log.e(TAG, "解码 png 格式图片时间 : ${System.currentTimeMillis() - pngStart} ")

        var webPStart = System.currentTimeMillis()
        BitmapFactory.decodeResource(resources, R.mipmap.icon_webp)
        Log.e(TAG, "解码 WebP 格式图片时间 : ${System.currentTimeMillis() - webPStart} ")
    }
}