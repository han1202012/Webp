package kim.hsl.webp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.webp.libwebp
import kim.hsl.webp.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {

    companion object{
        val TAG = "MainActivity"
        init {
            System.loadLibrary("webp")
        }
    }

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e(TAG, "libwebp 函数库版本 : ${libwebp.WebPGetDecoderVersion()}")

        // 测试 WebP 解码速度
        decodeWebP()

        // 测试 WebP 编码速度
        encodeWebP()

        // 使用 libwebp 库编码 WebP 图片
        libwebpEncode()

        // 使用 libwebp 库解码 WebP 图片
        libwebpDecode()
    }

    @SuppressLint("ResourceType")
    fun libwebpDecode() {
        var webPStart = System.currentTimeMillis()

        // 获取 WebP 资源文件的输入流
        var inputStream: InputStream = resources.openRawResource(R.mipmap.icon_webp)

        // 将数据读取到 Byte 数组输出流中
        var bos: ByteArrayOutputStream = ByteArrayOutputStream()
        var buffer: ByteArray = ByteArray(2048)
        // 记录长度
        var len = inputStream.read(buffer)
        while ( len != -1 ){
            bos.write(buffer, 0, len)
            len = inputStream.read(buffer)
        }
        inputStream.close()

        // 读取完毕后 , 获取完整的 Byte 数组数据
        var data_webp: ByteArray = bos.toByteArray()

        // 将 ByteArray 解码成 ARGB 数据
        var width = IntArray(1)
        var height = IntArray(1)
        var data_argb_byte: ByteArray = libwebp.WebPDecodeARGB(
                data_webp,
                data_webp.size.toLong(),
                width,
                height)

        // 将 data_argb: ByteArray 转为 IntArray
        var data_argb_int = IntArray(data_argb_byte.size / 4)
        // 使用 nio 中的 ByteBuffer 进行读写
        var byteBuffer: ByteBuffer = ByteBuffer.wrap(data_argb_byte);
        // 将 byteBuffer 转为 IntBuffer , 然后获取其中的 int 数组
        byteBuffer.asIntBuffer().get(data_argb_int)

        // 将 ARGB 数据转为 Bitmap 位图图像
        var bitmap: Bitmap = Bitmap.createBitmap(
                data_argb_int,              // 图像数据 , int 数组格式
                width[0],                   // 图像宽度
                height[0],                  // 图像高度
                Bitmap.Config.ARGB_8888     // 图像颜色格式
        )

        // 界面显示解码后的位图
        binding.imageView.setImageBitmap(bitmap)

        Log.e(TAG, "使用 libwebp.so 库解码 WebP 格式图片时间 : ${System.currentTimeMillis() - webPStart} ms")
    }


    fun libwebpEncode(){
        var webPStart = System.currentTimeMillis()

        // 读取一张本地图片
        var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_png)
        // 获取位图宽高
        var width = bitmap.width
        var height = bitmap.height
        // 申请一个 Byte 缓冲区
        var byteBuffer: ByteBuffer = ByteBuffer.allocate(bitmap.byteCount)
        // 将 位图 数据拷贝到 Byte 缓冲区中
        bitmap.copyPixelsToBuffer(byteBuffer)

        // 使用 libwebp.so 进行 WebP 格式编码
        var data: ByteArray = libwebp.WebPEncodeRGBA(
                byteBuffer.array(), // 位图数据
                width,       // 位图宽度
                height,      // 位图高度
                width * 4,   // 位图每行数据
                75F                 // 图像质量
        )

        // 将数据写出到文件中
        var fos = FileOutputStream("${cacheDir}/icon_webp2.webp")
        fos.write(data)
        fos.close()

        Log.e(TAG, "使用 libwebp.so 库编码 WebP 格式图片时间 : ${System.currentTimeMillis() - webPStart} ms , " +
                "输出文件 : ${cacheDir}/icon_webp2.webp")
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