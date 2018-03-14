package com.udacity.PopularMovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class TrailerItem implements Parcelable
{
    public static final String id_Json        ="id";
    public static final String iso6391_Json   ="iso_639_1";
    public static final String iso31661_Json  ="iso_3166_1";
    public static final String key_Json       ="key";
    public static final String name_Json      ="name";
    public static final String site_Json      ="site";
    public static final String size_Json      ="size";
    public static final String type_Json      ="type";

    private String id;
    private String iso6391;
    private String iso31661;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    protected TrailerItem(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.iso6391 = ((String) in.readValue((String.class.getClassLoader())));
        this.iso31661 = ((String) in.readValue((String.class.getClassLoader())));
        this.key = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.site = ((String) in.readValue((String.class.getClassLoader())));
        this.size = ((int) in.readValue((int.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
    }

    /*
     * No args constructor for use in serialization
     */
    public TrailerItem() {
    }

    public TrailerItem(String id, String iso6391, String iso31661, String key, String name, String site, int size, String type) {
        super();
        this.id       = id;
        this.iso6391  = iso6391;
        this.iso31661 = iso31661;
        this.key      = key;
        this.name     = name;
        this.site     = site;
        this.size     = size;
        this.type     = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso6391() {
        return iso6391;
    }

    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    public String getIso31661() {
        return iso31661;
    }

    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(iso6391);
        dest.writeValue(iso31661);
        dest.writeValue(key);
        dest.writeValue(name);
        dest.writeValue(site);
        dest.writeValue(size);
        dest.writeValue(type);
    }

    public int describeContents() {
        return 0;
    }

    public final static Parcelable.Creator<TrailerItem> CREATOR = new Creator<TrailerItem>() {

        @SuppressWarnings({
                "unchecked"
        })
        public TrailerItem createFromParcel(Parcel in) {
            return new TrailerItem(in);
        }

        public TrailerItem[] newArray(int size) {
            return (new TrailerItem[size]);
        }

    };

}