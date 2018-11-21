package com.digipro;

public class Channel {
	private String id;
	private String email;
	private String url;
	private String activeCampaignListId;
	private String buyUrl;
	private String buyPackUrl;

	public String getBuyPackUrl() {
		return buyPackUrl;
	}

	public void setBuyPackUrl(String buyPackUrl) {
		this.buyPackUrl = buyPackUrl;
	}

	public String getBuyUrl() {
		return buyUrl;
	}

	public void setBuyUrl(String buyUrl) {
		this.buyUrl = buyUrl;
	}

	public String getActiveCampaignListId() {
		return activeCampaignListId;
	}

	public void setActiveCampaignListId(String activeCampaignListId) {
		this.activeCampaignListId = activeCampaignListId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
