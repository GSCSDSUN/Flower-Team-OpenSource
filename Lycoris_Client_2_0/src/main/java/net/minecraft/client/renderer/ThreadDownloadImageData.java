/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadDownloadImageData extends SimpleTexture {
	private static final Logger logger = LogManager.getLogger();
	private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
	private final File cacheFile;
	private final String imageUrl;
	private final IImageBuffer imageBuffer;
	private BufferedImage bufferedImage;
	private Thread imageThread;
	private boolean textureUploaded;
	private static final String __OBFID = "CL_00001049";
	public Boolean imageFound = null;

	public ThreadDownloadImageData(File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation,
			IImageBuffer imageBufferIn) {
		super(textureResourceLocation);
		this.cacheFile = cacheFileIn;
		this.imageUrl = imageUrlIn;
		this.imageBuffer = imageBufferIn;
	}

	private void checkTextureUploaded() {
		if (!this.textureUploaded && this.bufferedImage != null) {
			if (this.textureLocation != null) {
				this.deleteGlTexture();
			}
			TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
			this.textureUploaded = true;
		}
	}

	@Override
	public int getGlTextureId() {
		this.checkTextureUploaded();
		return super.getGlTextureId();
	}

	public void setBufferedImage(BufferedImage bufferedImageIn) {
		this.bufferedImage = bufferedImageIn;
		if (this.imageBuffer != null) {
			this.imageBuffer.skinAvailable();
		}
		this.imageFound = this.bufferedImage != null;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		if (this.bufferedImage == null && this.textureLocation != null) {
			super.loadTexture(resourceManager);
		}
		if (this.imageThread == null) {
			if (this.cacheFile != null && this.cacheFile.isFile()) {
				logger.debug("Loading http texture from local cache ({})", new Object[] { this.cacheFile });
				try {
					this.bufferedImage = ImageIO.read(this.cacheFile);
					if (this.imageBuffer != null) {
						this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
					}
					this.imageFound = this.bufferedImage != null;
				} catch (IOException ioexception) {
					logger.error("Couldn't load skin " + this.cacheFile, (Throwable) ioexception);
					this.loadTextureFromServer();
				}
			} else {
				this.loadTextureFromServer();
			}
		}
	}

	protected void loadTextureFromServer() {
		this.imageThread = new Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet()) {
			private static final String __OBFID = "CL_00001050";

			@Override
			public void run() {
				HttpURLConnection httpurlconnection;
				block10: {
					block11: {
						httpurlconnection = null;
						logger.debug("Downloading http texture from {} to {}", new Object[] {
								ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.cacheFile });
						try {
							httpurlconnection = (HttpURLConnection) new URL(ThreadDownloadImageData.this.imageUrl)
									.openConnection(Minecraft.getMinecraft().getProxy());
							httpurlconnection.setDoInput(true);
							httpurlconnection.setDoOutput(false);
							httpurlconnection.connect();
							if (httpurlconnection.getResponseCode() / 100 == 2)
								break block10;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						httpurlconnection.disconnect();
					}
					ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
					return;
				}
				try {
					BufferedImage bufferedimage;
					if (ThreadDownloadImageData.this.cacheFile != null) {
						FileUtils.copyInputStreamToFile((InputStream) httpurlconnection.getInputStream(),
								(File) ThreadDownloadImageData.this.cacheFile);
						bufferedimage = ImageIO.read(ThreadDownloadImageData.this.cacheFile);
					} else {
						bufferedimage = TextureUtil.readBufferedImage(httpurlconnection.getInputStream());
					}
					if (ThreadDownloadImageData.this.imageBuffer != null) {
						bufferedimage = ThreadDownloadImageData.this.imageBuffer.parseUserSkin(bufferedimage);
					}
					ThreadDownloadImageData.this.setBufferedImage(bufferedimage);
					if (httpurlconnection != null) {
						httpurlconnection.disconnect();
					}
				} catch (Exception exception) {
					block12: {
						try {
							logger.error("Couldn't download http texture", (Throwable) exception);
							if (httpurlconnection == null)
								break block12;
							httpurlconnection.disconnect();
						} catch (Throwable throwable) {
							if (httpurlconnection != null) {
								httpurlconnection.disconnect();
							}
							ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
							throw throwable;
						}
					}
					ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
					return;
				}
				ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
			}
		};
		this.imageThread.setDaemon(true);
		this.imageThread.start();
	}
}
