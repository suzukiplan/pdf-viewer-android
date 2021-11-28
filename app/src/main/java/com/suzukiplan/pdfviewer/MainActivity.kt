package com.suzukiplan.pdfviewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private var pdfRenderer: PdfRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.view_pager)
        // 横スワイプで切り替えたい場合は ViewPager2.ORIENTATION_HORIZONTAL にする
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        // レンダラを作成（以下の処理は本当は非同期の方が良い）
        pdfRenderer = PdfRenderer(assets.openFd("example.pdf").parcelFileDescriptor)

        viewPager.adapter = Adapter()
    }

    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount() = pdfRenderer?.pageCount ?: 0
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(layoutInflater.inflate(R.layout.view_holder, parent, false))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.image_view)
        private var bitmap: Bitmap? = null

        fun bind(position: Int) {
            val page = pdfRenderer?.openPage(position) ?: return
            if (page.width != bitmap?.width || page.height != bitmap?.height) {
                bitmap?.recycle()
                bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            }
            val bitmap = this.bitmap
            if (null != bitmap) {
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                imageView.setImageBitmap(bitmap)
            }
            page.close()
        }
    }
}