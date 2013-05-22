package com.yify.mobile;

import java.io.File;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.graphics.Bitmap.CompressFormat;

public class MobileApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheOnDisc()
		.cacheInMemory()
		.showImageForEmptyUri(R.drawable.default_product)
		.showImageOnFail(R.drawable.default_product)
		.showStubImage(R.drawable.default_product)
		.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.enableLogging()
		.threadPoolSize(3)
		.threadPriority(Thread.NORM_PRIORITY - 1)
		.discCache(new UnlimitedDiscCache(cacheDir))
		.imageDownloader(new BaseImageDownloader(getApplicationContext()))
		.imageDecoder(new BaseImageDecoder())
		.tasksProcessingOrder(QueueProcessingType.FIFO)
		.defaultDisplayImageOptions(options)
		.discCacheSize(50 * 1024 * 1024)
		.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
		.discCacheFileCount(100)
		.denyCacheImageMultipleSizesInMemory()
		.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		.memoryCacheSize(2 * 1024 * 1024)
		.memoryCacheExtraOptions(400,  800)
		.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75)
		.build();
		
		ImageLoader.getInstance().init(config);
		
	}
	
}
