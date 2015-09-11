package com.poversee.neon;

/**
 * Created by effendi on 9/10/2015.
 */
public class ListDevice {
    private String deviceName="";
    private String deviceDescription="";
    private String imageUrl="";

    public void setDeviceName (String DeviceName){
        this.deviceName = DeviceName;
    }
    public void setDeviceDescription (String DeviceDescription){
        this.deviceDescription = DeviceDescription;
    }
    public void setImageUrl (String ImageUrl){
        this.imageUrl = ImageUrl;
    }

    public String getDeviceName (){
        return this.deviceName;
    }
    public String getDeviceDescription (){
        return this.deviceDescription;
    }
    public String getImageUrl (){
        return this.imageUrl;
    }
}
