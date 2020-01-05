package com.xxun.watch.xunfriends.bean;

import java.io.Serializable;

/**
 * @author cuiyufeng
 * @Description: LocationBean
 * @date 2019/1/16 20:26
 */
public class LocationBean implements Serializable {
    int RC;
    long SN;
    String Version;
    int CID;
    LocationPL PL;
    public class LocationPL implements Serializable {
        private String country;//国家
        private String city; //城市
        private String adcode;
        private String mapType;// "1",
        private String poi;
        private String type;// "3",
        private String province;//"上海市",
        private String citycode;//"289",
        private String road;
        private String street;//"漕宝路",
        private String location;//"121.415272,31.170809",
        private String radius; // "80",
        private String desc; //"上海市徐汇区漕宝路369号",
        private String timestamp; //时间戳
        // 多余字段
        private String bldg;
        private String business;//田林,漕河泾,漕宝路",
        private String country_code;
        private String district;//"徐汇区",
        private String street_number;//"369号",
        private String indoor;
        private String floor;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAdcode() {
            return adcode;
        }

        public void setAdcode(String adcode) {
            this.adcode = adcode;
        }

        public String getMapType() {
            return mapType;
        }

        public void setMapType(String mapType) {
            this.mapType = mapType;
        }

        public String getPoi() {
            return poi;
        }

        public void setPoi(String poi) {
            this.poi = poi;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCitycode() {
            return citycode;
        }

        public void setCitycode(String citycode) {
            this.citycode = citycode;
        }

        public String getRoad() {
            return road;
        }

        public void setRoad(String road) {
            this.road = road;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getRadius() {
            return radius;
        }

        public void setRadius(String radius) {
            this.radius = radius;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getBldg() {
            return bldg;
        }

        public void setBldg(String bldg) {
            this.bldg = bldg;
        }

        public String getBusiness() {
            return business;
        }

        public void setBusiness(String business) {
            this.business = business;
        }

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getStreet_number() {
            return street_number;
        }

        public void setStreet_number(String street_number) {
            this.street_number = street_number;
        }

        public String getIndoor() {
            return indoor;
        }

        public void setIndoor(String indoor) {
            this.indoor = indoor;
        }

        public String getFloor() {
            return floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }
    }

    public int getRC() {
        return RC;
    }

    public void setRC(int RC) {
        this.RC = RC;
    }

    public long getSN() {
        return SN;
    }

    public void setSN(long SN) {
        this.SN = SN;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public int getCID() {
        return CID;
    }

    public void setCID(int CID) {
        this.CID = CID;
    }

    public LocationPL getPL() {
        return PL;
    }

    public void setPL(LocationPL PL) {
        this.PL = PL;
    }
}