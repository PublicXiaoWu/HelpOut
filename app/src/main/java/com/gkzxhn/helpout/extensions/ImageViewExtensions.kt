package com.gkzxhn.helpout.extensions

/**
 * Created by 方 on 2018/3/6.
 */
/*
fun ImageView.load(context: Context, url: String, errorRes: Int) {
    Glide.with(context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .error()
            .into(this)
}

fun ImageView.loadBlur(context: Context, url: String) {
    GlideApp.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(this)
}

fun ImageView.loadRoundConner(context: Context, url: Any, errorRes: Int) {
    GlideApp.with(context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .signature(GlideUrl(url.toString()))
            .error(errorRes)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .apply(RequestOptions.bitmapTransform(CenterCropRoundCornerTransform(6f.dp2px())))
            .into(this)
}

fun ImageView.loadRound(context: Context, url: Any, errorRes: Int, hasCache: Boolean = true) {
    GlideApp.with(context)
            .load(url)
            .skipMemoryCache(!hasCache) // 不使用内存缓存
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(if (hasCache) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE)
            .apply(RequestOptions.circleCropTransform())
            .error(errorRes)
//            .listener(object : RequestListener<Drawable> {
//                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                    return false
//                }
//
//                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                    GlideApp.with(context)
//                            .load(errorRes)
//                            .transition(withCrossFade())
//                            .apply(RequestOptions.circleCropTransform())
//                            .into(this@load)
//                    Log.e(javaClass.simpleName, "current thread is ${Thread.currentThread().name} ; exception is ${e!!.message}")
//                    this@load.setImageResource(errorRes)
//                    return false
//                }
//
//            })
            .into(this)
}

fun ImageView.load(context: Context, url: String, errorRes: Int, signature: ObjectKey) {
    GlideApp.with(context)
            .load(url)
            .signature(signature)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .apply(RequestOptions.circleCropTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    GlideApp.with(context)
                            .load(errorRes)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.circleCropTransform())
                            .into(this@load)
                    return true
                }

            })
            .into(this)
}

fun ImageView.load(context: Context, bytes: ByteArray) {
    GlideApp.with(context)
            .load(bytes)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
}

fun ImageView.load(context: Context, url: String, completeCallback: () -> Unit) {
    GlideApp.with(context)
            .load(url)
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .onlyRetrieveFromCache(true)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    completeCallback()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    completeCallback()
                    return false
                }
            })
            .into(this)
}

fun ImageView.load(context: Context, id: Int) {
    GlideApp.with(context)
            .load(id)
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(this)

}*/
