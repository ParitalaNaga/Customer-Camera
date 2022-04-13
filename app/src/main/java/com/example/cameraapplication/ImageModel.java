package com.example.cameraapplication;

import org.json.JSONObject;

public class ImageModel {

    private String deviceos;

    private String labelName;
    private String spoofType;
    private String borderType;
    private String referenceId;
    private String appId;
    private String image;

    public JSONObject getResponseBodyJson() {
        return responseBodyJson;
    }

    public void setResponseBodyJson(JSONObject responseBodyJson) {
        this.responseBodyJson = responseBodyJson;
    }

    JSONObject responseBodyJson;

    public String getDeviceos() {
        return deviceos;
    }

    public void setDeviceos(String deviceos) {
        this.deviceos = deviceos;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getSpoofType() {
        return spoofType;
    }

    public void setSpoofType(String spoofType) {
        this.spoofType = spoofType;
    }

    public String getBorderType() {
        return borderType;
    }

    public void setBorderType(String borderType) {
        this.borderType = borderType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
