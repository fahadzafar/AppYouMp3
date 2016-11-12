package com.youtube.data;

public class VideoBean {

	public String VideoId;
	public String Title;
	public String LiveBCContent;
	
	public VideoBean(String iVideoId, String iTitle, String iLiveBCContent) {
		VideoId = iVideoId;
		Title = iTitle;
		LiveBCContent = iLiveBCContent;
	}
}
